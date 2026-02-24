#version 450

layout(set=0, binding=0, std140) uniform MoonUBO {
    mat4  viewProj;
    vec3  moonDirection;
    float moonIntensity;
    vec3  sunDirection;
    float phaseAngle;
    float angularSize;
    float pad0;
    float pad1;
    float pad2;
} params;

layout(location=0) out vec2 outUV;
layout(location=1) out float outIntensity;
layout(location=2) out float outPhaseAngle;

vec2 quadPos(int i) {
    vec2 p[6] = vec2[](
        vec2(-1.0, -1.0),
        vec2( 1.0, -1.0),
        vec2( 1.0,  1.0),
        vec2(-1.0, -1.0),
        vec2( 1.0,  1.0),
        vec2(-1.0,  1.0)
    );
    return p[i];
}

void main() {
    vec2 q = quadPos(gl_VertexIndex);
    vec3 center = normalize(params.moonDirection);
    float scale = tan(radians(params.angularSize * 0.5));

    vec3 up = vec3(0.0, 1.0, 0.0);
    vec3 right = normalize(cross(up, center));
    up = normalize(cross(center, right));

    vec3 world = center + (right * q.x + up * q.y) * scale;
    vec4 clip = params.viewProj * vec4(world, 0.0);
    gl_Position = clip.xyww;

    outUV = q * 0.5 + 0.5;
    outIntensity = params.moonIntensity;
    outPhaseAngle = params.phaseAngle;
}
