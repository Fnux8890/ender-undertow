package dk.michado.undertow.mixin;

import dk.michado.undertow.*;

import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandBuildContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Commands.class)
abstract class CommandsMixin {
    @Inject(method="<init>",at=@At("RETURN"))
    private void undertow$register(Commands.CommandSelection selection, CommandBuildContext context, CallbackInfo ci) {
        ((Commands)(Object)this).getDispatcher().register(Commands.literal("undertow_client_ready").executes(ctx -> {
            ctx.getSource().getPlayerOrException().addTag("eu.cinematic");
            return 1;
        }));
    }
}
