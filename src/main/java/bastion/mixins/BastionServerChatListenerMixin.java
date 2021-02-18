package bastion.mixins;

import bastion.Bastion;
import bastion.discord.utils.DiscordListener;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class BastionServerChatListenerMixin {
    @Shadow public ServerPlayerEntity player;

    @Inject(method = "onGameMessage", at = @At("RETURN"))
    public void chatMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
        if (!packet.getChatMessage().startsWith("/")) DiscordListener.sendMessage(Bastion.bastionConfig.chatBridgePrefix + " `<" + player.getName().getString() + ">` " + packet.getChatMessage());
        else if (Bastion.bastionConfig.adminLog) {
            DiscordListener.sendMessageAdminChat(player.getName().getString(), packet.getChatMessage());
        }
    }
}
