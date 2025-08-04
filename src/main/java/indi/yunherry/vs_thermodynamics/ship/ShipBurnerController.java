package indi.yunherry.vs_thermodynamics.ship;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import indi.yunherry.vs_thermodynamics.content.block.burner.BurnerApplier;
import indi.yunherry.vs_thermodynamics.utils.ShipUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;

import java.io.Serializable;
import java.util.ArrayList;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
@SuppressWarnings("deprecation")
public class ShipBurnerController implements ShipForcesInducer, Serializable {
    public final ArrayList<BurnerApplier> burners = new ArrayList<>();

    public ShipBurnerController() {
    }

    //@TODO 当前只有一个burner会生效
    //@TODO 要做成多个burner共享热力方块
    public void add(BurnerApplier burner) {
        if (burners.isEmpty()) {
            burners.add(burner);
        }

    }

    public void remove(BlockPos pos, ServerLevel level) {
        burners.clear();
        ShipUtils.getShipByPos(level, pos).saveAttachment(ShipBurnerController.class, null);
    }

    public static ShipBurnerController get(BlockPos pos, ServerLevel level) {
        ServerShip ship = ShipUtils.getShipByPos(level, pos);
        if (ship != null) {
            return ShipUtils.getOrCreateAsAttachment(ship, ShipBurnerController.class);
        }
        return null;
    }

    @Override
    public void applyForces(@NotNull PhysShip physShip) {
//        burners.forEach((k) -> k.applyForces(physShip));
        burners.get(0).applyForces(physShip);
    }
}
