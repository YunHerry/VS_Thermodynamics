package indi.yunherry.vs_thermodynamics.handler;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import indi.yunherry.vs_thermodynamics.VSThermodynamics;
import indi.yunherry.vs_thermodynamics.content.scenes.BurnerBlockScenes;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = VSThermodynamics.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PonderHandler {
    static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper("vs_thermodynamics");
    public static void register() {
        BurnerBlockScenes.register(HELPER);
    }

    @SubscribeEvent
    public static void onPonderRegister(FMLClientSetupEvent event) {
        PonderHandler.register();
    }
    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        ResourceProvider provider = event.getResourceProvider();
        event.registerShader(
                new ShaderInstance(provider, new ResourceLocation(VSThermodynamics.MODID, "rendertype_chain"), DefaultVertexFormat.POSITION_COLOR_LIGHTMAP),
                shader -> {
                    CustomRenderTypes.CHAIN_SHADER = shader;
                }
        );
    }

}
