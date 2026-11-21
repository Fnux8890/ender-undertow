fn post(input: PostInput) -> vec4<f32> {
    let cue = frame.mods[7];
    if cue.w != 97.25 || cue.x <= 0.0 {
        return input.color;
    }
    let size = vec2<f32>(textureDimensions(scene_color));
    let offset = (input.uv - vec2<f32>(0.5)) * size;
    let radius = clamp(2.0 * length(offset) / length(size), 0.0, 1.0);
    let blur = mix(cue.y, cue.x, smoothstep(0.0, 1.0, radius));
    let drift = vec2<f32>(sin(input.uv.y * 16.0 + cue.z * 0.22),
                          cos(input.uv.x * 11.0 - cue.z * 0.16)) * blur * 2.5 / size;
    let uv = input.uv + drift;
    var color = textureSampleLevel(scene_color, linear_sampler, uv, 0.0).rgb * 0.2;
    for (var i = 0; i < 8; i++) {
        let angle = f32(i) * 0.78539816;
        let step_uv = vec2<f32>(cos(angle), sin(angle)) * (5.5 * blur) / size;
        color += textureSampleLevel(scene_color, linear_sampler, uv + step_uv, 0.0).rgb * 0.1;
    }
    return vec4<f32>(color, 1.0);
}
