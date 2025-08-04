package indi.yunherry.vs_thermodynamics;

import com.tterrag.registrate.util.entry.BlockEntry;
import indi.yunherry.vs_thermodynamics.content.block.burner.BurnerBlock;
import indi.yunherry.vs_thermodynamics.content.block.burner.BurnerBlockEntity;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.eventbus.api.IEventBus;
import com.simibubi.create.content.fluids.tank.BoilerHeaters;


import static indi.yunherry.vs_thermodynamics.VSThermodynamics.REGISTRATE;

public class Blocks {
    public static final BlockEntry<BurnerBlock> BURNER = REGISTRATE
            .block("burner", BurnerBlock::new)
            .properties(p -> p
                    .sound(SoundType.NETHERITE_BLOCK)
                    .noOcclusion()
                    .strength(3f))
            .onRegister(b -> BoilerHeaters.registerHeater(b, (world, pos, state) -> {
                if (!(world.getBlockEntity(pos) instanceof BurnerBlockEntity be))
                    return -1; // -1 表示无热
                if (!state.getValue(BurnerBlock.LIT))
                    return -1; // 熄灭时无热
                // 根据实体内部 heat 值返回：0 被动热，>0 为主动热级别
                return be.heat >= 1.8 ? 1 : 0;
            }))
            .simpleItem()
            .register();
    public static void register(IEventBus modEventBus) {

    }

}
