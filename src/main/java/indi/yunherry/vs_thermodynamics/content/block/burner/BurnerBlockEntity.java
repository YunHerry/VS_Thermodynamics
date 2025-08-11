package indi.yunherry.vs_thermodynamics.content.block.burner;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.fluid.SmartFluidTank;


import com.simibubi.create.foundation.utility.Lang;
import indi.yunherry.vs_thermodynamics.utils.AirCalculatorUtils;
import indi.yunherry.vs_thermodynamics.utils.ShipUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.apigame.constraints.VSRopeConstraint;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
import org.valkyrienskies.mod.api.ValkyrienSkies;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;


import java.util.*;

public class BurnerBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation {
    public float heat = -1;
    SmartFluidTank tank = new SmartFluidTank(1000, this::onFluidStackChanged);

    public float valveState = 0f;
    public float prevValveState;
    int tick;
    float multiplier;
    boolean ignited = false;
    int ignitionTries;
    private BurnerBlockData burnerData = new BurnerBlockData();

    @Override
    public void saveToItem(@NotNull ItemStack p_187477_) {
        super.saveToItem(p_187477_);
    }

    @Override
    public void tick() {
        tick = (tick + 1) % 40000;
        super.tick();
        burnerData.setRunning(false);
        prevValveState = valveState;
        valveState = Mth.clamp(valveState + getSpeed() / 5000, 0, 1);
        burnerData.setValue(valveState);
        //最大上限
        float multiplier = 0;
        //液体不为空
        boolean containsValidFuel = !tank.getFluid().isEmpty();
        if (containsValidFuel) multiplier = this.multiplier;
        if (multiplier == 0) containsValidFuel = false;
//        if (valveState == 0 || !containsValidFuel) {
//            heat = -1;
//            if (!level.isClientSide) {
//                if (ignited)
//                    level.playSound(null, worldPosition, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.3f, level.getRandom().nextFloat() * 0.4F + 0.7F);
//
//                ignited = false;
//                ignitionTries = 0;
//            }
//        }
        //如果存在燃料,同时阀门是开的
        //if(containsValidFuel && valveState != 0) {

        if (containsValidFuel && valveState != 0) {
            heat = (valveState + 1) * multiplier;
            if (level.isClientSide) return;
            if (tick % 5 == 0) {
                if (!ignited) {
                    level.playSound(null, worldPosition, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.5f, level.getRandom().nextFloat() * 0.4F + 0.7F);
                    ignitionTries++;
                    if (ignitionTries > 4) ignited = true;
                }
            }
            //应该阀门大小决定消耗
            if (tank.drain(50, IFluidHandler.FluidAction.EXECUTE) != null) {
                burnerData.setRunning(true);
            }
            sendData();
            setChanged();
            ServerShip ship = ShipUtils.getShipByPos((ServerLevel) level, this.getBlockPos());
            if (ship != null) {
//                log.info("当前高度: {}",ship.getTransform().getShipToWorld().transformPosition(new Vector3d(ship.getInertiaData().getCenterOfMassInShip())).y);
                AirCalculatorUtils.asyncCalculateAirVolume(this, ship, level, this.burnerData, this.getBlockPos(), 500);
            }
        }
        if (level.isClientSide) return;

        if (getBlockState().getValue(BurnerBlock.HEAT_LEVEL) != calculateHeatLevel(heat)) {
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(BurnerBlock.HEAT_LEVEL, calculateHeatLevel(heat)).setValue(BurnerBlock.LIT, heat > 0));
            notifyUpdate();
        }

    }

    int x = 0;
    int y = 0;

    @Override
    public void tickAudio() {
        super.tickAudio();
        if (valveState == 0 || heat == -1) return;
        RandomSource random = RandomSource.create();
        if (tick % 100 == 0)
            level.playLocalSound(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 0.3f, 1f, true);

        x = (x + 1) % 4;
        if (x == 3) y = (y + 1) % 4;

        if (!(x == 0 || x == 3 || y == 0 || y == 3)) return;
        if (heat >= 1.8f && random.nextInt(0, (int) (1 + heat * 2)) != 1) {
            level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, worldPosition.getX() + 0.3475 + (double) x / 9, worldPosition.getY() + 1.1, worldPosition.getZ() + 0.3475 + (double) y / 9, 0, 0.02 * valveState, 0);
            return;
        }
        level.addParticle(ParticleTypes.FLAME, worldPosition.getX() + 0.3475 + (float) x / 9, worldPosition.getY() + 1.1, worldPosition.getZ() + 0.3475 + (float) y / 9, 0, 0.02 * valveState, 0);

        level.addParticle(ParticleTypes.SMOKE, worldPosition.getX() + 0.3475 + (float) x / 9, worldPosition.getY() + 1.1, worldPosition.getZ() + 0.3475 + (float) y / 9, 0, 0.02 * valveState, 0);


    }

    Fluid lastFluid;

    void onFluidStackChanged(FluidStack stack) {
        if (lastFluid != stack.getFluid())
            //燃烧强度决定最大上限
//            multiplier = FuelTypeManager.getBurnerStrength(tank.getFluid().getRawFluid());
            multiplier = 50;
        lastFluid = stack.getFluid();
    }

    public BlazeBurnerBlock.HeatLevel calculateHeatLevel(float heat) {
        if (heat >= 1.8) return BlazeBurnerBlock.HeatLevel.SEETHING;
        if (heat >= 1.4) return BlazeBurnerBlock.HeatLevel.KINDLED;
        if (heat >= 1.2) return BlazeBurnerBlock.HeatLevel.FADING;
        if (heat >= 1) return BlazeBurnerBlock.HeatLevel.SMOULDERING;
        return BlazeBurnerBlock.HeatLevel.NONE;
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        compound.putFloat("ValveState", valveState);
        compound.putFloat("Heat", heat);
        compound.putInt("Tick", tick);
        compound.put("FluidContent", tank.writeToNBT(new CompoundTag()));
        compound.putFloat("value", burnerData.getValue());
        compound.putFloat("strength", burnerData.getStrength());
        compound.putDouble("shipY", burnerData.getShipY());
        compound.putFloat("airSize", burnerData.getAirSize());
        compound.putDouble("f", burnerData.f.getAcquire());
        compound.putBoolean("isRunning", burnerData.isRunning());

        super.write(compound, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        valveState = compound.getFloat("ValveState");
        heat = compound.getFloat("Heat");
        tick = compound.getInt("Tick");
        tank.readFromNBT(compound.getCompound("FluidContent"));
        burnerData.setValue(compound.getFloat("value"));
        burnerData.setStrength(compound.getFloat("strength"));
        burnerData.setShipY(compound.getFloat("shipY"));
        burnerData.setAirSize(compound.getInt("airSize"));
        burnerData.f.setRelease(compound.getDouble("f"));
        burnerData.setRunning(compound.getBoolean("isRunning"));
        super.read(compound, clientPacket);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) return LazyOptional.of(() -> tank).cast();
        return super.getCapability(cap, side);
    }

    public BurnerBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    public BurnerBlockData getBurnerData() {
        return burnerData;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {

        Lang.builder().add(Lang.translate("gui.goggles.vs_thermodynamics.value", new Object[0])).space().add(Lang.number(burnerData.getValue())).add(Lang.text("%")).style(ChatFormatting.RED).forGoggles(tooltip);
        //Efficiency
        double lift = burnerData.getShipY();
        Lang.builder().add(Lang.text("当前高度: ")).add(Lang.number(lift)).add(Lang.text("")).style(ChatFormatting.GREEN).forGoggles(tooltip);
        Lang.builder().add(Lang.text("热空气效率")).forGoggles(tooltip);
        //效率
        double efficient = burnerData.f.getAcquire();

        Lang.builder().add(Lang.text(makeObstructionBar(5,((int) (efficient*50 / 10))))).add(Lang.text(getEfficientLevel(efficient))).style(ChatFormatting.GREEN).forGoggles(tooltip);
        if (burnerData.getAirSize() != 0) {
            Lang.builder().add(Lang.text("提供升力: ")).add(Lang.number(10000 * burnerData.getAirSize() * burnerData.getValue() * burnerData.f.getAcquire())).add(Lang.text("")).style(ChatFormatting.GREEN).forGoggles(tooltip);
            int hotAir = (int) burnerData.getAirSize();

            Lang.builder().add(Lang.text("Hot Air: ")).add(Lang.number(hotAir)).style(ChatFormatting.GOLD).forGoggles(tooltip);
        }
        return true;
    }

    public static String makeObstructionBar(int length, int filledLength) {
        String bar = " ";
        int i;
        for (i = 0; i < length; ++i) {
            bar = bar + "▒";
        }

        for (i = 0; i < filledLength - length; ++i) {
            bar = bar + "█";
        }
        return bar + " ";
    }
    public static String getEfficientLevel(double efficient) {
        double level = ((int) (efficient*50 / 10));
        if(level>3.5) {
          return String.format(" 高 (%d%)", (int)efficient*100);
        }
        if (level>2) {
            return String.format(" 中 (%d%)", (int)efficient*100);
        }
        if (level>0) {
            return String.format(" 低 (%d%)", (int)efficient*100);
        }
        return "计算空气效率错误";
    }
}