package indi.yunherry.vs_thermodynamics;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CreativeTab {
    private static final DeferredRegister<CreativeModeTab> TAB_REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "vs_thermodynamics");
    public static final RegistryObject<CreativeModeTab> CREATIVE_TAB = TAB_REGISTER.register("thermodynamics_creative_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.vs_thermodynamics_creative_tab"))
                    .icon(() -> new ItemStack(Blocks.BURNER.get()))
                    .displayItems((pParameters, output) -> {
                        output.accept(Blocks.BURNER.get());
                        output.accept(Items.DEBUG_STICK.get());
                        output.accept(Items.LINK_STICK.get());
                    })
                    .build());

    public static void register(IEventBus modEventBus) {
        TAB_REGISTER.register(modEventBus);
    }
}
