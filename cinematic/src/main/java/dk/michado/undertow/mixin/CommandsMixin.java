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
        var dispatcher = ((Commands)(Object)this).getDispatcher();
        dispatcher.register(Commands.literal("undertow_find_overlook")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ctx -> DiamondOverlook.find(ctx.getSource().getPlayerOrException())));
        dispatcher.register(Commands.literal("undertow_prepare_home")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ctx -> {
                    var player = ctx.getSource().getPlayerOrException();
                    var config = player.getRespawnConfig();
                    if (config == null || config.respawnData().dimension() != player.level().dimension()) return 0;
                    var pos = config.respawnData().pos();
                    var border = player.level().getWorldBorder();
                    if (!border.isWithinBounds(pos.offset(-2, 0, -2)) || !border.isWithinBounds(pos.offset(2, 0, 2))) return 0;
                    // Synchronous, bounded reads only on the rare home roll. No permanent tickets.
                    for (int x = (pos.getX()-2) >> 4; x <= (pos.getX()+2) >> 4; x++) {
                        for (int z = (pos.getZ()-2) >> 4; z <= (pos.getZ()+2) >> 4; z++) {
                            player.level().getChunk(x,z);
                        }
                    }
                    return 1;
                }));
        dispatcher.register(Commands.literal("undertow_client_ready").executes(ctx -> {
            ctx.getSource().getPlayerOrException().addTag("eu.cinematic");
            return 1;
        }));
    }
}
