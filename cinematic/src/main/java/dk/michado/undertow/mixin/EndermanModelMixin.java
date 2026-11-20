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
        float lift = ((JawState)state).undertow$jaw()*5;
        EndermanModel<?> model=(EndermanModel<?>)(Object)this;
        float grip = ((JawState)state).undertow$grip();
        if (grip > 0) {
            // Bring the two long forearms from the shoulders toward the miner's chest.
            model.rightArm.xRot += (-1.32f - model.rightArm.xRot) * grip;
            model.leftArm.xRot += (-1.38f - model.leftArm.xRot) * grip;
            model.rightArm.zRot += 0.12f * grip;
            model.leftArm.zRot -= 0.12f * grip;
        }
        if (state.isCreepy) return;
        model.head.y -= lift;
        model.hat.y += lift;
    }
}
