package indi.yunherry.vs_thermodynamics;

import indi.yunherry.vs_thermodynamics.handler.WindDirectionType;
import net.minecraft.util.RandomSource;
import org.joml.Vector3d;

public class WorldContext {
    //对流层方向
    public static WindDirectionType windDirection = WindDirectionType.NONE;
    //对流层低空大气
    public static Vector3d windVec = new Vector3d();
    //平流层高空稳定大气
    public static Vector3d windVec1 = new Vector3d();
    public static RandomSource randomSource = RandomSource.create();
}
