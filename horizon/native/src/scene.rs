const HOLD_TICKS: u32 = 62;
const REVEAL_TICKS: u32 = 35;
const CLOSE_RAMP_TICKS: u32 = 6;
const ARRIVE_TIMEOUT_TICKS: u32 = 40;

const DEEP_VIOLET: [f32; 3] = [0.22, 0.03, 0.34];
const DEEP_BLUE: [f32; 3] = [0.03, 0.10, 0.36];

#[derive(Debug, Clone, Copy, PartialEq, Eq)]
enum Phase {
    Idle,
    GrabHold,
    Reveal,
}

impl Default for Phase {
    fn default() -> Self {
        Self::Idle
    }
}

#[derive(Debug, Default, Clone, Copy, PartialEq, Eq)]
pub struct Scene {
    phase: Phase,
    hold_ticks: u32,
    close_ticks: u32,
    timeout_ticks: u32,
    reveal_ticks: u32,
}

impl Scene {
    pub fn active(&self) -> bool {
        self.phase != Phase::Idle
    }

    pub fn grab(&mut self) {
        self.phase = Phase::GrabHold;
        self.hold_ticks = 0;
        self.close_ticks = 0;
        self.timeout_ticks = 0;
        self.reveal_ticks = 0;
    }

    pub fn close(&mut self) {
        if self.phase == Phase::GrabHold && self.close_ticks == 0 {
            self.close_ticks = 1;
        }
    }

    pub fn arrive(&mut self) {
        if self.phase == Phase::GrabHold {
            self.phase = Phase::Reveal;
            self.timeout_ticks = 0;
            self.reveal_ticks = 0;
        }
    }

    pub fn cancel(&mut self) {
        *self = Self::default();
    }

    pub fn tick(&mut self) {
        match self.phase {
            Phase::Idle => {}
            Phase::GrabHold => {
                if self.close_ticks > 0 && self.close_ticks < CLOSE_RAMP_TICKS {
                    self.close_ticks += 1;
                }

                if self.hold_ticks < HOLD_TICKS {
                    self.hold_ticks += 1;
                } else {
                    self.timeout_ticks = self.timeout_ticks.saturating_add(1);
                    if self.timeout_ticks >= ARRIVE_TIMEOUT_TICKS {
                        self.phase = Phase::Reveal;
                        self.timeout_ticks = 0;
                        self.reveal_ticks = 0;
                    }
                }
            }
            Phase::Reveal => {
                self.reveal_ticks = self.reveal_ticks.saturating_add(1);
                if self.reveal_ticks >= REVEAL_TICKS {
                    *self = Self::default();
                }
            }
        }
    }

    pub fn hand_drop(&self) -> f32 {
        match self.phase {
            Phase::Idle => 0.0,
            Phase::GrabHold => smoothstep(0.0, 18.0, self.hold_ticks as f32)
                .max(self.close_progress())
                .clamp(0.0, 1.0),
            Phase::Reveal => (1.0 - smoothstep(0.15, 1.0, self.reveal_progress())).clamp(0.0, 1.0),
        }
    }

    pub fn turn(&self) -> f32 {
        match self.phase {
            Phase::Idle => 0.0,
            Phase::GrabHold => smoothstep(0.0, 24.0, self.hold_ticks as f32)
                .max(self.close_progress())
                .clamp(0.0, 1.0),
            Phase::Reveal => (1.0 - smoothstep(0.0, 0.7, self.reveal_progress())).clamp(0.0, 1.0),
        }
    }

    pub fn shake(&self) -> f32 {
        match self.phase {
            Phase::Idle => 0.0,
            Phase::GrabHold => {
                if self.hold_ticks >= HOLD_TICKS {
                    let decay =
                        (self.timeout_ticks as f32 / ARRIVE_TIMEOUT_TICKS as f32).clamp(0.0, 1.0);
                    (1.0 - 0.15 * decay).clamp(0.0, 1.0)
                } else {
                    let p = self.hold_progress();
                    let env = smoothstep(0.0, 1.0, p);
                    let pulse = 0.85 + 0.15 * (p * 12.0 * std::f32::consts::PI).cos().abs();
                    (env * pulse).clamp(0.0, 1.0)
                }
            }
            Phase::Reveal => {
                let t = self.reveal_progress();
                let env = 1.0 - smoothstep(0.0, 0.65, t);
                let pulse = 0.85 + 0.15 * (t * 6.0 * std::f32::consts::PI).cos().abs();
                (env * pulse).clamp(0.0, 1.0)
            }
        }
    }

