package dk.michado.undertow.mixin;

import dk.michado.undertow.*;

import net.minecraft.client.renderer.entity.EndermanRenderer;
import net.minecraft.client.renderer.entity.state.EndermanRenderState;
import net.minecraft.world.entity.monster.Enderman;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndermanRenderer.class)
abstract class EndermanRendererMixin {
    @Inject(method="extractRenderState(Lnet/minecraft/world/entity/monster/Enderman;Lnet/minecraft/client/renderer/entity/state/EndermanRenderState;F)V",at=@At("TAIL"))
    private void undertow$pose(Enderman mob, EndermanRenderState state, float partial, CallbackInfo ci) {
        ((JawState)state).undertow$grip(Cinematic.grip(mob));
        Cinematic.faceVictim(mob, state, partial);
        ((JawState)state).undertow$jaw(Cinematic.jaw(mob));
    }
}
