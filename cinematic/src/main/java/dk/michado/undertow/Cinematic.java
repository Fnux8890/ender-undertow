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
                || clock() - start > 65 || landed && clock() - arrival >= 15 && System.nanoTime()-arrivedAtNanos >= 700_000_000L) {
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

    public static float[] turn(Camera camera, float partial) {
        if (!active() || landed || camera.entity() != victim || camera.isDetached()
                || Minecraft.getInstance().options.screenEffectScale().get() < 0.5) return null;
        if (!(level.getEntity(carrier) instanceof Enderman mob)) return null;
        Vec3 direction = mob.getEyePosition(partial).subtract(camera.position());
        float yaw = (float) Math.toDegrees(Math.atan2(-direction.x, direction.z));
        float pitch = (float) -Math.toDegrees(Math.atan2(direction.y, direction.horizontalDistance()));
        double delta = Mth.wrapDegrees(yaw - camera.yRot());
        if (!Double.isNaN(turnDelta)) delta = turnDelta + Mth.wrapDegrees(delta-turnDelta);
        turnDelta = delta;
        float t = (float) ease((clock()-start)/10);
        return new float[]{camera.yRot() + (float)delta*t, Mth.lerp(t, camera.xRot(), pitch)};
    }

    public static void lids(GuiGraphicsExtractor canvas) {
        if (!active() || !closing && !landed) return;
        // Even when packets arrive in a burst, hold one visible beat over the relocation.
        double reveal = Math.min((clock()-arrival-2)/12, (System.nanoTime()-arrivedAtNanos-120_000_000L)/580_000_000.0);
        double shut = landed ? 1-ease(reveal) : ease((clock()-closed)/5);
        if (shut <= 0) return;
        canvas.nextStratum();
        int w = canvas.guiWidth(), h = canvas.guiHeight();
        if (Minecraft.getInstance().options.screenEffectScale().get() < 0.5) {
            canvas.fill(0,0,w,h,((int)(210*shut)<<24) | 0x200d32);
            return;
        }
        // A broad almond-shaped aperture reveals the live scene between two curved lids.
        int width = Math.max(1,w/180);
        for (int x=0; x<w; x+=width) {
            double nx = 2.0*(x+width*0.5)/w - 1;
            double aperture = (1-shut)*(1.6-0.6*nx*nx);
            double middle = h*(0.50+0.035*(1-nx*nx));
            int top = (int)Math.ceil(middle - h*0.55*aperture);
            int bottom = (int)Math.floor(middle + h*0.55*aperture);
            int right = Math.min(w,x+width);
            if (top>0) canvas.fill(x,0,right,Math.min(h,top),0xff1b0c2b);
            if (bottom<h) canvas.fill(x,Math.max(0,bottom),right,h,0xff1b0c2b);
            if (bottom>top) {
                canvas.fill(x,top,right,top+2,0x704f276a);
                canvas.fill(x,bottom-2,right,bottom,0x704f276a);
            }
        }
    }
}
