package indi.yunherry.vs_thermodynamics.content.block.burner;

import com.simibubi.create.content.kinetics.base.HorizontalAxisKineticBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import indi.yunherry.vs_thermodynamics.BlockEntityTypes;
import indi.yunherry.vs_thermodynamics.ship.ShipBurnerController;
import indi.yunherry.vs_thermodynamics.utils.ShipUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class BurnerBlock extends HorizontalAxisKineticBlock implements IBE<BurnerBlockEntity> {
    public static EnumProperty<BlazeBurnerBlock.HeatLevel> HEAT_LEVEL = BlazeBurnerBlock.HEAT_LEVEL;
    public static BooleanProperty LIT = BlockStateProperties.LIT;

    public BurnerBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.NONE).setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HEAT_LEVEL, LIT);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return box(1, 0, 1, 15, 12, 15);
    }

    @Override
    public Class<BurnerBlockEntity> getBlockEntityClass() {
        return BurnerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BurnerBlockEntity> getBlockEntityType() {
        return BlockEntityTypes.BURNER.get();
    }
    @SuppressWarnings("deprecation")
    @Override
    public void entityInside(BlockState p_60495_, Level p_60496_, BlockPos p_60497_, Entity p_60498_) {
        super.entityInside(p_60495_, p_60496_, p_60497_, p_60498_);
        if (!p_60496_.isClientSide) {
            BlockEntity blockEntity = p_60496_.getBlockEntity(p_60497_);
            if (blockEntity instanceof BurnerBlockEntity be) {
                if (!be.tank.getFluid().isEmpty() && be.valveState != 0) {
                    p_60498_.setSecondsOnFire(15);
                }
            }
        }
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!worldIn.isClientSide()) {
            ServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerLevel) worldIn, pos);
            BurnerBlockEntity burnerEntity = (BurnerBlockEntity) worldIn.getBlockEntity(pos);
            if (ship != null) {
                BurnerBlockData burnerBlockData = burnerEntity.getBurnerData();
                burnerBlockData.setStrength(1000);
                burnerBlockData.setValue(0);
                burnerBlockData.setAirSize(0);
                burnerBlockData.setShipY(0);
                ShipBurnerController burnerController = ShipUtils.getOrCreateAsAttachment(ship, ShipBurnerController.class);
                BurnerApplier burnerApplier = new BurnerApplier(burnerBlockData);
                burnerController.add(burnerApplier);
            }
        }
        super.onPlace(state, worldIn, pos, oldState, isMoving);
    }

    @Override
    public void onRemove(BlockState pState, Level level, BlockPos pos, BlockState pNewState, boolean pIsMoving) {
        if (!level.isClientSide()) {
            ShipBurnerController shipBurnerController = ShipBurnerController.get(pos, (ServerLevel) level);
            if (shipBurnerController != null) {
                shipBurnerController.remove(pos, (ServerLevel) level);

            }
        }
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity != null) ((SmartBlockEntity) entity).destroy();
        super.onRemove(pState, level, pos, pNewState, pIsMoving);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        if (state.getValue(BlockStateProperties.HORIZONTAL_AXIS) == Direction.Axis.X) {
            return face == Direction.EAST;
        }
        return face == Direction.NORTH;
    }
}