package dk.michado.undertow.mixin;

import dk.michado.undertow.*;
import net.minecraft.client.model.monster.enderman.EndermanModel;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.core.component.DataComponents;

import net.minecraft.client.renderer.entity.EndermanRenderer;
import net.minecraft.client.renderer.entity.state.EndermanRenderState;
import net.minecraft.world.entity.monster.Enderman;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndermanRenderer.class)
abstract class EndermanRendererMixin extends MobRenderer<Enderman, EndermanRenderState, EndermanModel<EndermanRenderState>> {
    private EndermanRendererMixin(EntityRendererProvider.Context context,
            EndermanModel<EndermanRenderState> model, float shadow) {
        super(context, model, shadow);
    }

    @Inject(method="<init>", at=@At("RETURN"))
    private void undertow$addGentlemanLayer(EntityRendererProvider.Context context, CallbackInfo ci) {
        addLayer(new GentlemanHeadLayer(this, context));
    }

    @Inject(method="extractRenderState(Lnet/minecraft/world/entity/monster/Enderman;Lnet/minecraft/client/renderer/entity/state/EndermanRenderState;F)V",at=@At("TAIL"))
    private void undertow$pose(Enderman mob, EndermanRenderState state, float partial, CallbackInfo ci) {
        var head = mob.getItemBySlot(EquipmentSlot.HEAD);
        ((JawState)state).undertow$gentleman(GentlemanHeadLayer.MODEL.equals(head.get(DataComponents.ITEM_MODEL)));
        ((JawState)state).undertow$grip(Cinematic.grip(mob));
        Cinematic.faceVictim(mob, state, partial);
        ((JawState)state).undertow$jaw(Cinematic.jaw(mob));
    }
}
