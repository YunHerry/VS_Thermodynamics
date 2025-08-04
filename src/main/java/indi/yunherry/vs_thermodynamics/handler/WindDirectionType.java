package indi.yunherry.vs_thermodynamics.handler;

import indi.yunherry.vs_thermodynamics.WorldContext;
import net.minecraft.util.RandomSource;
import org.joml.Vector3d;

public enum WindDirectionType {
    NORTH,
    WEST,
    EAST,
    SOUTH,
    NONE;
    public static String getWindDirectionType(WindDirectionType windDirection) {
        return switch (windDirection) {
            case NORTH -> "北风";
            case WEST -> "西风";
            case EAST -> "东风";
            case SOUTH -> "南风";
            case NONE -> "无风";
        };
    }
    public static Vector3d getWindDirectionVector(WindDirectionType direction,int maxSize) {
        return switch (direction) {
            case NORTH -> new Vector3d(0, 0, WorldContext.randomSource.nextInt(maxSize));    // 向南
            case SOUTH -> new Vector3d(0, 0, -WorldContext.randomSource.nextInt(maxSize));   // 向北
            case EAST  -> new Vector3d(-WorldContext.randomSource.nextInt(maxSize), 0, 0);   // 向西
            case WEST  -> new Vector3d(WorldContext.randomSource.nextInt(maxSize), 0, 0);    // 向东
            case NONE  -> new Vector3d(0, 0, 0);    // 无风
        };
    }
}