    pub fn focus_params(&self, distortion_scale: f32, time: f32) -> Option<[f32; 4]> {
        let edge = self.tint(1.0)[3] * distortion_scale;
        (edge > 0.0).then(|| [edge, self.tint(0.0)[3] * distortion_scale, time, 0.0])
    }

    pub fn tint(&self, radius: f32) -> [f32; 4] {
        let r = if radius.is_nan() {
            0.0
        } else {
            radius.clamp(0.0, 1.0)
        };

        match self.phase {
            Phase::Idle => [0.0, 0.0, 0.0, 0.0],
            Phase::GrabHold => {
                let hold_p = self.hold_progress();
                let cover = (0.12 * hold_p + 0.88 * smoothstep(0.86, 1.0, hold_p))
                    .max(self.close_progress());
                let alpha = smoothstep(0.0, 1.0, cover * 1.45 - (1.0 - r) * 0.45);
                let color_mix = 0.04 * r;
                let rgb = lerp_rgb(DEEP_VIOLET, DEEP_BLUE, color_mix);
                [
                    rgb[0].clamp(0.0, 1.0),
                    rgb[1].clamp(0.0, 1.0),
                    rgb[2].clamp(0.0, 1.0),
                    alpha.clamp(0.0, 1.0),
                ]
            }
            Phase::Reveal => {
                let t = self.reveal_progress();
                let feather = 0.5;
                let wave = t * (1.0 + feather);
                let clear = smoothstep(0.0, feather, wave - r);
                let alpha = (1.0 - clear).clamp(0.0, 1.0);
                let color_mix = (t * 1.6 + r * 0.04).clamp(0.0, 1.0);
                let rgb = lerp_rgb(DEEP_VIOLET, DEEP_BLUE, color_mix);
                [
                    rgb[0].clamp(0.0, 1.0),
                    rgb[1].clamp(0.0, 1.0),
                    rgb[2].clamp(0.0, 1.0),
                    alpha,
                ]
            }
        }
    }

    fn hold_progress(&self) -> f32 {
        (self.hold_ticks as f32 / HOLD_TICKS as f32).clamp(0.0, 1.0)
    }

    fn close_progress(&self) -> f32 {
        if self.close_ticks == 0 {
            0.0
        } else {
            smoothstep(0.0, CLOSE_RAMP_TICKS as f32, self.close_ticks as f32)
        }
    }

    fn reveal_progress(&self) -> f32 {
        (self.reveal_ticks as f32 / REVEAL_TICKS as f32).clamp(0.0, 1.0)
    }
}

fn smoothstep(edge0: f32, edge1: f32, x: f32) -> f32 {
    if edge1 <= edge0 {
        return if x >= edge1 { 1.0 } else { 0.0 };
    }
    let t = ((x - edge0) / (edge1 - edge0)).clamp(0.0, 1.0);
    t * t * (3.0 - 2.0 * t)
}

