package indi.yunherry.vs_thermodynamics;

import com.jozufozu.flywheel.core.PartialModel;
import net.minecraft.resources.ResourceLocation;


public class ParticleModels {
    public static final PartialModel SMALL_GAUGE_DIAL = model("block/basin_lid/gauge_dial");
    public static final PartialModel BURNER_EMPTY = model("block/burner/empty");

    @SuppressWarnings("*")
    public static PartialModel model(String id){
        return new PartialModel(ResourceLocation.fromNamespaceAndPath(VSThermodynamics.MODID, id));
    }
    public static void init(){}
}
