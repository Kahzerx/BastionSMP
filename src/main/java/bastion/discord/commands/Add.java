package bastion.discord.commands;

import bastion.discord.utils.DiscordPermission;
import bastion.discord.utils.DiscordUtils;
import com.mojang.authlib.GameProfile;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WhitelistEntry;

public class Add extends Commands {
    public Add() {
        super.setCBody("add");
        super.setPermission(DiscordPermission.WHITELIST_CHAT);
    }

    @Override
    public void execute(MessageReceivedEvent event, MinecraftServer server) {
        if (DiscordUtils.isAllowed(this.getPermission(), event.getChannel().getIdLong())) {
            String[] req = event.getMessage().getContentRaw().split(" ");
            String playerName = req[1];

            if (req.length != 2) {  // El comando es !add Kahzerx.
                event.getChannel().sendMessage("!add <playerName>").queue();
                return;
            }

            GameProfile gameProfile = server.getUserCache().findByName(playerName);

            if (gameProfile == null) {  // El Jugador es premium.
                event.getChannel().sendMessage("No es premium :P").queue();
                return;
            }

            Whitelist whitelist = server.getPlayerManager().getWhitelist();

            if (whitelist.isAllowed(gameProfile)) {  // Si ya estaba en la whitelist.
                event.getChannel().sendMessage("Ya estaba en whitelist").queue();
                return;
            }

            WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile);
            whitelist.add(whitelistEntry);  // Añadir a la whitelist vanilla.
            event.getChannel().sendMessage("Añadido :)").queue();
        }
    }
}
