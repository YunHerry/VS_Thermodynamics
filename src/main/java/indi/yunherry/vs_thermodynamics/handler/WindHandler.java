package indi.yunherry.vs_thermodynamics.handler;

import indi.yunherry.vs_thermodynamics.content.WindDataPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.concurrent.ThreadLocalRandom;

import static indi.yunherry.vs_thermodynamics.WorldContext.*;

@Mod.EventBusSubscriber
public class WindHandler {
    //对流层
    private final static WindDirectionType[] directionTypes = WindDirectionType.values();
    //平流层
    private final static WindDirectionType[] directionTypes1 = new WindDirectionType[]{WindDirectionType.NORTH, WindDirectionType.EAST, WindDirectionType.SOUTH, WindDirectionType.WEST};

//    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        //对流层风向
        if (ThreadLocalRandom.current().nextInt(1000) >= 998) {
            if (windDirection != WindDirectionType.NONE) {
                windDirection = WindDirectionType.NONE;
            } else {
                windDirection = directionTypes[ThreadLocalRandom.current().nextInt(directionTypes.length)];
                windVec = WindDirectionType.getWindDirectionVector(windDirection,100);
            }
        }
        //平流层风向
        if (ThreadLocalRandom.current().nextInt(10000) >= 9990) {
            WindDirectionType windDirection = directionTypes1[ThreadLocalRandom.current().nextInt(directionTypes1.length)];
            windVec1 = WindDirectionType.getWindDirectionVector(windDirection,400);
        }
        ThermodynamicsNetwork.CHANNEL.send(
                PacketDistributor.ALL.noArg(),
                new WindDataPacket(windDirection, windVec, windVec1)
        );
        ServerLevel serverLevel = server.overworld();

        var ships = VSGameUtilsKt.getAllShips(serverLevel);
//        for (var ship : ships) {
////            ShipForcesInducer shipForcesInducer = ShipUtils.getOrCreateAsAttachment((ServerShip) ship, WindController.class);
//        }
    }
//
//        MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
//        if (server == null) return;
//
//        VSGameEngine vsEngine = VSGameEngine.INSTANCE;
//        if (vsEngine == null) return;
//
//        // 自定义风力方向和强度
//        Vec3d windDirection = new Vec3d(1, 0, 0.5).normalize(); // 东南偏向
//        double windStrength = 8000.0; // 牛顿 (N)，每 tick
//
//        for (Ship ship : vsEngine.getShipManager().getAllLoadedShips()) {
//            // 只在空中或有空气时施加风力，或你可以加更多判断
//            if (ship.isDestroyed()) continue;
//
//            // 获取船的质心位置
//            Vec3d centerOfMass = ship.getTransform().getPositionInWorld();
//
//            // 计算风力向量
//            Vec3d windForce = windDirection.multiply(windStrength);
//
//            // 施加力（注意需要是世界坐标）
//            ship.applyForce(centerOfMass, windForce);
//        }
}
