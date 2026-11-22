mod scene;

use mod_api::*;
use scene::Scene;
use std::sync::{Arc, Mutex};

#[derive(Default)]
struct Encounter {
    scene: Scene,
    carrier: Option<EntityUuid>,
    eye: Option<[f64; 3]>,

    carrier_id: Option<u32>,
    clock: u32,
}

#[derive(Default)]
struct Undertow(Arc<Mutex<Encounter>>);

fn carrier_uuid(text: &str) -> Option<EntityUuid> {
    let words = text.strip_prefix("[I;")?.strip_suffix(']')?;
    let mut words = words.split(',');
    let mut id = 0u128;
    for _ in 0..4 {
        id = id << 32 | u128::from(words.next()?.trim().parse::<i32>().ok()? as u32);
    }
    words.next().is_none().then_some(EntityUuid(id))
}

#[mod_api::horizon_mod]
impl Hooks for Undertow {
    fn register_commands(&self, _: &mut dyn ServerHost, event: &mut RegisterCommandsEvent<'_>) {
        event
            .commands
            .push(Command::new("undertow_carrier <uuid:text>"));
    }

    fn run_mod_command(
        &self,
        host: &mut dyn ServerHost,
        event: &mut RunModCommandEvent<'_>,
    ) -> Flow {
        if event.command.literals.first().map(String::as_str) != Some("undertow_carrier") {
            return Flow::Continue;
        }
        let (CommandSource::Player { player, .. }, Some(CommandArgumentValue::String(raw))) =
            (event.source, event.command.argument("uuid"))
        else {
            return Flow::Cancel;
        };
        if let Some(entity) = carrier_uuid(raw).and_then(|id| host.entity_by_uuid(id)) {
            let Some(pos) = entity.position(host) else {
                return Flow::Cancel;
            };
            if pos
                .iter()
                .zip(event.command.position)
                .map(|(a, b)| (a - b).powi(2))
                .sum::<f64>()
                <= 25.0
            {
                let _ = player.tell(
                    host,
                    Text::literal(format!("undertow:carrier:{raw}|{}", entity.id())),
                );
            }
        }
        Flow::Cancel
    }

    fn client_chat_received(
        &self,
        host: &mut dyn ClientHost,
        event: &mut ClientChatReceivedEvent<'_>,
    ) -> Flow {
        if !event.system {
            return Flow::Continue;
        }
        let text::TextContent::Literal(line) = &event.message.content else {
            return Flow::Continue;
        };
        let Some(signal) = line.strip_prefix("undertow:") else {
            return Flow::Continue;
        };
        let mut state = self.0.lock().unwrap();
        if let Some(reply) = signal.strip_prefix("carrier:") {
            if let Some((uuid, id)) = reply.split_once('|') {
                if carrier_uuid(uuid).is_some_and(|u| state.carrier == Some(u)) {
                    state.carrier_id = id.parse().ok();
                }
            }
            return Flow::Cancel;
        }
        match signal {
            "arrive" => {
                state.scene.arrive();
                state.carrier = None;
                state.eye = None;
                state.carrier_id = None;
            }
            "cancel" => {
                state.scene.cancel();
                state.carrier = None;
                state.carrier_id = None;
                state.eye = None;
            }
            "close" => state.scene.close(),
            _ => {
                let Some((uuid, pos)) =
                    signal.strip_prefix("grab:").and_then(|s| s.split_once('|'))
                else {
                    return Flow::Continue;
                };
                let Some(id) = carrier_uuid(uuid) else {
                    return Flow::Continue;
                };
                let Some(coords) = pos.strip_prefix('[').and_then(|s| s.strip_suffix(']')) else {
                    return Flow::Continue;
                };
                let mut coords = coords
                    .split(',')
                    .map(|s| s.trim().trim_end_matches(['d', 'D']).parse::<f64>());
                let (Some(Ok(x)), Some(Ok(y)), Some(Ok(z)), None) =
                    (coords.next(), coords.next(), coords.next(), coords.next())
                else {
                    return Flow::Continue;
                };
                if ![x, y, z].iter().all(|v| v.is_finite()) {
                    return Flow::Continue;
                }
                *state = Encounter::default();
                state.eye = Some([x, y + 2.55, z]);
                state.carrier = Some(id);
                state.clock = 0;
                state.scene.grab();
                drop(state);
                let _ = host.send_chat(&format!("/undertow_carrier {uuid}"));
            }
        }
        Flow::Cancel
    }

    fn client_tick_post(&self, host: &mut dyn ClientHost, _: &mut ClientTickPostEvent) {
        let mut state = self.0.lock().unwrap();
        if host.local_player().is_none() {
            *state = Encounter::default();
            return;
        }
        if state.carrier.is_some() {
            if let Some(entity) = state.carrier_id.and_then(|id| host.entity(id)) {
                if let Some(pos) = entity.position(host) {
                    state.eye = Some([pos[0], pos[1] + 2.55, pos[2]]);
                }
            }
        }
        state.clock = state.clock.wrapping_add(1);
        state.scene.tick();
        if !state.scene.active() {
            state.carrier = None;
            state.carrier_id = None;
            state.eye = None;
        }
    }

