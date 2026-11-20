package dk.michado.undertow.mixin;

import dk.michado.undertow.*;

import net.minecraft.network.Connection;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
abstract class JoinMixin {
    @Inject(method="placeNewPlayer",at=@At("RETURN"))
    private void undertow$reset(Connection conn, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci) {
        player.removeTag("eu.cinematic");
        if (player.removeTag("eu.in_scene")) {
            var scores = player.level().getScoreboard();
            var hold = scores.getObjective("eu.hold");
            if (hold != null) scores.resetSinglePlayerScore(player,hold);
        }
    }
}
