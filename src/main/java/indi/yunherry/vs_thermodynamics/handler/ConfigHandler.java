package indi.yunherry.vs_thermodynamics.handler;

import indi.yunherry.vs_thermodynamics.config.ThermodynamicsConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import net.minecraft.world.InteractionResult;

public class ConfigHandler {
    public static InteractionResult saveListener(ConfigHolder<ThermodynamicsConfigData> modConfigConfigHolder,ThermodynamicsConfigData  modConfig) {
//        if (config.biomeTint != previousBiomeTintOption || config.ripple.useResourcepackResolution != previousUseResolutionOption || config.ripple.resolution != previousResolutionOption) {
//            Minecraft.getInstance().reloadResourcePacks();
//        }
        return InteractionResult.PASS;
    }
}
