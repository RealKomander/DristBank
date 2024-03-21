package dristmine.dristbank;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class MessageManager {
    private final JavaPlugin plugin;
    private FileConfiguration messagesConfig;

    public MessageManager(JavaPlugin plugin) {
        this.plugin = plugin;
        reloadMessagesConfig();
    }

    public void reloadMessagesConfig() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        // Load default messages from the JAR if not found in the file
        try (Reader defaultMessagesReader = new InputStreamReader(plugin.getResource("messages.yml"), StandardCharsets.UTF_8)) {
            YamlConfiguration defaultMessagesConfig = YamlConfiguration.loadConfiguration(defaultMessagesReader);
            messagesConfig.setDefaults(defaultMessagesConfig);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to load default messages from JAR.");
        }
    }

    public String getMessage(String key) {
        String message = messagesConfig.getString(key);
        if (message != null) {
            return ChatColor.translateAlternateColorCodes('&', message);
        } else {
            plugin.getLogger().warning("Message not found for key: " + key);
            return "";
        }
    }

    public String getMessage(String key, Object... placeholders) {
        String message = messagesConfig.getString(key);
        if (message != null) {
            message = ChatColor.translateAlternateColorCodes('&', message);
            if (placeholders != null) {
                for (int i = 0; i < placeholders.length; i++) {
                    message = message.replace("{" + i + "}", placeholders[i].toString());
                }
            }
            return message;
        } else {
            plugin.getLogger().warning("Message not found for key: " + key);
            return "";
        }
    }
}
