package indi.yunherry.vs_thermodynamics.content.block.mooringBollard;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MooringBollardBlock extends Block implements EntityBlock {
    public MooringBollardBlock(Properties properties) {
        super(properties);
    }

    // 当方块被移除时，我们需要清理约束
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
//        if (!level.isClientSide) {
//            BlockEntity blockEntity = level.getBlockEntity(pos);
//            if (blockEntity instanceof HingeBlockEntity hinge) {
//                hinge.removeConstraint(level); // 自定义方法，用于移除物理约束
//            }
//        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return null;
    }
}
