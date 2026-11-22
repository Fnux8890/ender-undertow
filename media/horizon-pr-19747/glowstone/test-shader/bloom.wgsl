// Deliberately wide, warm bloom for a screenshot comparison. Sample scene color only.
fn post(input: PostInput) -> vec4<f32> {
    let pixel = vec2<f32>(frame.resolution.y / 450.0) / frame.resolution;
    var halo = vec3<f32>(0.0);
    for (var ring = 1; ring <= 4; ring++) {
        for (var i = 0; i < 12; i++) {
            let a = 0.523599 * (f32(i) + 0.33 * f32(ring));
            let uv = input.uv + vec2<f32>(cos(a), sin(a)) * (f32(ring) * 6.0) * pixel;
            let c = textureSampleLevel(previous, linear_sampler, uv, 0.0).rgb;
            let warm = smoothstep(0.02, 0.15, c.r - c.b);
            halo += c * warm * smoothstep(0.12, 0.65, c.r) / f32(ring);
        }
    }
    return vec4<f32>(input.color.rgb + halo * vec3<f32>(0.19, 0.12, 0.045), input.color.a);
}
