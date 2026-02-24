#version 450
layout(location=0) in  vec2 inUV;
layout(location=0) out vec4 outColor;

layout(set=0, binding=0) uniform sampler2D skyViewLut;
layout(set=1, binding=0, std140) uniform SkyPassUBO {
    mat4 invViewProj;
    vec3 sunDirection;
    float pad;
} params;

vec3 uvToViewDir(vec2 uv) {
    vec4 clip = vec4(uv * 2.0 - 1.0, 1.0, 1.0);
    vec4 world = params.invViewProj * clip;
    return normalize(world.xyz / max(world.w, 1e-5));
}

vec2 dirToSkyViewUV(vec3 dir) {
    float phi = atan(dir.z, dir.x);
    float theta = asin(clamp(dir.y, -1.0, 1.0));
    return vec2(phi / (2.0 * 3.14159265) + 0.5,
                theta / 3.14159265 + 0.5);
}

void main() {
    vec3 viewDir = uvToViewDir(inUV);
    vec2 skyUV = dirToSkyViewUV(viewDir);
    vec3 color = texture(skyViewLut, skyUV).rgb;
    outColor = vec4(color, 1.0);
}
