package bastion.commands;

import bastion.Bastion;
import bastion.discord.utils.DiscordListener;
import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import java.util.Collection;
import java.util.Set;

import static net.minecraft.server.command.CommandManager.literal;

public class DiscordCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("discord")
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                .then(CommandManager.literal("setBot")
                        .then(CommandManager.argument("token", StringArgumentType.string())
                                .then(CommandManager.argument("channelId", LongArgumentType.longArg())
                                        .executes(context -> setup(context.getSource(), StringArgumentType.getString(context, "token"), LongArgumentType.getLong(context, "channelId"))))))
                .then(CommandManager.literal("stop")
                        .executes(context -> stop(context.getSource())))
                .then(CommandManager.literal("start")
                        .executes(context -> start(context.getSource())))
                .then(CommandManager.literal("chatBridgePrefix")
                        .then(CommandManager.argument("prefix", StringArgumentType.string())
                                .executes(context -> setPrefix(context.getSource(), StringArgumentType.getString(context, "prefix"))))
                        .executes(context -> getPrefix(context.getSource())))
                .then(CommandManager.literal("adminLog")
                        .then(CommandManager.argument("running", BoolArgumentType.bool())
                                .executes(context -> setAdminLog(context.getSource(), BoolArgumentType.getBool(context, "running"))))
                        .executes(context -> getAdminLog(context.getSource())))
                .then(CommandManager.literal("whitelistChat")
                        .then(CommandManager.literal("add")
                                .then(CommandManager.argument("chatID", LongArgumentType.longArg())
                                        .executes(context -> addId(context.getSource(), 0, LongArgumentType.getLong(context, "chatID")))))
                        .then(CommandManager.literal("list")
                                .executes(context -> getWhitelistChat(context.getSource())))
                        .then(CommandManager.literal("remove")
                                .then(CommandManager.argument("chatID", LongArgumentType.longArg())
                                        .executes(context -> removeId(context.getSource(), 0, LongArgumentType.getLong(context, "chatID"))))))
                .then(CommandManager.literal("allowedChat")
                        .then(CommandManager.literal("add")
                                .then(CommandManager.argument("chatID", LongArgumentType.longArg())
                                        .executes(context -> addId(context.getSource(), 1, LongArgumentType.getLong(context, "chatID")))))
                        .then(CommandManager.literal("list")
                                .executes(context -> getAllowedChat(context.getSource())))
                        .then(CommandManager.literal("remove")
                                .then(CommandManager.argument("chatID", LongArgumentType.longArg())
                                        .executes(context -> removeId(context.getSource(), 1, LongArgumentType.getLong(context, "chatID"))))))
                .then(CommandManager.literal("commandWhitelist")
                        .then(CommandManager.literal("add")
                                .then(CommandManager.argument("command", StringArgumentType.string())
                                        .executes(context -> addCommand(context.getSource(), StringArgumentType.getString(context, "command")))))
                        .then(CommandManager.literal("list")
                                        .executes(context -> getCommandWhitelist(context.getSource())))
                        .then(CommandManager.literal("remove")
                                .then(CommandManager.argument("command", StringArgumentType.string())
                                        .suggests((c, b) -> CommandSource.suggestMatching(commandWhitelist(), b))
                                        .executes(context -> removeCommand(context.getSource(), StringArgumentType.getString(context, "command"))))))
                .executes(context -> info(context.getSource())));
    }

    private static int setup(ServerCommandSource src, String token, long channelId){
        if (DiscordListener.chatBridge){
            src.sendFeedback(new LiteralText("Please stop the bot before you make any changes"), false);
        }
        else{
            Bastion.bastionConfig.setDiscordToken(token);
            Bastion.bastionConfig.setChatChannelID(channelId);
            src.sendFeedback(new LiteralText("Done!"), false);
        }
        return 1;
    }

    private static int stop(ServerCommandSource src){
        if (DiscordListener.chatBridge){
            DiscordListener.stop();
            Bastion.bastionConfig.setRunning(false);
            src.sendFeedback(new LiteralText("Discord integration has stopped"), false);
        }
        else{
            src.sendFeedback(new LiteralText("Discord integration is already off"), false);
        }
        return 1;
    }

    private static int start(ServerCommandSource src){
        if (!DiscordListener.chatBridge){
            if (Bastion.bastionConfig.getChatChannelID() != 0 && !Bastion.bastionConfig.discordToken.equals("")) {
                try {
                    DiscordListener.connect(src.getMinecraftServer(), Bastion.bastionConfig.getDiscordToken(), String.valueOf(Bastion.bastionConfig.getChatChannelID()));
                    src.sendFeedback(new LiteralText("Discord integration is running"), false);
                } catch (Exception e) {
                    e.printStackTrace();
                    src.sendFeedback(new LiteralText("Unable to start the process, is the token correct?"), false);
                }
            }
            else{
                src.sendFeedback(new LiteralText("Set up a bot first please"), false);
            }
        }
        else{
            src.sendFeedback(new LiteralText("Discord integration is already on"), false);
        }
        return 1;
    }

    private static int info(ServerCommandSource src) {
        if (DiscordListener.chatBridge) src.sendFeedback(new LiteralText("Chat bridge is currently on!"), false);
        else src.sendFeedback(new LiteralText("Chat bridge is currently off!"), false);

        return 1;
    }

    private static int getPrefix(ServerCommandSource src) {
        src.sendFeedback(new LiteralText(Bastion.bastionConfig.chatBridgePrefix.equals("") ? "There is no prefix." : "The prefix is " + Bastion.bastionConfig.chatBridgePrefix + "."), false);
        return 1;
    }

    private static int setPrefix(ServerCommandSource src, String prefix) {
        Bastion.bastionConfig.setChatBridgePrefix(prefix);
        src.sendFeedback(new LiteralText("Prefix is now " + prefix + "."), false);
        return 1;
    }

    private static int getAdminLog(ServerCommandSource src) {
        src.sendFeedback(new LiteralText(Bastion.bastionConfig.adminLog ? "AdminLog is running." : "AdminLog is not running."), false);
        return 1;
    }

    private static int setAdminLog(ServerCommandSource src, boolean isRunning) {
        if (Bastion.bastionConfig.getAdminChatID() == 0L) {
            src.sendFeedback(new LiteralText("Don't forget to set an adminChat too."), false);
        }

        if (isRunning) {
            Bastion.bastionConfig.setAdminLog(true);
            src.sendFeedback(new LiteralText("AdminLog is on."), false);
        } else {
            Bastion.bastionConfig.setAdminLog(false);
            src.sendFeedback(new LiteralText("AdminLog is off."), false);
        }
        return 1;
    }

    private static int addId(ServerCommandSource src, int id, long chatID) {
        if (id == 0) {
            if (Bastion.bastionConfig.whitelistChat.contains(chatID)) {
                src.sendFeedback(new LiteralText("This ID already exists."), false);
            } else {
                Bastion.bastionConfig.addWhitelist(chatID);
                src.sendFeedback(new LiteralText("ID added."), false);
            }
        } else if (id == 1) {
            if (Bastion.bastionConfig.allowedChat.contains(chatID)) {
                src.sendFeedback(new LiteralText("This ID already exists."), false);
            } else {
                Bastion.bastionConfig.addAllowed(chatID);
                src.sendFeedback(new LiteralText("ID added."), false);
            }
        }
        return 1;
    }

    private static int removeId(ServerCommandSource src, int id, long chatID) {
        if (id == 0) {
            if (!Bastion.bastionConfig.whitelistChat.contains(chatID)) {
                src.sendFeedback(new LiteralText("This ID doesn't exist."), false);
            } else {
                Bastion.bastionConfig.removeWhitelist(chatID);
                src.sendFeedback(new LiteralText("ID removed."), false);
            }
        } else if (id == 1) {
            if (!Bastion.bastionConfig.allowedChat.contains(chatID)) {
                src.sendFeedback(new LiteralText("This ID doesn't exist."), false);
            } else {
                Bastion.bastionConfig.removeAllowed(chatID);
                src.sendFeedback(new LiteralText("ID removed."), false);
            }
        }
        return 1;
    }

    private static int addCommand(ServerCommandSource src, String command) {
        if (Bastion.bastionConfig.commandWhitelist.contains(command)) {
            src.sendFeedback(new LiteralText("This command is already whitelisted."), false);
        } else {
            Bastion.bastionConfig.addCommand(command);
            src.sendFeedback(new LiteralText("Command added."), false);
        }
        return 1;
    }

    private static int removeCommand(ServerCommandSource src, String command) {
        if (!Bastion.bastionConfig.commandWhitelist.contains(command)) {
            src.sendFeedback(new LiteralText("This command wasn't whitelisted."), false);
        } else {
            Bastion.bastionConfig.removeCommand(command);
            src.sendFeedback(new LiteralText("Command removed."), false);
        }
        return 1;
    }

    private static int getCommandWhitelist(ServerCommandSource src) {
        src.sendFeedback(new LiteralText("" + Bastion.bastionConfig.commandWhitelist), false);
        return 1;
    }

    private static int getWhitelistChat(ServerCommandSource src) {
        src.sendFeedback(new LiteralText(Bastion.bastionConfig.whitelistChat.toString()), false);
        return 1;
    }

    private static int getAllowedChat(ServerCommandSource src) {
        src.sendFeedback(new LiteralText(Bastion.bastionConfig.allowedChat.toString()), false);
        return 1;
    }

    public static Collection<String> commandWhitelist() {
        Set<String> commands = Sets.newLinkedHashSet();
        commands.addAll(Bastion.bastionConfig.commandWhitelist);
        return commands;
    }
}
