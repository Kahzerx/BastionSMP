package bastion.mixins;

import bastion.Bastion;
import bastion.discord.utils.DiscordListener;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class BastionPlayerManagerOnPlayerJoinMixin {
    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void onPlayerJoin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if (DiscordListener.chatBridge) DiscordListener.sendMessage(Bastion.bastionConfig.chatBridgePrefix + " :arrow_right: **" + player.getName().getString().replace("_", "\\_") + " joined the game!**");
    }
}
