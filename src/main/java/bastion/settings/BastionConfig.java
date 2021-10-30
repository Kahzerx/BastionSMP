package bastion.settings;

import bastion.utils.FileManager;

import java.util.List;

public class BastionConfig {
    public String discordToken;
    public String chatBridgePrefix;
    public long chatChannelID;
    public boolean isRunning;
    public boolean adminLog;
    public long adminChatID;
    public List<Long> whitelistChat;
    public List<Long> allowedChat;
    public List<String> commandWhitelist;

    public BastionConfig(String discordToken, String chatBridgePrefix, long chatChannelID,
                         boolean isRunning, boolean adminLog, long adminChatID, List<Long> whitelistChat,
                         List<Long> allowedChat, List<String> commandWhitelist) {
        this.discordToken = discordToken;
        this.chatBridgePrefix = chatBridgePrefix;
        this.chatChannelID = chatChannelID;
        this.isRunning = isRunning;
        this.adminLog = adminLog;
        this.adminChatID = adminChatID;
        this.whitelistChat = whitelistChat;
        this.allowedChat = allowedChat;
        this.commandWhitelist = commandWhitelist;
    }

    public void setDiscordToken(String discordToken) {
        this.discordToken = discordToken;
        FileManager.updateFile();
    }

    public void setChatChannelID(long chatChannelID) {
        this.chatChannelID = chatChannelID;
        FileManager.updateFile();
    }

    public void setRunning(boolean running) {
        isRunning = running;
        FileManager.updateFile();
    }

    public long getChatChannelID() {
        return chatChannelID;
    }

    public String getDiscordToken() {
        return discordToken;
    }

    public void setChatBridgePrefix(String chatBridgePrefix) {
        this.chatBridgePrefix = chatBridgePrefix;
        FileManager.updateFile();
    }

    public long getAdminChatID() {
        return adminChatID;
    }

    public void setAdminLog(boolean adminLog) {
        this.adminLog = adminLog;
        FileManager.updateFile();
    }

    public void addWhitelist(long ID) {
        this.whitelistChat.add(ID);
        FileManager.updateFile();
    }

    public void removeWhitelist(long ID) {
        this.whitelistChat.remove(ID);
        FileManager.updateFile();
    }

    public void addAllowed(long ID) {
        this.allowedChat.add(ID);
        FileManager.updateFile();
    }

    public void removeAllowed(long ID) {
        this.allowedChat.remove(ID);
        FileManager.updateFile();
    }

    public void addCommand(String cmd) {
        this.commandWhitelist.add(cmd);
        FileManager.updateFile();
    }

    public void removeCommand(String cmd) {
        this.commandWhitelist.remove(cmd);
        FileManager.updateFile();
    }

    public List<Long> getWhitelistChat() {
        return whitelistChat;
    }

    public List<Long> getAllowedChat() {
        return allowedChat;
    }

    @Override
    public String toString() {
        return "Config{" +
                "discordToken='" + discordToken + '\'' +
                ", chatBridgePrefix='" + chatBridgePrefix + '\'' +
                ", chatChannelID=" + chatChannelID +
                ", isRunning=" + isRunning +
                ", adminLog=" + adminLog +
                ", adminChatID=" + adminChatID +
                ", whitelistChat=" + whitelistChat +
                ", allowedChat=" + allowedChat +
                ", commandWhitelist=" + commandWhitelist +
                '}';
    }
}
