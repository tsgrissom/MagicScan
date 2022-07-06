package io.github.potterplus.api.storage.flatfile;

import io.github.potterplus.api.misc.PluginLogger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Represents a YAML file controlled by a specific plugin under its folder (ex. messages.yml).
 */
public class PluginYamlFile<T extends JavaPlugin> extends YamlFile {

    private final T plugin;

    public PluginYamlFile(T plugin, String fileName) {
        super(plugin.getDataFolder(), fileName);

        this.plugin = plugin;

        if (file == null) {
            InputStream is = plugin.getResource(fileName);

            if (is == null) {
                PluginLogger.atWarn()
                        .with("Could not read '%s' from JAR", fileName)
                        .print();

                return;
            }

            InputStreamReader reader = new InputStreamReader(is);

            fileConfiguration.addDefaults(YamlConfiguration.loadConfiguration(reader));

            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            file = new File(plugin.getDataFolder(), fileName);
        }
    }

    @Override
    public FileConfiguration getFileConfiguration() {
        if (fileConfiguration == null) {
            reload();
        }

        return fileConfiguration;
    }

    public void reload () {
        if (file == null) {
            this.file = new File(plugin.getDataFolder(), getFileName());
        }

        this.fileConfiguration = YamlConfiguration.loadConfiguration(file);
        InputStream is = plugin.getResource(getFileName());

        if (is != null) {
            Reader defConfigStream = new InputStreamReader(is, StandardCharsets.UTF_8);
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);

            fileConfiguration.setDefaults(defConfig);
        }
    }

    public void save() {
        if (file == null || fileConfiguration == null) {
            return;
        }

        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveDefault() {
        if (file == null) {
            file = new File(plugin.getDataFolder(), getFileName());
        }

        if (!file.exists()) {
            plugin.saveResource(getFileName(), false);
        }
    }
}
