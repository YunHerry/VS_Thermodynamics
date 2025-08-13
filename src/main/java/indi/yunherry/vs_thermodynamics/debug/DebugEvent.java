package indi.yunherry.vs_thermodynamics.debug;


import indi.yunherry.vs_thermodynamics.Items;
import indi.yunherry.vs_thermodynamics.WorldContext;
import indi.yunherry.vs_thermodynamics.handler.WindDirectionType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class DebugEvent {
    @SubscribeEvent
    public static void onRenderWindDirection(RenderGuiEvent.Post event) {
//        if (!WorldContext.isDebugMode) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.screen != null) return;
        if (mc.player == null) return;
        if (!mc.player.getMainHandItem().is(Items.DEBUG_STICK.get())) return;
        Font font = mc.font;
        GuiGraphics guiGraphics = event.getGuiGraphics();
        Component text = Component.literal(String.format("对流层风向: %s", WorldContext.windVec.toString()));
        Component text1 = Component.literal(String.format("平流层风向: %s", WorldContext.windVec1.toString()));
        // 计算屏幕位置（物品栏上方居中）
        int screenWidth = event.getWindow().getGuiScaledWidth();
        // 渲染文本
        guiGraphics.drawString(
                font,
                text,
                (screenWidth - font.width(text)) / 2,
                event.getWindow().getGuiScaledHeight() - 42,
                0xFFFFFF,
                false
        );
        guiGraphics.drawString(
                font,
                text1,
                (screenWidth - font.width(text)) / 2,
                event.getWindow().getGuiScaledHeight() - 52,
                0xFFFFFF,
                false
        );

    }
}
