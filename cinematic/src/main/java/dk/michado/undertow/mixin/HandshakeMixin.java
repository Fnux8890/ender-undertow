package dk.michado.undertow.mixin;

import dk.michado.undertow.*;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
abstract class HandshakeMixin {
    @Inject(method="handleCommands",at=@At("TAIL"))
    private void undertow$ready(ClientboundCommandsPacket packet, CallbackInfo ci) {
        undertow$advertise();
    }
    @Inject(method="handleRespawn",at=@At("TAIL"))
    private void undertow$respawn(ClientboundRespawnPacket packet, CallbackInfo ci) {
        Cinematic.clear();
        undertow$advertise();
    }
    @org.spongepowered.asm.mixin.Unique
    private void undertow$advertise() {
        ClientPacketListener connection=(ClientPacketListener)(Object)this;
        if (connection.getCommands().getRoot().getChild("undertow_client_ready") != null)
            connection.sendCommand("undertow_client_ready");
    }
}
