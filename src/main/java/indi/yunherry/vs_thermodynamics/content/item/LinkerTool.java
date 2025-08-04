package indi.yunherry.vs_thermodynamics.content.item;

import indi.yunherry.vs_thermodynamics.content.block.mooringBollard.MooringBollardBlockEntity;
import indi.yunherry.vs_thermodynamics.handler.ClientRenderHooks;
import indi.yunherry.vs_thermodynamics.handler.RopeConstraintData;
import indi.yunherry.vs_thermodynamics.utils.ConnectionPointData;
import indi.yunherry.vs_thermodynamics.utils.ShipUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.joml.*;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.apigame.constraints.VSRopeConstraint;
import org.valkyrienskies.core.apigame.world.ClientShipWorldCore;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;


/**
 * 工具：两次右键铆桩（Mooring Bollard）即可在两端之间创建 VSRopeConstraint。
 * — 第一次记录端点 A<br>
 * — 第二次记录端点 B 并立即生成绳索
 * <p>
 * ⚠ 若点击位置不在任何飞船上，则 shipId = -1，自动视为“地面锚点”。
 */
public class LinkerTool extends Item {
    public LinkerTool(Properties props) {
        super(props);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        if (level.isClientSide) return InteractionResult.SUCCESS;   // 只在服务端执行逻辑

        BlockPos pos = ctx.getClickedPos();
        BlockEntity be = level.getBlockEntity(pos);

        // 这里如果你只允许对 MooringBollard 使用，就取消注释
        // if (!(be instanceof MooringBollardBlockEntity)) return InteractionResult.PASS;

        Player player = ctx.getPlayer();
        ItemStack stack = ctx.getItemInHand();
        CompoundTag tag = stack.getOrCreateTag();

        /* ---------- ① 取得当前端信息 ---------- */
        ServerShip ship = ShipUtils.getShipByPos((ServerLevel) level, pos);   // 可能为 null
        long shipIdCurrent = ship != null ? ship.getId() : -1L;         // 地面 → -1
        int xCurrent = pos.getX(), yCurrent = pos.getY(), zCurrent = pos.getZ();

        /* ---------- ② 第一次点击：只记录 ---------- */
        if (!tag.contains("firstPointSet")) {
            tag.putBoolean("firstPointSet", true);
            tag.putLong("shipId1", shipIdCurrent);
            tag.putInt("x1", xCurrent);
            tag.putInt("y1", yCurrent);
            tag.putInt("z1", zCurrent);

            player.sendSystemMessage(Component.literal("已记录第一个铆桩，继续右键第二个铆桩。"));
            return InteractionResult.SUCCESS;
        }

        /* ---------- ③ 第二次点击：开始创建绳索 ---------- */
        long shipId1 = tag.getLong("shipId1");
        RopeConstraintData ropeConstraintData = new RopeConstraintData();

        BlockPos pos1 = new BlockPos(tag.getInt("x1"), tag.getInt("y1"), tag.getInt("z1"));
        long shipId2 = shipIdCurrent;
        BlockPos pos2 = pos;

        // 不允许同一艘【移动飞船】内部自连，但允许 船↔地面、两艘不同船
        if (shipId1 == shipId2 && shipId1 != -1L) {
            player.sendSystemMessage(Component.literal("不能将铆桩连接到同一艘飞船上！"));
            resetTool(stack);
            return InteractionResult.FAIL;
        }

//        // 取两端 BE，校验类型
//        if (!(level.getBlockEntity(pos1) instanceof MooringBollardBlockEntity hinge1) ||
//                !(level.getBlockEntity(pos2) instanceof MooringBollardBlockEntity hinge2)) {
//            player.sendSystemMessage(Component.literal("目标方块必须都是铆桩！"));
//            resetTool(stack);
//            return InteractionResult.FAIL;
//        }

        Vector3dc sPos1, sPos2;
        ClientShipWorldCore shipObjectWorld = VSGameUtilsKt.getShipObjectWorld(Minecraft.getInstance().level);

        if (shipId1 != -1L) {        // 端点1在飞船上：转局部

            ServerShip ship1 = VSGameUtilsKt.getShipManagingPos((ServerLevel) level, pos1);
            Vector3d world = new Vector3d(pos1.getX() + 0.5, pos1.getY() + 0.5, pos1.getZ() + 0.5);

// ★ 用 RenderTransform 获取 4×4 船→世界矩阵，再求逆
            Vector3d sPos = ship1.getShipToWorld().invert(new Matrix4d())           // 逆：World ➜ Ship
                    .transformPosition(world);        // 乘到局部   :contentReference[oaicite:3]{index=3}
//            sPos1 = VectorConversionsMCKt.toJOMLD(pos1).add(0.5,0.5,0.5).sub(ship1.getInertiaData().getCenterOfMassInShip());
            sPos1 = world;
        } else {
//            shipId1 = makeStaticShip((ServerLevel) level,pos1).getId();
//            System.out.println(shipId1);
            ServerShipWorldCore core = VSGameUtilsKt.getShipObjectWorld((ServerLevel) level);
            shipId1 = core.getDimensionToGroundBodyIdImmutable().get(VSGameUtilsKt.getDimensionId(level));
            ropeConstraintData.setShip1Static(true);

            sPos1 = new Vector3d(pos1.getX() + 0.5, pos1.getY() + 0.5, pos1.getZ() + 0.5);
        }
        if (shipId2 != -1L) {
            ServerShip ship2 = VSGameUtilsKt.getShipManagingPos((ServerLevel) level, pos2);
            Vector3d world = new Vector3d(pos2.getX() + 0.5, pos2.getY() + 0.5, pos2.getZ() + 0.5);
//
//// ★ 用 RenderTransform 获取 4×4 船→世界矩阵，再求逆
//            Vector3d sPos = ship2.getShipToWorld()
//                    .invert(new Matrix4d())           // 逆：World ➜ Ship
//                    .transformPosition(world);        // 乘到局部   :contentReference[oaicite:3]{index=3}
            sPos2 = world;
        } else {
            ropeConstraintData.setShip2Static(true);
            ServerShipWorldCore core = VSGameUtilsKt.getShipObjectWorld((ServerLevel) level);
            shipId2 = core.getDimensionToGroundBodyIdImmutable().get(VSGameUtilsKt.getDimensionId(level));
//            shipId2 = makeStaticShip((ServerLevel) level,pos1).getId();
//            System.out.println(shipId2);
            sPos2 = new Vector3d(pos2.getX() + 0.5, pos2.getY() + 0.5, pos2.getZ() + 0.5);
        }

        /* ---------- ⑤ 在物理世界注册 VSRopeConstraint ---------- */
        ServerShipWorldCore core = VSGameUtilsKt.getShipObjectWorld((ServerLevel) level);
        // 参数：shipIdA, shipIdB, compliance, sPosA, sPosB, maxForce, ropeLength
//        System.out.println(shipId1);
//        System.out.println(shipId2);
//        System.out.println(pos1);
//        System.out.println(pos2);
//        System.out.println(sPos1);
//        System.out.println(sPos2);
        ropeConstraintData.setRopeLength(20.0);
        ropeConstraintData.setRopeConstraint(Pair.of(new ConnectionPointData(shipId1, new Vector3d(pos1.getX() + 0.5, pos1.getY() + 0.5, pos1.getZ() + 0.5)), new ConnectionPointData(shipId2, new Vector3d(pos2.getX() + 0.5, pos2.getY() + 0.5, pos2.getZ() + 0.5))));
//        ClientRenderHooks.ropes.put(ropeConstraintData.getRopeConstraint(), 20.0);
        System.out.println(sPos1.toString() + "       " + sPos2.toString());
        VSRopeConstraint vsRopeConstraint = new VSRopeConstraint(shipId1, shipId2, Float.MIN_VALUE, new Vector3d(pos1.getX() + 0.5, pos1.getY() + 0.5, pos1.getZ() + 0.5), new Vector3d(pos2.getX() + 0.5, pos2.getY() + 0.5, pos2.getZ() + 0.5), Float.MAX_VALUE, 20.0);
        core.createNewConstraint(vsRopeConstraint);
        ClientRenderHooks.ropeConstraints.add(ropeConstraintData);
        player.sendSystemMessage(Component.literal("铆桩已成功连接！"));
        resetTool(stack);
        return InteractionResult.SUCCESS;
    }

    /**
     * 清空手中工具的 NBT
     */
    private void resetTool(ItemStack stack) {
        stack.setTag(new CompoundTag());
    }

    public static ServerShip makeStaticShip(ServerLevel level, BlockPos pos) {
        ServerShipWorldCore core = VSGameUtilsKt.getShipObjectWorld(level);
        ;

        // ① 创建新 ship，类型为 STATIC
        Vector3d vector3d = VectorConversionsMCKt.toJOMLD(pos).add(0.5, 0.5, 0.5);
        Vector3i v = new Vector3i((int) vector3d.x, (int) vector3d.y, (int) vector3d.z);
        ServerShip staticShip = core.createNewShipAtBlock(v, false, 1.0, VSGameUtilsKt.getDimensionId(level));
        // ② 设无限质量，防止被力拖动
//        staticShip.setStatic(true);
//        // ③ 把坐标移到方块中心
//        staticShip.getTransform().get().set(
//                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        return staticShip;
    }
}
