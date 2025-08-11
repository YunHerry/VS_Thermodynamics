package indi.yunherry.vs_thermodynamics.content.block.burner;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import net.minecraft.core.BlockPos;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.concurrent.atomic.AtomicReference;
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class BurnerBlockData {
    private float value;
    private float strength;
    private double shipY;
    public AtomicReference<Double> force = new AtomicReference<>(0d);
    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    private boolean isRunning;
    public AtomicReference<Double> f = new AtomicReference<>(1d);
    private AtomicReference<Integer> airSize = new AtomicReference<>(0);
    @JsonIgnore
    public BlockPos liftCenter = new BlockPos(0,0,0);

    public Vector3dc getTransformLiftCenter() {
        return transformLiftCenter;
    }

    public void setTransformLiftCenter(Vector3dc transformLiftCenter) {
        this.transformLiftCenter = transformLiftCenter;
    }

    public Vector3dc transformLiftCenter;
    public BurnerBlockData() {
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getAirSize() {
        return airSize.getAcquire();
    }

    public void setAirSize(Integer airSize) {
        this.airSize.setRelease(airSize);
    }

    public double getShipY() {
        return shipY;
    }

    public void setShipY(double shipY) {
        this.shipY = shipY;
    }

    public float getStrength() {
        return strength;
    }

    public void setStrength(float strength) {
        this.strength = strength;
    }
}
