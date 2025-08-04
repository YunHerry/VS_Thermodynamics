package indi.yunherry.vs_thermodynamics.content.block.mooringBollard;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;

public class MooringBollardBlockEntity extends BlockEntity {
    // === 存储的核心数据 ===
    private long shipId1 = -1L;
    private long shipId2 = -1L;
    // 连接点在各自飞船的局部坐标系中的位置
    private Vector3d localPos1 = null;
    private Vector3d localPos2 = null;
    // 约束在物理世界中的ID
    private int constraintId = -1;
    // 标记是否已经成功创建了约束
    private boolean isLinked = false;

    public MooringBollardBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

//    public MooringBollardBlockEntity(BlockPos pos, BlockState state) {
//        super(MyBlockEntities.HINGE.get(), pos, state);
//    }

    // [新增] 辅助方法：将 Vector3d 写入新的 CompoundTag
    private static CompoundTag writeVector3d(Vector3d vec) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("x", vec.x());
        tag.putDouble("y", vec.y());
        tag.putDouble("z", vec.z());
        return tag;
    }

    // [新增] 辅助方法：从 CompoundTag 读取 Vector3d
    private static Vector3d readVector3d(CompoundTag tag) {
        return new Vector3d(
                tag.getDouble("x"),
                tag.getDouble("y"),
                tag.getDouble("z")
        );
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("isLinked", isLinked);
        if (isLinked) {
            tag.putLong("shipId1", shipId1);
            tag.putLong("shipId2", shipId2);
            // [更正] 使用我们自己的辅助方法来写入 Vector3d
            tag.put("localPos1", writeVector3d(this.localPos1));
            tag.put("localPos2", writeVector3d(this.localPos2));
            tag.putInt("constraintId", constraintId);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.isLinked = tag.getBoolean("isLinked");
        if (this.isLinked) {
            this.shipId1 = tag.getLong("shipId1");
            this.shipId2 = tag.getLong("shipId2");
            // [更正] 使用我们自己的辅助方法来读取 Vector3d
            if (tag.contains("localPos1", 10)) { // 10 is the NBT type for CompoundTag
                this.localPos1 = readVector3d(tag.getCompound("localPos1"));
            }
            if (tag.contains("localPos2", 10)) {
                this.localPos2 = readVector3d(tag.getCompound("localPos2"));
            }
            this.constraintId = tag.getInt("constraintId");
        }
    }

}
