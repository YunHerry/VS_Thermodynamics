package indi.yunherry.vs_thermodynamics.handler;

import indi.yunherry.vs_thermodynamics.utils.ConnectionPointData;
import org.apache.commons.lang3.tuple.Pair;

public class RopeConstraintData {
    private Pair<ConnectionPointData, ConnectionPointData> ropeConstraint;
    private Double ropeLength;
    private boolean isShip1Static;
    private boolean isShip2Static;

    public RopeConstraintData() {

    }

    public boolean isShip1Static() {
        return isShip1Static;
    }

    public void setShip1Static(boolean ship1Static) {
        isShip1Static = ship1Static;
    }

    public boolean isShip2Static() {
        return isShip2Static;
    }

    public void setShip2Static(boolean ship2Static) {
        isShip2Static = ship2Static;
    }

    public Pair<ConnectionPointData, ConnectionPointData> getRopeConstraint() {
        return ropeConstraint;
    }

    public void setRopeConstraint(Pair<ConnectionPointData, ConnectionPointData> ropeConstraint) {
        this.ropeConstraint = ropeConstraint;
    }

    public Double getRopeLength() {
        return ropeLength;
    }

    public void setRopeLength(Double ropeLength) {
        this.ropeLength = ropeLength;
    }
}
