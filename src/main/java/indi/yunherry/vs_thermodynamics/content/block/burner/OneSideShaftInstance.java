package indi.yunherry.vs_thermodynamics.content.block.burner;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;

import com.simibubi.create.content.kinetics.base.SingleRotatingInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;

import indi.yunherry.vs_thermodynamics.ParticleModels;
import net.minecraft.core.Direction;


public class OneSideShaftInstance extends SingleRotatingInstance<BurnerBlockEntity> {

    public OneSideShaftInstance(MaterialManager materialManager, BurnerBlockEntity blockEntity) {
        super(materialManager, blockEntity);
    }

    @Override
    protected Instancer<RotatingData> getModel() {
        return getRotatingMaterial().getModel(ParticleModels.BURNER_EMPTY, blockState, Direction.WEST);
    }
}
