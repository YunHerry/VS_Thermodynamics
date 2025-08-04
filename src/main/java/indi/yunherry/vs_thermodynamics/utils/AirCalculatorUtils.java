package indi.yunherry.vs_thermodynamics.utils;

import indi.yunherry.vs_thermodynamics.content.block.burner.BurnerBlockData;
import indi.yunherry.vs_thermodynamics.content.block.burner.BurnerBlockEntity;
import indi.yunherry.vs_thermodynamics.content.block.burner.BurnerRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Vector3d;
import org.joml.primitives.AABBic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AirCalculatorUtils {

    private static final ExecutorService pool = Executors.newFixedThreadPool(4, new ThreadFactory() {
        private final AtomicInteger count = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "my-pool-thread-" + count.getAndIncrement());
            t.setUncaughtExceptionHandler((thread, throwable) -> {
                System.err.println("线程 " + thread.getName() + " 异常: " + throwable);
                throwable.printStackTrace();
                // 这里可以异步通知、日志、告警等
            });
            return t;
        }
    });
    private static final Logger log = LoggerFactory.getLogger(AirCalculatorUtils.class);

    public static void asyncCalculateAirVolume(BurnerBlockEntity blockEntity, ServerShip ship, Level level, BurnerBlockData burnerBlockData, BlockPos burnerPos, int maxAirVolume) {
//        System.out.println(((ThreadPoolExecutor) pool).getQueue().size());
        pool.submit(() -> {
            try {
                AABBic aabb = ship.getShipAABB();
                double shipY = ship.getTransform().getShipToWorld().transformPosition(new Vector3d(ship.getInertiaData().getCenterOfMassInShip())).y;
                burnerBlockData.setShipY(shipY);
                Iterable<BlockPos> iter = BlockPos.betweenClosed(aabb.minX(), aabb.minY(), aabb.minZ(), aabb.maxX(), aabb.maxY(), aabb.maxZ());
                List<BlockPos> blockPosList = new ArrayList<>();
                iter.forEach(item -> {
                    BlockState state = level.getBlockState(item);
                    if (!state.isAir() && !state.hasProperty(BlockStateProperties.WATERLOGGED))

                        blockPosList.add(new BlockPos(item));
                });
                Stream<BlockPos> stream = blockPosList.stream();

                Map<Integer, List<BlockPos>> result = stream.collect(Collectors.groupingBy(BlockPos::getY));
                Set<BlockPos> blockPos = AirCalculatorUtils.calculateNewHotAirVolume(result, new HashSet<>(), burnerPos, 600);
                burnerBlockData.setAirSize(blockPos.size());
//                burnerBlockData.setShipY(burnerData.getAirSize() * GlobalContext.LIFT * calculatePriority(ship.getTransform().getPositionInShip().y()) * burnerData.getValue());
                BlockPos center = AirCalculatorUtils.calculateCenterOfLift(blockPos);
                if (center != null) {
                    BurnerRenderer.liftPos = center;
                    burnerBlockData.liftCenter = center;
                    burnerBlockData.transformLiftCenter = VectorConversionsMCKt.toJOMLD(center).sub(ship.getInertiaData().getCenterOfMassInShip());
//            System.out.println(VectorConversionsMCKt.toJOMLD(center).sub(ship.getInertiaData().getCenterOfMassInShip()));
                    //f_damped(ship.getTransform().getPositionInShip().y(),ship.getVelocity().y())
                }

                burnerBlockData.f.setRelease(f_damped(shipY, ship.getVelocity().y()));
//                log.warn("debug参数: {} {} {}", shipY, ship.getVelocity().y(), f_damped(shipY, ship.getVelocity().y()));
//            log.info("产生升力: {}",ship.getInertiaData().getMass());

                blockEntity.sendData();
                blockEntity.setChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
//            log.info("当前队列数量: {} | burner数量: {}", ((ThreadPoolExecutor) pool).getQueue().size(), ShipBurnerController.burners.size());
        });
    }

    /**
     * 计算新的热空气体积，考虑热源位置和热量限制。
     *
     * @param layeredBlocksMap     分层存储的方块地图，键为Y坐标，值为该层所有方块的位置列表。
     * @param existingHotAirBlocks 已存在的热空气方块集合。
     * @param heatSourcePos        热源（产生热空气的方块）的位置。
     * @param maxHeatBlocks        热源能够产生的最大热空气方块数量。
     * @return 新确认的热空气方块集合。
     */
    public static Set<BlockPos> calculateNewHotAirVolume(Map<Integer, List<BlockPos>> layeredBlocksMap, Set<BlockPos> existingHotAirBlocks, BlockPos heatSourcePos, int maxHeatBlocks) {

        Set<BlockPos> newlyConfirmedHotAirForThisRegion = new HashSet<>();

        // 检查输入
        if (layeredBlocksMap == null || layeredBlocksMap.isEmpty()) {
            return newlyConfirmedHotAirForThisRegion;
        }

        if (!isValidHeatSourcePosition(layeredBlocksMap, heatSourcePos)) {
            // 如果热源位置不合法，则不产生热空气
            return newlyConfirmedHotAirForThisRegion;
        }


        Map<Integer, Set<BlockPos>> solidBlocksByLayer = new HashMap<>();
        for (Map.Entry<Integer, List<BlockPos>> entry : layeredBlocksMap.entrySet()) {
            if (entry.getValue() != null) { // 添加对列表本身的null检查
                solidBlocksByLayer.put(entry.getKey(), new HashSet<>(entry.getValue()));
            } else {
                solidBlocksByLayer.put(entry.getKey(), Collections.emptySet());
            }
        }

        List<Integer> sortedYLevels = solidBlocksByLayer.keySet().stream().sorted(Comparator.reverseOrder()) // 从高Y到低Y处理
                .collect(Collectors.toList());

        if (sortedYLevels.isEmpty()) {
            return newlyConfirmedHotAirForThisRegion;
        }

        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE, maxZ = Integer.MIN_VALUE;
        boolean hasBlocks = false;

        for (int y : sortedYLevels) {
            Set<BlockPos> layerBlocks = solidBlocksByLayer.get(y);
            if (layerBlocks != null && !layerBlocks.isEmpty()) {
                hasBlocks = true;
                for (BlockPos bp : layerBlocks) {
                    if (bp == null) continue;
                    minX = Math.min(minX, bp.getX());
                    maxX = Math.max(maxX, bp.getX());
                    minZ = Math.min(minZ, bp.getZ());
                    maxZ = Math.max(maxZ, bp.getZ());
                }
            }
        }

        if (!hasBlocks) {
            return newlyConfirmedHotAirForThisRegion;
        }

        // 这个集合用于跟踪在当前 calculateNewHotAirVolume 调用中，为当前这个结构确认的热空气。
        // 它也用于帮助判断更低楼层的空气是否被当前结构更高楼层的热空气密封。
        Set<BlockPos> hotAirConfirmedInThisCallInternal = new HashSet<>();

        for (int currentY : sortedYLevels) {
            Set<BlockPos> currentLayerSolidBlocks = solidBlocksByLayer.getOrDefault(currentY, Collections.emptySet());
            Set<BlockPos> layerAboveSolidBlocks = solidBlocksByLayer.getOrDefault(currentY + 1, Collections.emptySet());

            Set<BlockPos> potentialAirCandidatesThisLayer = new HashSet<>();

            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos currentPos = new BlockPos(x, currentY, z);

                    // 1. 当前位置必须是空气 (不属于当前结构的固体方块)
                    if (currentLayerSolidBlocks.contains(currentPos)) {
                        continue;
                    }
                    // 2. 当前位置不能是已存在的热空气方块 (避免重复计算体积)
                    if (existingHotAirBlocks.contains(currentPos)) {
                        continue;
                    }

                    // 3. 检查上方是否密封
                    BlockPos posAbove = new BlockPos(x, currentY + 1, z);
                    boolean isSealedAbove = layerAboveSolidBlocks.contains(posAbove) ||         // 被当前结构的固体块密封
                            hotAirConfirmedInThisCallInternal.contains(posAbove) || // 被当前结构本轮计算中更高层的热空气密封
                            existingHotAirBlocks.contains(posAbove);              // 被已存在的外部热空气密封

                    if (isSealedAbove) {
                        potentialAirCandidatesThisLayer.add(currentPos);
                    }
                }
            }

            Set<BlockPos> visitedInLayerBFS = new HashSet<>();
            for (BlockPos startNode : potentialAirCandidatesThisLayer) {
                if (visitedInLayerBFS.contains(startNode) || newlyConfirmedHotAirForThisRegion.contains(startNode)) {
                    // 如果已在本轮BFS中访问过，或者已被最终确认为此区域热空气，则跳过
                    continue;
                }

                Set<BlockPos> currentComponentMembers = new HashSet<>(); // 用于收集当前连通区域的成员
                Queue<BlockPos> queue = new LinkedList<>();
                boolean componentIsSealed = true;

                queue.add(startNode);
                visitedInLayerBFS.add(startNode); // 标记为在本层BFS的迭代中已访问

                Set<BlockPos> bfsInternalVisitedThisComponent = new HashSet<>(); // 跟踪当前BFS组件的内部访问，防止重复入队
                bfsInternalVisitedThisComponent.add(startNode);


                while (!queue.isEmpty()) {
                    BlockPos currentAirBlock = queue.poll();
                    currentComponentMembers.add(currentAirBlock); // 将实际处理的方块加入当前组件

                    BlockPos[] neighbors = {currentAirBlock.offset(0, 0, -1), // North
                            currentAirBlock.offset(0, 0, 1),  // South
                            currentAirBlock.offset(1, 0, 0),  // East
                            currentAirBlock.offset(-1, 0, 0)  // West
                    };

                    for (BlockPos neighbor : neighbors) {
                        // A. 如果邻居是当前结构的固体方块，视为墙壁
                        if (currentLayerSolidBlocks.contains(neighbor)) {
                            continue;
                        }

                        // B. 如果邻居是已存在的外部热空气，也视为墙壁/密封边界
                        if (existingHotAirBlocks.contains(neighbor)) {
                            continue;
                        }

                        // C. 如果邻居是本轮计算的候选空气之一 (属于同一潜在密封空间)
                        if (potentialAirCandidatesThisLayer.contains(neighbor)) {
                            if (!bfsInternalVisitedThisComponent.contains(neighbor)) {
                                bfsInternalVisitedThisComponent.add(neighbor);
                                visitedInLayerBFS.add(neighbor); // 标记为在本层BFS的迭代中已访问
                                queue.add(neighbor);
                            }
                        }
                        // D. 邻居是空气，不是外部热空气，也不是本轮候选 -> 这是一个泄露点
                        else {
                            componentIsSealed = false;
                            // 可以选择break或继续完成BFS以访问完此泄露组件的所有成员
                        }
                    }
                }

                if (componentIsSealed) {
                    newlyConfirmedHotAirForThisRegion.addAll(currentComponentMembers);
                    hotAirConfirmedInThisCallInternal.addAll(currentComponentMembers); // 也更新内部跟踪器
                }
            }
        }

        // 限制热空气方块数量
        if (newlyConfirmedHotAirForThisRegion.size() > maxHeatBlocks) {
            // 根据距离热源的远近排序，保留最近的方块
            List<BlockPos> sortedByDistance = newlyConfirmedHotAirForThisRegion.stream().sorted(Comparator.comparingDouble(pos -> pos.distSqr(heatSourcePos))).limit(maxHeatBlocks).collect(Collectors.toList());
            newlyConfirmedHotAirForThisRegion = new HashSet<>(sortedByDistance);
        }


        return newlyConfirmedHotAirForThisRegion;
    }


    /**
     * 验证热源位置是否合法。
     * 热源必须满足以下条件：
     * 1. 必须位于倒扣的碗下方（吊篮平台上）。
     * 2. 不能在碗的上方。
     * 3. 不能在碗的左边和右边 (可以通过比较 X,Z 坐标范围来判断)。
     *
     * @param layeredBlocksMap 分层存储的方块地图。
     * @param heatSourcePos    待验证的热源位置。
     * @return 如果热源位置合法则返回 true，否则返回 false。
     */
    private static boolean isValidHeatSourcePosition(Map<Integer, List<BlockPos>> layeredBlocksMap, BlockPos heatSourcePos) {
        if (layeredBlocksMap == null || layeredBlocksMap.isEmpty()) {
            return false;
        }

        int hsX = heatSourcePos.getX();
        int hsY = heatSourcePos.getY();
        int hsZ = heatSourcePos.getZ();

        // Ensure the heat source block itself is part of the map.
        // This check might be redundant if heatSourcePos always comes from iterating layeredBlocksMap keys.
        List<BlockPos> heatSourceLayerItself = layeredBlocksMap.get(hsY);
        if (heatSourceLayerItself == null || !heatSourceLayerItself.contains(heatSourcePos)) {
            return false;
        }

        // Determine the overall top Y, mainly for the search range for the pillar base.
        // If keySet is empty, max will throw an error, but layeredBlocksMap.isEmpty() should catch it.
        // However, adding a specific check for keySet emptiness before calling max is safer.
        if (layeredBlocksMap.keySet().isEmpty()) {
            return false;
        }
        int overallTopY = Collections.max(layeredBlocksMap.keySet());

        // Find the Y-coordinate of the lowest block directly above the heat source (at same X, Z).
        // This block is the base of the "cap" or "roof".
        int pillarBaseY = -1;
        for (int y = hsY + 1; y <= overallTopY; y++) {
            List<BlockPos> currentLayer = layeredBlocksMap.get(y);
            if (currentLayer != null) {
                for (BlockPos b : currentLayer) {
                    if (b.getX() == hsX && b.getZ() == hsZ) {
                        pillarBaseY = y; // Found the lowest block in the pillar above HS
                        break; // Found the base, no need to check higher in this layer
                    }
                }
            }
            if (pillarBaseY != -1) {
                break; // Exit search for pillar base once found in any layer
            }
        }

        // If no block is found above the heat source at the same X,Z, it cannot be a valid heat source.
        if (pillarBaseY == -1) {
            return false;
        }

        // The core condition:
        // There must be at least two full air layers between the heat source and the base of the cap.
        // hsY = heat source
        // hsY + 1 = air layer 1
        // hsY + 2 = air layer 2
        // hsY + 3 = earliest possible Y for pillarBaseY
        return pillarBaseY >= hsY + 3;
    }

    public static BlockPos calculateCenterOfLift(Collection<BlockPos> hotAirPositions) {
        if (hotAirPositions == null || hotAirPositions.isEmpty()) {
            return null;
        }

        double sumX = 0;
        double sumY = 0;
        double sumZ = 0;

        for (BlockPos pos : hotAirPositions) {
            sumX += pos.getX();
            sumY += pos.getY();
            sumZ += pos.getZ();
        }

        int count = hotAirPositions.size();

        double centerX = sumX / count;
        double centerY = sumY / count;
        double centerZ = sumZ / count;
        return new BlockPos((int) Math.round(centerX), (int) Math.round(centerY), (int) Math.round(centerZ));
    }

    public static double f(double x) {
        if (x <= 225) return 1.0;
        return Math.exp(-0.01 * (x - 225)); // 可调的下降速率
    }

    /**
     * 高级版本 - 考虑载具速度的阻尼效果
     * 如果你能获取到载具的垂直速度，这个版本会更加稳定
     *
     * @param currentHeight 当前高度
     * @param targetHeight 目标高度
     * @param verticalVelocity 垂直速度 (向上为正，向下为负)
     * @param transitionZone 过渡区域大小
     * @param minForceRatio 最小推力比例
     * @param dampingFactor 阻尼系数 (建议 0.1-0.3)
     * @return 调整后的推力权重
     */
    /**
     * 计算带有速度阻尼的推力乘数，以防止振荡。
     *
     * @param x                飞船的Y坐标 (高度)。
     * @param verticalVelocity 飞船的垂直速度 (正为向上，负为向下)。
     * @return 最终的推力乘数。
     */
    public static double f_damped(double x, double verticalVelocity) {
        // --- 阻尼参数，这个值需要你反复调试来获得最佳手感 ---
        // 值越大，阻尼效应越强，飞船的“刹车感”越明显。
        // 从一个较小的值开始，比如 0.05。
        final double dampingFactor = 0.05;
        // ----------------------------------------------------

        // 1. 计算基础的、只与高度相关的推力
        double baseMultiplier;
        if (x <= 225) {
            baseMultiplier = 1.0;
        } else {
            baseMultiplier = Math.exp(-0.01 * (x - 225));
        }

        // 2. 计算阻尼项
        // 我们只在飞船向上运动时施加阻尼，防止其上升过冲
        // 如果 verticalVelocity > 0，dampingTerm 为负数，从而减小推力
        double dampingTerm = -dampingFactor * Math.max(0, verticalVelocity);

        // 3. 合并基础推力和阻尼项
        double finalMultiplier = baseMultiplier + dampingTerm;

        // 4. 确保最终结果不会小于0
        return Math.max(0, finalMultiplier);
    }
}