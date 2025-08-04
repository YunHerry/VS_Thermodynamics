package indi.yunherry.vs_thermodynamics;

import indi.yunherry.vs_thermodynamics.content.item.DebugStickItem;
import indi.yunherry.vs_thermodynamics.content.item.LinkerTool;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static indi.yunherry.vs_thermodynamics.VSThermodynamics.MODID;


public class Items {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> DEBUG_STICK = ITEMS.register("debug_stick",
            () -> new DebugStickItem(new Item.Properties()));
    public static final RegistryObject<Item> LINK_STICK = ITEMS.register("link_stick",
            () -> new LinkerTool(new Item.Properties()));
    public static void register() {

    }
}