fn lerp_rgb(a: [f32; 3], b: [f32; 3], t: f32) -> [f32; 3] {
    let t = t.clamp(0.0, 1.0);
    [
        a[0] + (b[0] - a[0]) * t,
        a[1] + (b[1] - a[1]) * t,
        a[2] + (b[2] - a[2]) * t,
    ]
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn focus_follows_the_local_scene_and_distortion_preference() {
        let mut scene = Scene::default();
        assert!(scene.focus_params(1.0, 0.0).is_none());
        scene.grab();
        scene.arrive();
        for _ in 0..12 {
            scene.tick();
        }
        assert!(scene.focus_params(0.0, 12.5).is_none());
        let full = scene.focus_params(1.0, 12.5).unwrap();
        let half = scene.focus_params(0.5, 12.5).unwrap();
        assert!(full[0] > full[1]);
        assert_eq!(half, [full[0] * 0.5, full[1] * 0.5, 12.5, 0.0]);
        let mut cancelled = scene;
        cancelled.cancel();
        assert!(cancelled.focus_params(1.0, 13.0).is_none());
        for _ in 12..REVEAL_TICKS {
            scene.tick();
        }
        assert!(scene.focus_params(1.0, 35.0).is_none());
    }

    #[test]
    fn hold_and_center_first_reveal_curve() {
        let mut scene = Scene::default();
        assert_eq!(scene.hand_drop(), 0.0);
        assert_eq!(scene.turn(), 0.0);
        assert_eq!(scene.shake(), 0.0);
        assert_eq!(scene.tint(0.0), [0.0; 4]);
        assert_eq!(scene.tint(1.0), [0.0; 4]);

        scene.grab();
        for tick in 1..=HOLD_TICKS {
            if tick == 45 {
                scene.close();
            }
            scene.tick();

            for r in [-1.0, 0.0, 0.5, 1.0, 2.0, f32::NAN, f32::INFINITY] {
                let rgba = scene.tint(r);
                assert!(rgba.iter().all(|c| (0.0..=1.0).contains(c)));
            }
            assert!((0.0..=1.0).contains(&scene.hand_drop()));
            assert!((0.0..=1.0).contains(&scene.turn()));
            assert!((0.0..=1.0).contains(&scene.shake()));

            if tick == 10 {
                let center = scene.tint(0.0);
                let edge = scene.tint(1.0);
                assert!(edge[3] > center[3]);
                assert!(edge[2] > edge[1]);
            }
        }

        assert_eq!(scene.hand_drop(), 1.0);
        assert_eq!(scene.turn(), 1.0);
        assert_eq!(scene.shake(), 1.0);
        assert_eq!(scene.tint(0.0)[3], 1.0);
        assert_eq!(scene.tint(1.0)[3], 1.0);

        scene.arrive();
        assert_eq!(scene.tint(0.0)[3], 1.0);
        assert_eq!(scene.tint(1.0)[3], 1.0);

        for tick in 1..=REVEAL_TICKS {
            scene.tick();
            let center = scene.tint(0.0);
            let mid = scene.tint(0.5);
            let edge = scene.tint(1.0);

            for rgba in [center, mid, edge] {
                assert!(rgba.iter().all(|c| (0.0..=1.0).contains(c)));
            }

            if tick == 14 {
                assert!(center[3] < mid[3] && mid[3] < edge[3]);
            }
            if tick == 24 {
                assert_eq!(center[3], 0.0);
                assert!(edge[3] > 0.0);
                assert!(edge[2] > edge[0] && edge[2] > edge[1]);
            }
        }

        assert_eq!(scene.hand_drop(), 0.0);
        assert_eq!(scene.turn(), 0.0);
        assert_eq!(scene.shake(), 0.0);
        assert_eq!(scene.tint(0.0), [0.0; 4]);
        assert_eq!(scene.tint(1.0), [0.0; 4]);
    }

    #[test]
    fn out_of_order_timeout_and_repeated_cancel() {
        let mut scene = Scene::default();

        // Out-of-order events while idle are ignored.
        scene.close();
        scene.arrive();
        scene.tick();
        assert_eq!(scene.tint(0.0), [0.0; 4]);
        assert_eq!(scene.tint(1.0), [0.0; 4]);
        assert_eq!(scene.hand_drop(), 0.0);

        // Early arrive followed by late close stays in reveal and finishes in 35 ticks.
        scene.grab();
        for _ in 0..10 {
            scene.tick();
        }
        scene.arrive();
        scene.close();
        for _ in 0..14 {
            scene.tick();
        }
        assert!(scene.tint(0.0)[3] < scene.tint(1.0)[3]);
        for _ in 0..(REVEAL_TICKS - 14) {
            scene.tick();
        }
        assert_eq!(scene.tint(1.0), [0.0; 4]);
        assert_eq!(scene.hand_drop(), 0.0);

        // Missing arrive times out into reveal and returns to idle.
        scene.grab();
        for _ in 0..HOLD_TICKS {
            scene.tick();
        }
        assert_eq!(scene.tint(0.0)[3], 1.0);
        for _ in 0..ARRIVE_TIMEOUT_TICKS {
            scene.tick();
        }
        for _ in 0..REVEAL_TICKS {
            scene.tick();
        }
        assert_eq!(scene.tint(0.0), [0.0; 4]);
        assert_eq!(scene.tint(1.0), [0.0; 4]);
        assert_eq!(scene.hand_drop(), 0.0);
        assert_eq!(scene.turn(), 0.0);
        assert_eq!(scene.shake(), 0.0);

        // Repeated cancellation is idempotent and blocks stale events.
        scene.grab();
        for _ in 0..20 {
            scene.tick();
        }
        assert!(scene.tint(1.0)[3] > 0.0);
        scene.cancel();
        scene.cancel();
        scene.cancel();
        scene.close();
        scene.arrive();
        scene.tick();
        assert_eq!(scene.tint(0.0), [0.0; 4]);
        assert_eq!(scene.tint(1.0), [0.0; 4]);
        assert_eq!(scene.hand_drop(), 0.0);
        assert_eq!(scene.turn(), 0.0);
        assert_eq!(scene.shake(), 0.0);
    }
}
