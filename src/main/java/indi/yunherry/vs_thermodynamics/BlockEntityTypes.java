package indi.yunherry.vs_thermodynamics;

import com.simibubi.create.content.kinetics.base.ShaftInstance;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import indi.yunherry.vs_thermodynamics.content.block.burner.BurnerBlockEntity;
import indi.yunherry.vs_thermodynamics.content.block.burner.BurnerRenderer;
import indi.yunherry.vs_thermodynamics.content.block.burner.OneSideShaftInstance;

import static com.simibubi.create.Create.REGISTRATE;

public class BlockEntityTypes {
    public static final BlockEntityEntry<BurnerBlockEntity> BURNER = REGISTRATE.blockEntity("burner", BurnerBlockEntity::new)
            .instance(()-> OneSideShaftInstance::new)
            .validBlocks(Blocks.BURNER)
            .renderer(() -> BurnerRenderer::new)
            .register();

    public static void register() {

    }
}
