package indi.yunherry.vs_thermodynamics.ship;

import com.google.common.util.concurrent.AtomicDouble;
import indi.yunherry.vs_thermodynamics.WorldContext;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WindController implements ShipForcesInducer {
    private static final ExecutorService pool = Executors.newFixedThreadPool(4);
    private AtomicDouble shipY = new AtomicDouble();

    @Override
    public void applyForces(@NotNull PhysShip physShip) {
//        pool.submit(() -> {
//            shipY.lazySet(physShip.getTransform().getShipToWorld().transformPosition(new Vector3d(physShip.getCenterOfMass())).y);
//        });
//        double physShipY = shipY.get();
//        if (physShipY == 0) return;
//        if (physShipY > 100) {
//            physShip.applyInvariantForce(WorldContext.windVec1);
//            return;
//        }
//        physShip.applyInvariantForce(WorldContext.windVec);
    }
}
