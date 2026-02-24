#version 450

layout(location=0) in vec3  inDirection;
layout(location=1) in float inMagnitude;
layout(location=2) in vec3  inColor;
layout(location=3) in float inPad;

layout(set=0, binding=0, std140) uniform StarUBO {
    mat4  viewProj;
    float starVisibility;
    float twinkleTime;
    float minMagnitudeBrightness;
    float pad;
} params;

layout(location=0) out vec3  outColor;
layout(location=1) out float outVisibility;

void main() {
    vec4 clip = params.viewProj * vec4(inDirection, 0.0);
    gl_Position = clip.xyww;

    float brightness = clamp((6.5 - inMagnitude) / 5.5, 0.0, 1.0);
    gl_PointSize = mix(1.0, 3.5, brightness);

    float twinkle = 0.85 + 0.15 * sin(
        params.twinkleTime * 2.3 + inDirection.x * 127.1 + inDirection.y * 311.7);

    outColor = inColor * max(brightness, params.minMagnitudeBrightness) * twinkle;
    outVisibility = params.starVisibility;
}
