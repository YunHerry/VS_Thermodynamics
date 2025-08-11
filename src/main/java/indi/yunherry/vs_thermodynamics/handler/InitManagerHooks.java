package indi.yunherry.vs_thermodynamics.handler;

import indi.yunherry.vs_thermodynamics.VSThermodynamics;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = VSThermodynamics.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class InitManagerHooks {
    //目前仅支持主世界
    @SubscribeEvent
    public static void onServerStart(ServerAboutToStartEvent evt) {

    }

}
