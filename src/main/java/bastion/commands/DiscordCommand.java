package bastion.commands;

import bastion.Bastion;
import bastion.discord.utils.DiscordListener;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

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
                        .then(CommandManager.literal("remove")
                                .then(CommandManager.argument("chatID", LongArgumentType.longArg())
                                        .executes(context -> removeId(context.getSource(), 0, LongArgumentType.getLong(context, "chatID"))))))
                .then(CommandManager.literal("allowedChat")
                        .then(CommandManager.literal("add")
                                .then(CommandManager.argument("chatID", LongArgumentType.longArg())
                                        .executes(context -> addId(context.getSource(), 1, LongArgumentType.getLong(context, "chatID")))))
                        .then(CommandManager.literal("remove")
                                .then(CommandManager.argument("chatID", LongArgumentType.longArg())
                                        .executes(context -> removeId(context.getSource(), 1, LongArgumentType.getLong(context, "chatID"))))))
                .then(CommandManager.literal("commandWhitelist"))
                        .then(CommandManager.literal("add")
                        .then(CommandManager.literal("remove")))
                .executes(context -> info(context.getSource())));
    }

    private static int setup(ServerCommandSource src, String token, long channelId){
        if (DiscordListener.chatBridge){
            src.sendFeedback(new LiteralText("Please stop the bot before you make any changes"), false);
        }
        else{
            Bastion.config.setDiscordToken(token);
            Bastion.config.setChatChannelId(channelId);
            src.sendFeedback(new LiteralText("Done!"), false);
        }
        return 1;
    }

    private static int stop(ServerCommandSource src){
        if (DiscordListener.chatBridge){
            DiscordListener.stop();
            Bastion.config.setRunning(false);
            src.sendFeedback(new LiteralText("Discord integration has stopped"), false);
        }
        else{
            src.sendFeedback(new LiteralText("Discord integration is already off"), false);
        }
        return 1;
    }

    private static int start(ServerCommandSource src){
        if (!DiscordListener.chatBridge){
            if (Bastion.config.chatChannelId != 0 && !Bastion.config.discordToken.equals("")) {
                try {
                    DiscordListener.connect(src.getMinecraftServer(), Bastion.config.discordToken, String.valueOf(Bastion.config.chatChannelId));
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

    private static int info(ServerCommandSource src){
        if (DiscordListener.chatBridge) src.sendFeedback(new LiteralText("Chat bridge is currently on!"), false);
        else src.sendFeedback(new LiteralText("Chat bridge is currently off!"), false);

        return 1;
    }

    private static int getPrefix(ServerCommandSource src) {
        src.sendFeedback(new LiteralText(Bastion.config.chatBridgePrefix.equals("") ? "There is no prefix." : "The prefix is " + Bastion.config.chatBridgePrefix + "."), false);
        return 1;
    }

    private static int setPrefix(ServerCommandSource src, String prefix) {
        Bastion.config.setChatBridgePrefix(prefix);
        src.sendFeedback(new LiteralText("Prefix is now " + prefix + "."), false);
        return 1;
    }

    private static int getAdminLog(ServerCommandSource src) {
        src.sendFeedback(new LiteralText(Bastion.config.adminLog ? "AdminLog is running." : "AdminLog is not running."), false);
        return 1;
    }

    private static int setAdminLog(ServerCommandSource src, boolean isRunning) {
        if (Bastion.config.adminChat == 0L) {
            src.sendFeedback(new LiteralText("Don't forget to set an adminChat too."), false);
        }

        if (isRunning) {
            Bastion.config.setAdminLog(true);
            src.sendFeedback(new LiteralText("AdminLog is on."), false);
        } else {
            Bastion.config.setAdminLog(false);
            src.sendFeedback(new LiteralText("AdminLog is off."), false);
        }
        return 1;
    }

    private static int addId(ServerCommandSource src, int id, long chatID) {
        if (id == 0) {
            if (Bastion.config.whitelistChat.contains(chatID)) {
                src.sendFeedback(new LiteralText("This ID already exists."), false);
            } else {
                Bastion.config.addWhitelist(chatID);
                src.sendFeedback(new LiteralText("ID added."), false);
            }
        } else if (id == 1) {
            if (Bastion.config.allowedChat.contains(chatID)) {
                src.sendFeedback(new LiteralText("This ID already exists."), false);
            } else {
                Bastion.config.addAllowed(chatID);
                src.sendFeedback(new LiteralText("ID added."), false);
            }
        }
        return 1;
    }

    private static int removeId(ServerCommandSource src, int id, long chatID) {
        if (id == 0) {
            if (!Bastion.config.whitelistChat.contains(chatID)) {
                src.sendFeedback(new LiteralText("This ID doesn't exist."), false);
            } else {
                Bastion.config.removeWhitelist(chatID);
                src.sendFeedback(new LiteralText("ID deleted."), false);
            }
        } else if (id == 1) {
            if (!Bastion.config.allowedChat.contains(chatID)) {
                src.sendFeedback(new LiteralText("This ID doesn't exist."), false);
            } else {
                Bastion.config.removeAllowed(chatID);
                src.sendFeedback(new LiteralText("ID deleted."), false);
            }
        }
        return 1;
    }
}
