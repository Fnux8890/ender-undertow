#version 330
#extension GL_ARB_separate_shader_objects : require
uniform sampler2D InSampler;
layout(std140) uniform SamplerInfo { vec2 OutSize; vec2 InSize; };
layout(std140) uniform FocusConfig { vec2 BlurDir; float Defocus; };
layout(location = 0) in vec2 texCoord;
layout(location = 0) out vec4 fragColor;

void main() {
    // Use screen-space distance so the feather stays round on wide displays.
    float distanceFromCenter = length((texCoord - 0.5) * InSize) / length(0.5 * InSize);
    float focusFront = mix(1.65, -0.6, Defocus);
    float softness = smoothstep(focusFront - 0.52, focusFront + 0.52, distanceFromCenter);
    vec2 spread = BlurDir * (0.011 * InSize.y * softness) / InSize;
    vec4 color = texture(InSampler, texCoord) * 0.20;
    float total = 0.20;
    for (int i = 1; i <= 6; ++i) {
        float x = float(i) / 3.0;
        float weight = exp(-1.45 * x*x);
        total += weight * 0.20;
        color += weight * 0.10 * (texture(InSampler, texCoord + x*spread)
                               + texture(InSampler, texCoord - x*spread));
    }
    fragColor = color / total;
}
