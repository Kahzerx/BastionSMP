package bastion.utils;

import bastion.Bastion;
import bastion.settings.BastionConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    public static String yamlFile = "config.yaml";

    public static void initializeYaml() {
        File file = new File(yamlFile);
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
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
                Bastion.config = new BastionConfig("", "", 0, false, false, 0L, whitelistChat, allowedChat, commandWhitelist);
                mapper.writeValue(file, Bastion.config);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                Bastion.config = mapper.readValue(file, BastionConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateFile() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            mapper.writeValue(new File(yamlFile), Bastion.config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
