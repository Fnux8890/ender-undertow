#version 330
uniform sampler2D InSampler;
layout(std140) uniform HorizonModEffect { vec4 Cue; };
layout(location = 0) in vec2 texCoord;
layout(location = 0) out vec4 fragColor;

void main() {
    vec2 size = vec2(textureSize(InSampler, 0));
    vec2 offset = (texCoord - 0.5) * size;
    float radius = clamp(2.0 * length(offset) / length(size), 0.0, 1.0);
    float blur = mix(Cue.y, Cue.x, smoothstep(0.0, 1.0, radius));
    vec2 drift = vec2(sin((1.0 - texCoord.y) * 16.0 + Cue.z * 0.22),
                      -cos(texCoord.x * 11.0 - Cue.z * 0.16));
    vec2 uv = texCoord + drift * blur * 2.5 / size;
    vec4 color = texture(InSampler, uv) * 0.2;
    for (int i = 0; i < 8; ++i) {
        float angle = float(i) * 0.78539816;
        vec2 delta = vec2(cos(angle), sin(angle)) * (5.5 * blur) / size;
        color += texture(InSampler, uv + delta) * 0.1;
    }
    fragColor = color;
}
