package bastion.discord.commands;

import bastion.discord.utils.DiscordPermission;
import bastion.discord.utils.DiscordUtils;
import com.mojang.authlib.GameProfile;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WhitelistEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

public class Remove extends Commands {
    public Remove() {
        super.setCBody("remove");
        super.setPermission(DiscordPermission.WHITELIST_CHAT);
    }

    @Override
    public void execute(MessageReceivedEvent event, MinecraftServer server) {
        if (DiscordUtils.isAllowed(this.getPermission(), event.getChannel().getIdLong())) {
            String[] req = event.getMessage().getContentRaw().split(" ");
            String playerName = req[1];

            if (req.length != 2) {  // El comando es !remove Kahzerx.
                event.getChannel().sendMessage("!remove <playerName>").queue();
                return;
            }

            Whitelist whitelist = server.getPlayerManager().getWhitelist();
            GameProfile gameProfile = server.getUserCache().findByName(playerName);

            if (gameProfile == null) {  // El Jugador es premium.
                event.getChannel().sendMessage("No es premium :P").queue();
                return;
            }

            if (!whitelist.isAllowed(gameProfile)) {
                event.getChannel().sendMessage("No está en la whitelist").queue();
                return;
            }

            WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile);  // Sacar de la whitelist vanilla.
            whitelist.remove(whitelistEntry);
            ServerPlayerEntity serverPlayerEntity = server.getPlayerManager().getPlayer(gameProfile.getId());

            if (serverPlayerEntity != null) {
                serverPlayerEntity.networkHandler.disconnect(new TranslatableText("multiplayer.disconnect.not_whitelisted"));  // kickear si está conectado.
            }

            event.getChannel().sendMessage("Eliminado ;(").queue();
        }
    }
}
