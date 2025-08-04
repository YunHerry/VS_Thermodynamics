package indi.yunherry.vs_thermodynamics.ship;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import indi.yunherry.vs_thermodynamics.content.block.burner.BurnerApplier;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.PhysShip;
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BurnerApplier.class, name = "burner"),
        // 如果你有其他实现类，在这里添加
        // @JsonSubTypes.Type(value = OtherApplierImpl.class, name = "other")
})
public interface IBurnerApplier {
    void applyForces(@NotNull PhysShip physShip);
}
