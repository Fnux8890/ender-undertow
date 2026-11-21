package dk.michado.undertow.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.CrossFrameResourcePool;
import dk.michado.undertow.Cinematic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
abstract class FocusMixin {
    @Shadow @Final private RenderTarget mainRenderTarget;
    @Shadow @Final private CrossFrameResourcePool resourcePool;
    @Unique private static final Identifier[] UNDERTOW_FOCUS = java.util.stream.IntStream.rangeClosed(0, 32)
            .mapToObj(i -> Identifier.parse("ender_undertow:focus_" + i)).toArray(Identifier[]::new);

    @Inject(method="renderLevel", at=@At("TAIL"))
    private void undertow$focus(CallbackInfo ci) {
        double strength = Minecraft.getInstance().options.screenEffectScale().get();
        if (strength < 0.5) return;
        int step = (int)Math.round(32 * strength * Cinematic.defocus());
        if (step == 0) return;
        var shader = Minecraft.getInstance().getShaderManager().getPostChain(
                UNDERTOW_FOCUS[step], LevelTargetBundle.MAIN_TARGETS);
        if (shader != null) shader.process(mainRenderTarget, resourcePool);
    }
}
