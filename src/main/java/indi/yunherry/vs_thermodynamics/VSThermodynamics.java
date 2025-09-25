package indi.yunherry.vs_thermodynamics;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import indi.yunherry.vs_thermodynamics.config.ConfigManager;
import indi.yunherry.vs_thermodynamics.config.ThermodynamicsConfigData;
import indi.yunherry.vs_thermodynamics.content.ConfigSyncPacket;
import indi.yunherry.vs_thermodynamics.handler.ConfigHandler;
import indi.yunherry.vs_thermodynamics.handler.ThermodynamicsNetwork;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.PacketDistributor;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;

import static indi.yunherry.vs_thermodynamics.Items.ITEMS;

@Mod(VSThermodynamics.MODID)
public class VSThermodynamics {
    public static final String MODID = "vs_thermodynamics";
    public static boolean DEBUG_MODE = false;
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);
    public VSThermodynamics() {
        this(FMLJavaModLoadingContext.get());
    }
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
        if (ModList.get().isLoaded("cloth_config")) {
            AutoConfig.register(ThermodynamicsConfigData.class, JanksonConfigSerializer::new);
            ConfigManager.CONFIG = AutoConfig.getConfigHolder(ThermodynamicsConfigData.class).getConfig();
            AutoConfig.getConfigHolder(ThermodynamicsConfigData.class).registerSaveListener(ConfigHandler::saveListener);
        }
    }
    public static boolean isDebugLevel() {
        return "debug".equals(System.getProperty("forge.logging.console.level"));
    }
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ThermodynamicsNetwork::register);
        if (ModList.get().isLoaded("cloth_config")) {
            ModLoadingContext.get().registerExtensionPoint(
                    ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory(
                            (mc, screen) -> AutoConfig.getConfigScreen(ThermodynamicsConfigData.class, screen).get()
                    )
            );
        } else {
            ConfigManager.CONFIG = new  ThermodynamicsConfigData();
        }
        if (isDebugLevel()) {
            VSThermodynamics.DEBUG_MODE = true;
            Configurator.setLevel("Burger Factory", org.apache.logging.log4j.Level.OFF);
            Configurator.setLevel("org.valkyrienskies.core.impl.shadow.Ej", org.apache.logging.log4j.Level.OFF);
            Configurator.setLevel("or.va.co.im.ne.NetworkChannel", org.apache.logging.log4j.Level.OFF);
        }

    }
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        // 只在服务器端处理
        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof ServerPlayer player) {
            LOGGER.info("Player {} joined server, sending config data", player.getName().getString());

            // 稍微延迟发送，确保客户端完全准备好
            player.server.execute(() -> {
                ThermodynamicsNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),new ConfigSyncPacket(ConfigManager.CONFIG));
            });
        }
    }
}
