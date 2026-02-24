#version 450
layout(location=0) in  vec2  inUV;
layout(location=1) in  float inIntensity;
layout(location=2) in  float inPhaseAngle;
layout(location=0) out vec4  outColor;

layout(set=1, binding=0) uniform sampler2D moonAlbedo;

void main() {
    vec4 albedo = texture(moonAlbedo, inUV);

    float x = inUV.x * 2.0 - 1.0;
    float y2 = max(0.0, 1.0 - x * x);
    float lit = clamp(cos(inPhaseAngle) * x + sin(inPhaseAngle) * sqrt(y2), 0.0, 1.0);

    vec3 color = albedo.rgb * lit * inIntensity;
    outColor = vec4(color, albedo.a * lit);
}
