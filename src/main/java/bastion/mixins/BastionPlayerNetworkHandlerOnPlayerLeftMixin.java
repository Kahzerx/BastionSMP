package bastion.mixins;

import bastion.Bastion;
import bastion.discord.utils.DiscordListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class BastionPlayerNetworkHandlerOnPlayerLeftMixin {
    @Shadow
    public ServerPlayerEntity player;
    @Inject(method = "onDisconnected", at = @At("RETURN"))
    private void onPlayerLeft(Text reason, CallbackInfo ci){
        if (DiscordListener.chatBridge) DiscordListener.sendMessage(Bastion.bastionConfig.chatBridgePrefix + " :arrow_left: **" + player.getName().getString().replace("_", "\\_") + " left the game!**");
    }
}
