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
    @Unique private static final Identifier[] UNDERTOW_FOCUS = {
        Identifier.parse("ender_undertow:focus_soft"), Identifier.parse("ender_undertow:focus_deep")
    };

    @Inject(method="renderLevel", at=@At("TAIL"))
    private void undertow$focus(CallbackInfo ci) {
        double fatigue = Cinematic.fatigue();
        if (fatigue < 0.10) return;
        var shader = Minecraft.getInstance().getShaderManager().getPostChain(
                UNDERTOW_FOCUS[fatigue > 0.55 ? 1 : 0], LevelTargetBundle.MAIN_TARGETS);
        if (shader != null) shader.process(mainRenderTarget, resourcePool);
    }
}
