package indi.yunherry.vs_thermodynamics.content.block.burner;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.core.api.ships.PhysShip;

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
//TODO: 实现能够停船的锚点
public class BurnerApplier {

    private BurnerBlockData burnerData;

    public BurnerApplier() {
    }

    public BurnerApplier(BurnerBlockData burnerData) {
        this.burnerData = burnerData;
    }

    public void applyForces(@NotNull PhysShip physShip) {
        if (burnerData.getAirSize() > 0 && burnerData.isRunning()) {
            physShip.applyInvariantForceToPos(new Vector3d(0, 10000 * burnerData.getAirSize() * burnerData.getValue() * burnerData.f.getAcquire(), 0), burnerData.transformLiftCenter);
        }
    }
}
