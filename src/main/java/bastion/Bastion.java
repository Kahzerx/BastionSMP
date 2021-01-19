package bastion;

import bastion.commands.DiscordCommand;
import bastion.commands.HereCommand;
import bastion.commands.SBCommand;
import bastion.commands.WhereCommand;
import bastion.settings.BastionConfig;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

public class Bastion {

    public static BastionConfig config;

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher){
        DiscordCommand.register(dispatcher);
        HereCommand.register(dispatcher);
        WhereCommand.register(dispatcher);
        SBCommand.register(dispatcher);
    }
}
