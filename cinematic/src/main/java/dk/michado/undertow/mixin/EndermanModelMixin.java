package dk.michado.undertow.mixin;

import dk.michado.undertow.*;

import net.minecraft.client.model.monster.enderman.EndermanModel;
import net.minecraft.client.renderer.entity.state.EndermanRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndermanModel.class)
abstract class EndermanModelMixin {
    @Inject(method="setupAnim(Lnet/minecraft/client/renderer/entity/state/EndermanRenderState;)V",at=@At("TAIL"))
    private void undertow$jaw(EndermanRenderState state, CallbackInfo ci) {
        if (state.isCreepy) return;
        float lift = ((JawState)state).undertow$jaw()*5;
        EndermanModel<?> model=(EndermanModel<?>)(Object)this;
        model.head.y -= lift;
        model.hat.y += lift;
    }
}
