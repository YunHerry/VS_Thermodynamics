package indi.yunherry.vs_thermodynamics;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import indi.yunherry.vs_thermodynamics.handler.ThermodynamicsNetwork;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;

import static indi.yunherry.vs_thermodynamics.Items.ITEMS;

@Mod(VSThermodynamics.MODID)
public class VSThermodynamics {
    public static final String MODID = "vs_thermodynamics";
    public static boolean DEBUG_MODE = false;
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);
    public VSThermodynamics(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener(this::commonSetup);
        Blocks.register(modEventBus);
        ITEMS.register(modEventBus);
        CreativeTab.register(modEventBus);
        ParticleModels.init();
        BlockEntityTypes.register();
        MinecraftForge.EVENT_BUS.register(this);
        REGISTRATE.registerEventListeners(modEventBus);
    }
    public static boolean isDebugLevel() {
        return "debug".equals(System.getProperty("forge.logging.console.level"));
    }
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ThermodynamicsNetwork::register);
        if (isDebugLevel()) {
            VSThermodynamics.DEBUG_MODE = true;
            Configurator.setLevel("Burger Factory", org.apache.logging.log4j.Level.OFF);
            Configurator.setLevel("org.valkyrienskies.core.impl.shadow.Ej", org.apache.logging.log4j.Level.OFF);
            Configurator.setLevel("or.va.co.im.ne.NetworkChannel", org.apache.logging.log4j.Level.OFF);
        }
    }
}
