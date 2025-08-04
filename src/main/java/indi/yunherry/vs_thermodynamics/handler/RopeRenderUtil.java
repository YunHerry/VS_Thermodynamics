package indi.yunherry.vs_thermodynamics.handler;


import com.mojang.blaze3d.vertex.*;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import org.joml.Vector3f;

public final class RopeRenderUtil {

    private static final RenderType TYPE = RenderType.leash();  // 缰绳专用渲染层:contentReference[oaicite:3]{index=3}

    private RopeRenderUtil() {}

    /**
     * 精确复刻 vanilla 的缰绳渲染。
     *
     * @param matrices  PoseStack 已经 push()、且原点在摄像机位置
     * @param buffer    MultiBufferSource
     * @param start     起点相对摄像机坐标
     * @param end       终点相对摄像机坐标
     * @param lightA    起点光照 (packed)
     * @param lightB    终点光照 (packed)
     */
    public static void drawLeash(PoseStack matrices,
                                 MultiBufferSource buffer,
                                 Vector3f start,
                                 Vector3f end,
                                 int lightA,
                                 int lightB) {

        VertexConsumer vc = buffer.getBuffer(TYPE);

        PoseStack.Pose pose = matrices.last();

        float dx = end.x() - start.x();
        float dy = end.y() - start.y();
        float dz = end.z() - start.z();
        int segments = 24;

        for (int i = 0; i < segments; ++i) {
            float t0 = i       / (float) segments;
            float t1 = (i + 1) / (float) segments;

            // 抛物线垂度
            float y0 = dy * t0 - (t0 * t0 + t0) * 0.5f;
            float y1 = dy * t1 - (t1 * t1 + t1) * 0.5f;

            // 插值坐标
            float x0 = dx * t0;
            float z0 = dz * t0;
            float x1 = dx * t1;
            float z1 = dz * t1;

            // 每段上下面片
            putLeashVertex(vc, pose, x0, y0, z0, lightA, 0.0f, i % 2 == 0);
            putLeashVertex(vc, pose, x1, y1, z1, lightB, 0.5f, i % 2 == 0);
            putLeashVertex(vc, pose, x1, y1, z1, lightB, 0.5f, i % 2 != 0);
            putLeashVertex(vc, pose, x0, y0, z0, lightA, 0.0f, i % 2 != 0);
        }
    }

    private static void putLeashVertex(VertexConsumer vc,
                                       PoseStack.Pose pose,
                                       float x, float y, float z,
                                       int light,
                                       float u,
                                       boolean flipNormal) {

        // 原版颜色 (0.5, 0.4, 0.3):contentReference[oaicite:5]{index=5}
        float r = 0.5f, g = 0.4f, b = 0.3f;
        float nx = flipNormal ? -y * 0.025f : y * 0.025f;  // 粗略法线，防止Z-fight
        float ny = flipNormal ? -x * 0.025f : x * 0.025f;
        float nz = 0.0f;

        vc.vertex(pose.pose(), x, y, z)
                .color(r, g, b, 1.0f)
//                .uv(u, 0.0f)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(pose.normal(), nx, ny, nz)
                .endVertex();
    }

    /** 打包光照值 */
    public static int packLight(ClientLevel level, BlockPos pos) {
        int block = level.getBrightness(LightLayer.BLOCK, pos);
        int sky   = level.getBrightness(LightLayer.SKY,   pos);
        return LightTexture.pack(block, sky);
    }
}
