package io.github.potterplus.api.storage.flatfile;

import io.github.potterplus.api.string.StringUtilities;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

public class MessagesFile<T extends JavaPlugin> extends PluginYamlFile<T> {

    private final T plugin;

    public MessagesFile(T plugin) {
        super(plugin, "messages.yml");

        this.plugin = plugin;
    }

    public String getRawMessage(String key) {
        String str = getFileConfiguration().getString(key);

        if (str == null) {
            InputStream def = plugin.getResource("messages.yml");

            if (def != null) {
                Reader reader = new InputStreamReader(def);
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(reader);

                str = yaml.getString(key);

                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (str == null) {
                throw new IllegalArgumentException(String.format("Could not resolve message from key '%s'", key));
            }
        }

        return str;
    }

    public List<String> getMessageBlock(String node) {
        return StringUtilities.color(getFileConfiguration().getStringList(node));
    }

    public String getMessage(String node) {
        return StringUtilities.color(getRawMessage(node));
    }

    public String getStrippedMessage(String node) {
        return ChatColor.stripColor(getMessage(node));
    }

    public void sendMessageBlock(CommandSender to, String node) {
        getMessageBlock(node).forEach(to::sendMessage);
    }

    public void sendMessageBlock(CommandSender to, String node, Map<String, String> replace) {
        getMessageBlock(node).stream().map(s -> StringUtilities.replace(s, replace)).forEach(to::sendMessage);
    }

    public void sendMessage(CommandSender to, String node) {
        sendMessage(to, node, null);
    }

    public void sendMessage(CommandSender to, String node, Map<String, String> replace) {
        String message = getMessage(node);

        message = StringUtilities.color(message);

        if (replace != null) {
            message = StringUtilities.replace(message, replace);
        }

        to.sendMessage(message);
    }
}
