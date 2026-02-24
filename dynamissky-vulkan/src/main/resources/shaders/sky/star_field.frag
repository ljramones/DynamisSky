#version 450
layout(location=0) in  vec3  inColor;
layout(location=1) in  float inVisibility;
layout(location=0) out vec4  outColor;

void main() {
    vec2 coord = gl_PointCoord * 2.0 - 1.0;
    float dist = dot(coord, coord);
    if (dist > 1.0) discard;

    float alpha = (1.0 - dist) * inVisibility;
    outColor = vec4(inColor * alpha, alpha);
}
