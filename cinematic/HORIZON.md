# Horizon cinematic target

Target **Horizon Nightly 2026-11-20**, Minecraft 26.3, source revision `0a875da91dbb0fdcb7ba8c42c11341aa229af51a`.

## Verified gameplay baseline

The official Linux nightly, with its own freshly prepared 26.3 resources, passed all **32 integrated-server assertions** on seeds **41 and 37** using the data pack built from the Java beta sources. These include the ordinary eight-tick encounter, the extended 27-tick hold, targeting a named miner's original carrier, a safe teleport, and cancellation when that carrier disappears. The test is available through `tools/test_ender_undertow.py --cinematic`. The assertions cover the server state. Neither the Java JARs nor the Java rendering mixins run in Horizon.

An older local debug client discovered both the ZIP and the extracted pack but reported an unknown block tag for `undertow:floor`. The previous data pack also failed its landing checks on that client. Keep the floor, headroom and escape-space rules when porting. Use the pinned nightly for the first rendering test.

## Client implementation

- **Turn.** Use `native_camera_pose` (API 402) to adjust the rendered yaw and pitch using the shortest arc to the selected Enderman. This hook preserves the player's gameplay aim. Keep the horizon level and retain the reduced-motion fade.
- **Signals.** Negotiate support per player and clear it on disconnect and respawn. The Java data pack emits targeted action bars for grab, close, arrive and cancel. The native `client_packet` hook observes incoming play packets in order and is read-only. Verify both integrated and LAN delivery, and suppress the reserved text in the action-bar presentation path. Prefer a dedicated payload if the native bridge can provide equivalent local and remote delivery.
- **Jaw.** Resolve the signalled UUID to the original carrier. Interpolate the existing Enderman head and hat offsets in the mob renderer. Keep its AI and actual target intact.
- **Sight.** Use a registered GUI layer for the violet eyelids and reveal. Verify the layer ordering with HUD hidden and pause menus open. On cancellation, world close, death or timeout, restore the ordinary view.
- **Acceptance.** Run the same named-player rehearsal in a lit cave, record the actual Horizon window, and repeat with the carrier removed during the lift. In a two-player LAN session, only the selected miner should receive the camera turn and lose sight. Test the ordinary eight-tick fallback with a client that does not opt in.

[Camera pose hook](https://github.com/horizon-rust/horizon/blob/0a875da91dbb0fdcb7ba8c42c11341aa229af51a/crates/horizon-client/src/game/native_hooks/camera_pose.rs) · [Incoming packet hook](https://github.com/horizon-rust/horizon/blob/0a875da91dbb0fdcb7ba8c42c11341aa229af51a/crates/horizon-client/src/game/native_hooks/client_packet.rs) · [GUI drawing contract](https://github.com/horizon-rust/horizon/blob/0a875da91dbb0fdcb7ba8c42c11341aa229af51a/crates/horizon-mod-vocab/src/hooks/gui_layer_draw.rs)
