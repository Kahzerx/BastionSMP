package bastion.discord.commands;

import bastion.discord.utils.DiscordPermission;
import bastion.discord.utils.DiscordUtils;
import bastion.utils.FileManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.server.MinecraftServer;

public class Reload extends Commands {
    public Reload() {
        super.setCBody("reload");
        super.setPermission(DiscordPermission.ADMIN_CHAT);
    }

    @Override
    public void execute(MessageReceivedEvent event, MinecraftServer server) {
        if (DiscordUtils.isAllowed(this.getPermission(), event.getChannel().getIdLong())) {
            server.getPlayerManager().reloadWhitelist();
            server.kickNonWhitelistedPlayers(server.getCommandSource());
            FileManager.initializeYaml();
            event.getChannel().sendMessage("Reloaded!").queue();
        }
    }
}
