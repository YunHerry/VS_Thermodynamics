package indi.yunherry.vs_thermodynamics.utils;

import indi.yunherry.vs_thermodynamics.ship.ShipBurnerController;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;

public class ShipUtils {
    //创建实例以后就会执行
    public static <T> T getOrCreateAsAttachment(ServerShip ship,Class<T> clazz){
        T attachment = ship.getAttachment(clazz);
        if (attachment == null) {
            try {
                attachment = clazz.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            ship.saveAttachment(clazz, attachment);
        }
        return attachment;
    }
    public static ServerShip getShipByPos(ServerLevel serverLevel, BlockPos pos){
        ServerShip ship = VSGameUtilsKt.getShipObjectManagingPos(serverLevel, pos);
        if (ship == null){
            ship = VSGameUtilsKt.getShipManagingPos(serverLevel, pos);
        }
        return ship;
    }
}
