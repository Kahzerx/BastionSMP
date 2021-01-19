package bastion.settings;

import bastion.utils.FileManager;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class BastionConfig {
    public BastionConfig(String discordToken, String chatBridgePrefix, long chatChannelId, boolean isRunning, boolean adminLog, long adminChat, List<Long> whitelistChat, List<Long> allowedChat, List<String> commandWhitelist) {
        this.discordToken = discordToken;
        this.chatBridgePrefix = chatBridgePrefix;
        this.chatChannelId = chatChannelId;
        this.isRunning = isRunning;
        this.adminLog = adminLog;
        this.adminChat = adminChat;
        this.whitelistChat = whitelistChat;
        this.allowedChat = allowedChat;
        this.commandWhitelist = commandWhitelist;
    }

    public BastionConfig() {}

    public String discordToken;
    public String chatBridgePrefix;
    public long chatChannelId;
    public boolean isRunning;
    public boolean adminLog;
    public long adminChat;
    public List<Long> whitelistChat;
    public List<Long> allowedChat;
    public List<String> commandWhitelist;

    public void setDiscordToken(String discordToken) {
        this.discordToken = discordToken;
        FileManager.updateFile();
    }

    public void setChatChannelId(long chatChannelId) {
        this.chatChannelId = chatChannelId;
        FileManager.updateFile();
    }

    public void setRunning(boolean running) {
        isRunning = running;
        FileManager.updateFile();
    }

    public void setChatBridgePrefix(String chatBridgePrefix) {
        this.chatBridgePrefix = chatBridgePrefix;
        FileManager.updateFile();
    }

    public void setAdminLog(boolean adminLog) {
        this.adminLog = adminLog;
        FileManager.updateFile();
    }

    public void addWhitelist(long chatID) {
        whitelistChat.add(chatID);
        FileManager.updateFile();
    }

    public void addAllowed(long chatID) {
        allowedChat.add(chatID);
        FileManager.updateFile();
    }

    public void removeWhitelist(long chatID) {
        whitelistChat.remove(chatID);
        FileManager.updateFile();
    }

    public void removeAllowed(long chatID) {
        allowedChat.remove(chatID);
        FileManager.updateFile();
    }
}
