package bastion.utils;

import bastion.Bastion;
import bastion.settings.BastionConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    public static String jsonConfigFile = "config.json";

    public static void initializeJson() {
        File file = new File(jsonConfigFile);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (!file.exists()) {
            try {
                // Se ejecuta al crearse el archivo a modo de helper.
                List<Long> whitelistChat = new ArrayList<>();
                whitelistChat.add(1L);
                whitelistChat.add(2L);
                List<Long> allowedChat = new ArrayList<>();
                allowedChat.add(3L);
                allowedChat.add(4L);
                List<String> commandWhitelist = new ArrayList<>();
                commandWhitelist.add("commands_you_dont_want");
                commandWhitelist.add("to_admin_log");
                commandWhitelist.add("fill");

                Bastion.bastionConfig = new BastionConfig("", "", 0, false, false,
                        0, whitelistChat, allowedChat, commandWhitelist);
                FileWriter writer = new FileWriter(file);
                gson.toJson(Bastion.bastionConfig, writer);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                StringBuilder result = new StringBuilder();
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    result.append(line);
                }
                Bastion.bastionConfig = gson.fromJson(result.toString(), BastionConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateFile() {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter writer = new FileWriter(jsonConfigFile);
            gson.toJson(Bastion.bastionConfig, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