    fn camera_pose(&self, _: &dyn ClientHost, event: &mut CameraPoseEvent<'_>) {
        let state = self.0.lock().unwrap();
        let t = state.scene.turn();
        if t == 0.0 {
            return;
        }
        let Some(eye) = state.eye else {
            return;
        };
        let [x, y, z] = std::array::from_fn(|i| eye[i] - event.position[i]);
        let yaw = (-x).atan2(z).to_degrees() as f32;
        let pitch = (-y).atan2(x.hypot(z)).to_degrees() as f32;
        event.yaw += ((yaw - event.yaw + 180.0).rem_euclid(360.0) - 180.0) * t;
        event.pitch += (pitch - event.pitch) * t;
    }

    fn camera_shake(&self, _: &dyn ClientHost, event: &mut CameraShakeEvent<'_>) {
        let state = self.0.lock().unwrap();
        let t = state.clock as f32 + event.partial;
        let scale = state.scene.shake();
        event.shake.yaw += (t * 1.27).sin() * scale * 2.1;
        event.shake.pitch += (t * 1.73).sin() * scale * 1.35;
    }

    fn client_player_logging_out(
        &self,
        _: &mut dyn ClientHost,
        _: &mut ClientPlayerLoggingOutEvent,
    ) {
        *self.0.lock().unwrap() = Encounter::default();
    }

    fn client_player_clone(&self, _: &mut dyn ClientHost, _: &mut ClientPlayerCloneEvent) {
        *self.0.lock().unwrap() = Encounter::default();
    }

    fn register_entity_layers(
        &self,
        _: &RegistrationContext,
        event: &mut RegisterEntityLayersEvent<'_>,
    ) {
        let _ = event.layers.add(
            LayerTarget::Entity("minecraft:enderman".into()),
            Arc::new(CarrierLayer(self.0.clone())),
        );
    }

    fn render_hand(&self, _: &dyn ClientHost, event: &mut RenderHandEvent<'_, '_>) -> Flow {
        event.render.translation[1] -= self.0.lock().unwrap().scene.hand_drop() * 0.9;
        Flow::Continue
    }

    fn mod_post_effects(&self, _: &dyn ClientHost, event: &mut ModPostEffectsEvent<'_>) {
        let state = self.0.lock().unwrap();
        let time = state.clock as f32 + event.partial;
        if let Some(params) = state.scene.focus_params(event.distortion_scale, time) {
            event.effects.push(ModPostEffect {
                id: Identifier::parse("ender_undertow_cinematic:focus").unwrap(),
                params,
            });
        }
    }

    fn gui_render_post(&self, _: &dyn ClientHost, event: &mut GuiRenderPostEvent<'_>) {
        let state = self.0.lock().unwrap();
        if state.scene.tint(1.0)[3] <= 0.0 {
            return;
        }
        let [w, h] = event.batch.size();
        // Non-overlapping cells prevent alpha accumulation along the reveal.
        for row in 0..36 {
            for col in 0..64 {
                let x = (col as f32 + 0.5) / 32.0 - 1.0;
                let y = (row as f32 + 0.5) / 18.0 - 1.0;
                let [r, g, b, a] = state.scene.tint((x * w).hypot(y * h) / w.hypot(h));
                let byte = |v: f32| (255.0 * v).round() as u32;
                if byte(a) == 0 {
                    continue;
                }
                let left = (w * col as f32 / 64.0).round();
                let top = (h * row as f32 / 36.0).round();
                let right = (w * (col + 1) as f32 / 64.0).round();
                let bottom = (h * (row + 1) as f32 / 36.0).round();
                event.batch.fill(
                    [left, top, right - left, bottom - top],
                    byte(a) << 24 | byte(r) << 16 | byte(g) << 8 | byte(b),
                );
            }
        }
    }
}

struct CarrierLayer(Arc<Mutex<Encounter>>);
impl EntityRenderLayer for CarrierLayer {
    fn submit(
        &self,
        entity: &EntityRenderState,
        _: Option<&PosedParts>,
        draw: &mut dyn BlockEntityDrawContext,
    ) -> Result<(), String> {
        let state = self.0.lock().unwrap();
        if state.carrier.is_none() {
            return Ok(());
        }

        if state.carrier_id != Some(entity.id) {
            return Ok(());
        }
        let reach = state.scene.hand_drop();
        let face = Some(CubeFace::new("block/black_concrete"));
        for x in [-5.5, 3.5] {
            draw.cube(
                [x, 29.0, 1.0],
                [x + 2.0, 31.0, 2.0 + reach * 14.0],
                [face; 6],
                0x504457,
            );
        }
        let jaw = state.scene.shake() * 3.0;
        draw.cube(
            [-3.0, 38.5 - jaw, 4.0],
            [3.0, 39.0, 4.2],
            [face; 6],
            0x351834,
        );
        Ok(())
    }
}
