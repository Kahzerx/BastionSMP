package bastion.discord.utils;

import bastion.Bastion;
import bastion.discord.commands.*;
import com.mojang.authlib.GameProfile;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import javax.annotation.Nonnull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordListener extends ListenerAdapter {
    private static final Pattern url_patt = Pattern.compile("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)");
    private static JDA process = null;
    public static String channelId = "";
    public static String token = "";
    public static boolean chatBridge = false;

    MinecraftServer server;

    Commands online = new Online();
    Commands add = new Add();
    Commands remove = new Remove();
    Commands reload = new Reload();
    Commands list = new WList();

    public DiscordListener (MinecraftServer s){
        this.server = s;
    }

    public static void connect(MinecraftServer server, String t, String c){
        token = t;
        channelId = c;
        try{
            chatBridge = false;
            Bastion.config.setRunning(false);
            process = JDABuilder.createDefault(token).addEventListeners(new DiscordListener(server)).build();
            process.awaitReady();
            chatBridge = true;
            Bastion.config.setRunning(true);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (chatBridge){
            if (event.getAuthor().isBot()) return;
            if (event.getMessage().getContentDisplay().equals("")) return;
            if (event.getMessage().getContentRaw().equals("")) return;

            String prefix = "!";

            if (event.getMessage().getContentRaw().equals(prefix + online.getCBody())) {
                online.execute(event, server);
            }

            else if (event.getMessage().getContentRaw().startsWith(prefix + add.getCBody() + " ")) {  // Añadir a la whitelist
                add.execute(event, server);
            }

            else if (event.getMessage().getContentRaw().startsWith(prefix + remove.getCBody() + " ")) {  // Eliminar de la whitelist
                remove.execute(event, server);
            }

            else if (event.getMessage().getContentRaw().equals(prefix + reload.getCBody())) {  // Recargar la whitelist y archivo de configuración.
                reload.execute(event, server);
            }

            else if (event.getMessage().getContentRaw().equals(prefix + list.getCBody())) {  // Listar gente en la whitelist.
                list.execute(event, server);
            }

            else if (event.getChannel().getIdLong() == (Bastion.config.chatChannelId)) {
                DiscordUtils.sendMessage(event, server);
            }
        }
    }

    public static void sendMessage(String msg){
        if (chatBridge){
            try {
                TextChannel ch = process.getTextChannelById(channelId);
                if (ch != null) ch.sendMessage(msg).queue();
            }
            catch (Exception e){
                System.out.println("wrong channelId :(");
            }
        }
    }

    public static void sendMessageAdminChat(String user, String msg) {
        if (chatBridge && shouldFeedback(msg.split(" ")[0].substring(1))) {
            try {
                TextChannel ch = process.getTextChannelById(Bastion.config.adminChat);
                if (ch != null) ch.sendMessage(String.format("`%s` ha ejecutado `%s`", user, msg)).queue();
            }
            catch (Exception e){
                System.out.println("wrong channelId :(");
            }
        }
    }

    private static boolean shouldFeedback(String command) {
        for (String check : Bastion.config.commandWhitelist) {
            if (check.equalsIgnoreCase(command)) {
                return false;
            }
        }
        return !command.equals("");
    }

    public static void stop(){
        process.shutdownNow();
        chatBridge = false;
    }
}
