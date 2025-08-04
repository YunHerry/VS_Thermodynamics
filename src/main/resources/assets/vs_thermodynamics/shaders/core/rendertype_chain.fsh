#version 150

uniform sampler2D Sampler0; // 主贴图
uniform sampler2D Sampler2; // 光照贴图 (由Minecraft自动绑定)

in vec4 vertexColor;
in vec2 texCoord;
in vec2 lightmapCoord;

out vec4 fragColor;

void main() {
    // 从主贴图采样颜色
    vec4 texel = texture(Sampler0, texCoord);

    // 如果贴图的alpha值很低，则抛弃该片段（实现透明效果）
    if (texel.a < 0.1) {
        discard;
    }

    // 从光照贴图采样光照值
    vec4 lightColor = texture(Sampler2, lightmapCoord);

    // 最终颜色 = 贴图颜色 * 顶点颜色（用于着色） * 光照
    fragColor = texel * vertexColor * lightColor;
}