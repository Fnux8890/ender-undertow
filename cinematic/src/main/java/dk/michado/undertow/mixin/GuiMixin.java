package dk.michado.undertow.mixin;

import dk.michado.undertow.Cinematic;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
abstract class GuiMixin {
    // Forge dispatches to its layered HUD. Draw after that call completes on either loader,
    // while retaining the normal screen and pause menu layers above the eyelids.
    @Redirect(method="extractRenderState", at=@At(value="INVOKE",
            target="Lnet/minecraft/client/gui/Hud;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"))
    private void undertow$finishHud(Hud hud, GuiGraphicsExtractor canvas, DeltaTracker delta) {
        hud.extractRenderState(canvas, delta);
        Cinematic.lids(canvas);
    }
}
