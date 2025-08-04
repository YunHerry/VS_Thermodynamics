package indi.yunherry.vs_thermodynamics.handler;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

public class CustomRenderTypes extends RenderType {
    // This is a necessary but unused constructor for extension.
    public CustomRenderTypes(String s, VertexFormat v, VertexFormat.Mode m, int i, boolean b, boolean b2, Runnable r, Runnable r2) {
        super(s, v, m, i, b, b2, r, r2);
    }
    public static ShaderInstance CHAIN_SHADER;
    /**
     * A custom render type to draw ropes/cables using individual triangles.
     * This avoids Z-fighting and sorting issues inherent with multiple transparent triangle strips.
     */
    public static final RenderType ROPE_TRIANGLES = create("rope_triangles", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, VertexFormat.Mode.TRIANGLES, // Use TRIANGLES instead of TRIANGLE_STRIP
            256, false, true, // Enable transparency
            CompositeState.builder().setShaderState(RENDERTYPE_LEASH_SHADER) // Use existing leash shader
                    .setTextureState(NO_TEXTURE).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setTransparencyState(TRANSLUCENT_TRANSPARENCY) // Standard transparency
                    .createCompositeState(false));
}