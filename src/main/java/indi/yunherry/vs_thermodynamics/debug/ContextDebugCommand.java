package indi.yunherry.vs_thermodynamics.debug;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ContextDebugCommand {
    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
//        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
//
//        LiteralArgumentBuilder<CommandSourceStack> setWindCommand = Commands
//                .literal("setLift")
//                .requires(source -> source.hasPermission(2)) // 需要OP权限
//                .then(Commands.argument("lift", DoubleArgumentType.doubleArg())
//                        .executes(ctx -> {
//                            GlobalContext.LIFT = DoubleArgumentType.getDouble(ctx, "lift");
//                            return Command.SINGLE_SUCCESS;
//                        }));
//
//        dispatcher.register(setWindCommand);
    }
}
