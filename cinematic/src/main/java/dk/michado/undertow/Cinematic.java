package dk.michado.undertow;

import java.util.UUID;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Enderman;
import net.minecraft.world.phys.Vec3;

/** Presentation state belongs to the local victim. Never move the player or simulate a teleport here. */
public final class Cinematic {
    private static ClientLevel level;
    private static LocalPlayer victim;
    private static UUID carrier;
    private static double start, closed, arrival;
    private static boolean closing, landed;
    private static double turnDelta;
    private static long arrivedAtNanos;
    private static final boolean TRACE = Boolean.getBoolean("enderundertow.trace");
    private static void trace(String message) {
        if (TRACE) com.mojang.logging.LogUtils.getLogger().info("[Ender Undertow] {}", message);
    }

    private static double clock() {
        return victim.tickCount + Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
    }

    public static void clear() {
        victim = null;
        level = null;
        carrier = null;
        closing = landed = false;
    }

    public static boolean active() {
        Minecraft game = Minecraft.getInstance();
        if (victim == null) return false;
        if (game.level != level || game.player != victim || !victim.isAlive()
                || game.gui.screen() instanceof net.minecraft.client.gui.screens.PauseScreen
                || clock() - start > 65 || landed && clock() - arrival >= 34 && System.nanoTime()-arrivedAtNanos >= 700_000_000L) {
            clear();
            return false;
        }
        return true;
    }

    public static boolean signal(String text) {
        if (text.startsWith("undertow:grab:")) {
            try {
                // Java Edition serializes entity UUIDs as four signed 32-bit integers.
                String raw = text.substring("undertow:grab:".length()).replace(" ", "");
                if (!raw.startsWith("[I;") || !raw.endsWith("]")) return false;
                String[] parts = raw.substring(3, raw.length()-1).split(",");
                if (parts.length != 4) return false;
                UUID id = new UUID(pair(parts[0], parts[1]), pair(parts[2], parts[3]));
                clear();
                Minecraft game = Minecraft.getInstance();
                if (game.player != null && game.level != null) {
                    victim = game.player;
                    level = game.level;
                    carrier = id;
                    start = clock();
                    trace("grab " + id);
                    turnDelta = Double.NaN;
                }
                return true;
            } catch (NumberFormatException malformed) {
                return false;
            }
        }
        switch (text) {
            case "undertow:close" -> { if (active() && !closing) { closed = clock(); closing = true; trace("close"); } }
            case "undertow:arrive" -> { if (active()) { arrival = clock(); arrivedAtNanos = System.nanoTime(); landed = true; trace("arrive"); } }
            case "undertow:cancel" -> { trace("cancel"); clear(); }
            default -> { return false; }
        }
        return true;
    }

    private static long pair(String high, String low) {
        return ((long) Integer.parseInt(high) << 32) | Integer.toUnsignedLong(Integer.parseInt(low));
    }

    private static double ease(double value) {
        double t = Math.clamp(value, 0, 1);
        return t*t*(3-2*t);
    }

    public static boolean holding(Enderman mob) {
        return active() && !landed && mob.getUUID().equals(carrier);
    }

    public static void faceVictim(Enderman mob, net.minecraft.client.renderer.entity.state.EndermanRenderState state, float partial) {
        if (!holding(mob)) return;
        Vec3 toward = victim.getEyePosition(partial).subtract(mob.getEyePosition(partial));
        state.bodyRot = (float)Math.toDegrees(Math.atan2(-toward.x, toward.z));
        state.yRot = 0;
        state.xRot = (float)-Math.toDegrees(Math.atan2(toward.y, toward.horizontalDistance()));
    }

    public static float jaw(Enderman mob) {
        if (!holding(mob)) return 0;
        return (float) ease((clock()-start-8)/8);
    }

    public static float grip(Enderman mob) {
        return holding(mob) ? (float)ease((clock()-start-2)/8) : 0;
    }

    public static double handDrop() {
        if (!active()) return 0;
        return landed ? 1-ease((clock()-arrival-5)/15) : ease((clock()-start-4)/8);
    }

    public static double fatigue() {
        if (!active() || !landed) return 0;
        double strength = Minecraft.getInstance().options.screenEffectScale().get();
        return strength < 0.5 ? 0 : strength*(1-ease((clock()-arrival-3)/28));
    }

    public static float[] turn(Camera camera, float partial) {
        if (!active() || camera.entity() != victim || camera.isDetached()
                || Minecraft.getInstance().options.screenEffectScale().get() < 0.5) return null;
        if (landed) {
            double amount = fatigue();
            if (amount <= 0) return null;
            double phase = (clock()-arrival)*0.25;
            return new float[] {camera.yRot() + (float)(1.35*amount*Math.sin(phase)),
                    camera.xRot() + (float)(0.65*amount*Math.sin(phase*0.8))};
        }
        if (!(level.getEntity(carrier) instanceof Enderman mob)) return null;
        Vec3 direction = mob.getEyePosition(partial).subtract(camera.position());
        float yaw = (float) Math.toDegrees(Math.atan2(-direction.x, direction.z));
        float pitch = (float) -Math.toDegrees(Math.atan2(direction.y, direction.horizontalDistance()));
        double delta = Mth.wrapDegrees(yaw - camera.yRot());
        if (!Double.isNaN(turnDelta)) delta = turnDelta + Mth.wrapDegrees(delta-turnDelta);
        turnDelta = delta;
        double age = clock()-start;
        float t = (float)ease(age/10);
        float facingYaw = camera.yRot() + (float)delta*t;
        float facingPitch = Mth.lerp(t, camera.xRot(), pitch);

        // The scream plays at hold 12, fifteen ticks after the grab at hold 27.
        double beat = age - 15;
        if (beat > 0 && beat < 10) {
            double strength = Minecraft.getInstance().options.screenEffectScale().get();
            double envelope = strength * ease(beat) * (1-ease((beat-1)/9));
            facingPitch -= (float)(3.6 * envelope * Math.sin(beat*1.32));
            facingYaw += (float)(envelope * (2.1*Math.sin(beat*1.16) + 0.8*Math.sin(beat*2.07)));
        }
        return new float[]{facingYaw, facingPitch};
    }

    public static double defocus() {
        if (!active() || !closing && !landed) return 0;
        if (!landed) return ease((clock()-closed)/5);
        return 1-ease((clock()-arrival-2)/30);
    }

    public static void reducedFade(GuiGraphicsExtractor canvas) {
        if (Minecraft.getInstance().options.screenEffectScale().get() >= 0.5) return;
        int alpha = (int)(96*defocus());
        if (alpha == 0) return;
        canvas.nextStratum();
        canvas.fill(0, 0, canvas.guiWidth(), canvas.guiHeight(), alpha << 24);
    }
}
