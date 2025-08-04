package indi.yunherry.vs_thermodynamics.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import indi.yunherry.vs_thermodynamics.VSThermodynamics;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class DebugCommand {
    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("thermodynamics")
                .requires(source -> source.hasPermission(2)) // 需要权限等级为 2（OP）
                .then(Commands.literal("debug")
                        .then(Commands.literal("enable")
                                .executes(context -> {
                                    VSThermodynamics.DEBUG_MODE = true;
                                    context.getSource().sendSuccess(() -> Component.literal("调试模式已启用"), true);
                                    return 1;
                                })
                        )
                        .then(Commands.literal("disable")
                                .executes(context -> {
                                    VSThermodynamics.DEBUG_MODE = false;
                                    context.getSource().sendSuccess(() -> Component.literal("调试模式已禁用"), true);
                                    return 1;
                                })
                        )
                )
        );
    }
}
