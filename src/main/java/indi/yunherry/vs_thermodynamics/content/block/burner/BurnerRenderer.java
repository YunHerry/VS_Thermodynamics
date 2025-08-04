package indi.yunherry.vs_thermodynamics.content.block.burner;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.HorizontalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import indi.yunherry.vs_thermodynamics.ParticleModels;
import indi.yunherry.vs_thermodynamics.VSThermodynamics;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;


import static net.minecraft.world.level.block.NetherPortalBlock.AXIS;


public class BurnerRenderer extends ShaftRenderer<BurnerBlockEntity> {
    public static BlockPos liftPos = null;

    public BurnerRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(BurnerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        BlockState state = be.getBlockState();
        //仪表盘
        float rotation = Mth.lerp(Mth.lerp(partialTicks, be.prevValveState, be.valveState), -45, 45);
        Direction.Axis axis = state.getValue(HorizontalAxisKineticBlock.HORIZONTAL_AXIS);
        SuperByteBuffer gauge = CachedBufferer.partial(ParticleModels.SMALL_GAUGE_DIAL, state);
        gauge.centre()                                         // 在模型中心定位
                .rotateY(axis == Direction.Axis.X ? 90 : 0)      // 如果 X 轴朝向，旋转 90°
                .unCentre()                                       // 回到原点
                .translate(0.9f, 0.35f, 0.5f)                    // 平移到方块表面
                .rotateX(-rotation)                                // 绕 X 轴倾斜显示刻度
                .renderInto(ms, buffer.getBuffer(RenderType.solid()));  // 绘制至缓冲区
        // 渲染第二个仪表（反向旋转）正面的
        gauge = CachedBufferer.partial(ParticleModels.SMALL_GAUGE_DIAL, state);
        gauge.centre().rotateY(axis == Direction.Axis.X ? 90 : 0).unCentre().translate(0.1f, 0.35f, 0.5f).rotateX(rotation).renderInto(ms, buffer.getBuffer(RenderType.solid()));

        gauge = CachedBufferer.partial(ParticleModels.SMALL_GAUGE_DIAL, state);
        // x控制前后 z控制的是左右 z=0.4时最小 z=时最大
        gauge.centre().rotateY(axis == Direction.Axis.X ? 0 : 90).unCentre().translate(0.08f, 0.3f, 0.58f).renderInto(ms, buffer.getBuffer(RenderType.solid()));

        Direction.Axis axiss = state.getValue(AXIS);
        Direction shaftDirection = axiss == Direction.Axis.X ? Direction.EAST : Direction.NORTH;
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        SuperByteBuffer superBuffer = CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, state, shaftDirection);
        standardKineticRotationTransform(superBuffer, be, light).renderInto(ms, vb);
    }


}