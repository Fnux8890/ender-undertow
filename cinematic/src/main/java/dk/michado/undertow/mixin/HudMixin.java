package dk.michado.undertow.mixin;

import dk.michado.undertow.*;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.*;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
abstract class HudMixin {
    @Inject(method="setOverlayMessage",at=@At("HEAD"),cancellable=true)
    private void undertow$signal(Component message, boolean animate, CallbackInfo ci) {
        if (Cinematic.signal(message.getString())) ci.cancel();
    }
}
