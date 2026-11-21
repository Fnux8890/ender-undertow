package dk.michado.undertow.mixin;

import dk.michado.undertow.*;

import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
abstract class CameraMixin {
    @Shadow protected abstract void setRotation(float yaw,float pitch);

    @Inject(method="alignWithEntity",at=@At("TAIL"))
    private void undertow$turn(float partial, CallbackInfo ci) {
        float[] angles = Cinematic.turn((Camera)(Object)this, partial);
        if (angles != null) setRotation(angles[0], angles[1]);
    }
}
