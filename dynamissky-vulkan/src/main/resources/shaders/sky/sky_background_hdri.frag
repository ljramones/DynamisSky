#version 450
layout(location=0) in  vec2 inUV;
layout(location=0) out vec4 outColor;

layout(set=0, binding=0) uniform sampler2D hdriTexture;
layout(set=1, binding=0, std140) uniform HdriUBO {
    mat4 invViewProj;
    float rotation;
    float intensity;
    float pad0;
    float pad1;
} params;

vec3 uvToViewDir(vec2 uv) {
    vec4 clip = vec4(uv * 2.0 - 1.0, 1.0, 1.0);
    vec4 world = params.invViewProj * clip;
    return normalize(world.xyz / max(world.w, 1e-5));
}

void main() {
    vec3 dir = uvToViewDir(inUV);

    float u = atan(dir.z, dir.x) / (2.0 * 3.14159265) + 0.5;
    float v = asin(clamp(dir.y, -1.0, 1.0)) / 3.14159265 + 0.5;

    u = fract(u + params.rotation / (2.0 * 3.14159265));
    vec3 color = texture(hdriTexture, vec2(u, v)).rgb * params.intensity;
    outColor = vec4(color, 1.0);
}
