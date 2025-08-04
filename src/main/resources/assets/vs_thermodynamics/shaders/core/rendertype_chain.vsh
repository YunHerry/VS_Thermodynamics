#version 150

in vec3 Position;
in vec4 Color;
in vec2 UV0; // 贴图坐标
in ivec2 UV2; // 光照贴图坐标

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec4 vertexColor;
out vec2 texCoord;
out vec2 lightmapCoord;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    vertexColor = Color;
    texCoord = UV0;
    lightmapCoord = vec2(UV2.x / 16.0, UV2.y / 16.0); // 转换为可用的光照UV
}