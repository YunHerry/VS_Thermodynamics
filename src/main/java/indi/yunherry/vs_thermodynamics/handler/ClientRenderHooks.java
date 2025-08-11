package indi.yunherry.vs_thermodynamics.handler;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import indi.yunherry.vs_thermodynamics.VSThermodynamics;
import indi.yunherry.vs_thermodynamics.utils.ConnectionPointData;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.apigame.ships.ClientShipCore;
import org.valkyrienskies.core.apigame.world.ClientShipWorldCore;
import org.valkyrienskies.mod.common.VSClientGameUtils;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ClientRenderHooks {
    // --- CHANGE 1: Use the new Map data structure ---

    public static Map<Pair<ConnectionPointData, ConnectionPointData>, Double> ropeConstraints = new ConcurrentHashMap<>();

//    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent evt) {
        if (evt.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        final Minecraft mc = Minecraft.getInstance();
        // --- CHANGE 2: Check if the new map is empty ---
        if (mc.level == null || ropeConstraints.isEmpty()) return;

        final PoseStack poseStack = evt.getPoseStack();
        final MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        final ClientShipWorldCore shipWorld = VSGameUtilsKt.getShipObjectWorld(mc.level);
        final Vec3 camPos = mc.gameRenderer.getMainCamera().getPosition();

        final VertexConsumer vertexConsumer = bufferSource.getBuffer(CustomRenderTypes.ROPE_TRIANGLES);

        poseStack.pushPose();
        poseStack.translate(-camPos.x, -camPos.y, -camPos.z);

//        ropeConstraints.forEach(item -> {
//            Vector3d posA = getWorldCoordinates(item.getLeft(), shipWorld,item.getLeft().isShip1Static());
//            Vector3d posB = getWorldCoordinates(item.getRight(), shipWorld,item.isShip2Static());
////            System.out.println(posA.toString() + "       " + posB.toString());
//            if (posA != null && posB != null) {
//                renderLeash(poseStack, vertexConsumer, posA, posB, item.getRopeLength());
//            }
//        });

        poseStack.popPose();
        bufferSource.endBatch(CustomRenderTypes.ROPE_TRIANGLES);
    }

    private static Vector3d getWorldCoordinates(ConnectionPointData pointData, ClientShipWorldCore shipWorld, boolean isStatic) {
        if (!isStatic) {
            final ClientShip ship = shipWorld.getLoadedShips().getById(pointData.id());
            return ship.getRenderTransform().getShipToWorld().transformPosition(new Vector3d(pointData.pos()));
        } else {
            return new Vector3d(pointData.pos());
        }
    }

    /**
     * Renders a leash with dynamic physical effects based on its length.
     * The ropeLength is now passed in as a parameter for performance.
     */
    // --- CHANGE 5: Update method signature to accept ropeLength ---
    public static void renderLeash(PoseStack poseStack, VertexConsumer vertexConsumer, Vector3d posA, Vector3d posB, double ropeLength) {
        final Matrix4f matrix = poseStack.last().pose();
        final Minecraft mc = Minecraft.getInstance();

        // --- The ropeLength is now passed in, so we don't calculate it here anymore ---
        // final double ropeLength = posA.distance(posB); // THIS LINE IS REMOVED!

        final double tautLength = 4.0;
        final double stressLength = 25.0;
        final float maxSag = 2.5f;
        final float normalThickness = 0.05f;
        final float stressedThickness = 0.07f;

        final float stressFactor = (float) Mth.clamp((ropeLength - tautLength) / (stressLength - tautLength), 0.0, 1.0);

        final float sagAmount = Mth.lerp(stressFactor, 0.0f, maxSag);
        final float thickness = Mth.lerp(stressFactor, normalThickness, stressedThickness);

        final float base_r = 0.5f;
        final float base_g = 0.4f;
        final float base_b = 0.3f;

        final int segments = 24;
        Vec3 p1_base = new Vec3(posA.x, posA.y, posA.z);
        Vec3 p2_base = new Vec3(posB.x, posB.y, posB.z);

        // The rest of the rendering logic remains identical...
        for (int i = 0; i < segments; ++i) {
            float t1 = (float) i / segments;
            float t2 = (float) (i + 1) / segments;

            Vec3 segmentStart_base = p1_base.lerp(p2_base, t1);
            Vec3 segmentEnd_base = p1_base.lerp(p2_base, t2);

            Vec3 ropeDir = segmentEnd_base.subtract(segmentStart_base);
            if (ropeDir.lengthSqr() < 1.0E-6D) {
                ropeDir = p2_base.subtract(p1_base);
            }
            ropeDir = ropeDir.normalize();

            Vec3 side = ropeDir.cross(new Vec3(0.0, 1.0, 0.0));
            if (side.lengthSqr() < 1.0E-6D) {
                side = ropeDir.cross(new Vec3(1.0, 0.0, 0.0));
            }
            side = side.normalize();

            Vec3 up = ropeDir.cross(side).normalize();

            float sag1 = -sagAmount * Mth.sin(t1 * Mth.PI);
            float sag2 = -sagAmount * Mth.sin(t2 * Mth.PI);
            Vec3 segmentStart = segmentStart_base.add(0, sag1, 0);
            Vec3 segmentEnd = segmentEnd_base.add(0, sag2, 0);

            Vec3 mid = segmentStart.lerp(segmentEnd, 0.5f);
            int light = LevelRenderer.getLightColor(mc.level, BlockPos.containing(mid));

            float r = base_r;
            float g = base_g;
            float b = base_b;
            if (i % 2 == 0) {
                r *= 0.7f;
                g *= 0.7f;
                b *= 0.7f;
            }

            Vec3 sideOffset = side.scale(thickness);
            Vec3 upOffset = up.scale(thickness);

            Vec3 v1 = segmentStart.add(sideOffset);
            Vec3 v2 = segmentEnd.add(sideOffset);
            Vec3 v3 = segmentEnd.subtract(sideOffset);
            Vec3 v4 = segmentStart.subtract(sideOffset);

            Vec3 v5 = segmentStart.add(upOffset);
            Vec3 v6 = segmentEnd.add(upOffset);
            Vec3 v7 = segmentEnd.subtract(upOffset);
            Vec3 v8 = segmentStart.subtract(upOffset);

            addVertex(vertexConsumer, matrix, v1, r, g, b, light);
            addVertex(vertexConsumer, matrix, v2, r, g, b, light);
            addVertex(vertexConsumer, matrix, v3, r, g, b, light);
            addVertex(vertexConsumer, matrix, v3, r, g, b, light);
            addVertex(vertexConsumer, matrix, v4, r, g, b, light);
            addVertex(vertexConsumer, matrix, v1, r, g, b, light);

            addVertex(vertexConsumer, matrix, v5, r, g, b, light);
            addVertex(vertexConsumer, matrix, v6, r, g, b, light);
            addVertex(vertexConsumer, matrix, v7, r, g, b, light);
            addVertex(vertexConsumer, matrix, v7, r, g, b, light);
            addVertex(vertexConsumer, matrix, v8, r, g, b, light);
            addVertex(vertexConsumer, matrix, v5, r, g, b, light);
        }
    }

    private static void addVertex(VertexConsumer consumer, Matrix4f matrix, Vec3 pos, float r, float g, float b, int light) {
        consumer.vertex(matrix, (float) pos.x, (float) pos.y, (float) pos.z).color(r, g, b, 1.0F).uv2(light).endVertex();
    }
}