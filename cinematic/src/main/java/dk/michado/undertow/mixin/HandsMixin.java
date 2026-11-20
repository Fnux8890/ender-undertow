package dk.michado.undertow.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dk.michado.undertow.Cinematic;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
abstract class HandsMixin {
    @Inject(method="submitHandsWithItems", at=@At("HEAD"))
    private void undertow$lowerHands(float partial, PoseStack pose, SubmitNodeCollector collector,
            PlayerRenderState player, FirstPersonHandsAndItemsRenderState hands, CallbackInfo ci) {
        pose.translate(0, -0.95 * Cinematic.handDrop(), 0);
    }
}
