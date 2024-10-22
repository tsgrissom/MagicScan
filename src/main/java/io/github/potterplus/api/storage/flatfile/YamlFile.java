package io.github.potterplus.api.storage.flatfile;

import io.github.potterplus.api.misc.PluginLogger;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Represents a File and its YAML FileConfiguration.
 */
public class YamlFile {

    @Getter @NonNull
    private final File directory;

    @Getter @NonNull
    private final String fileName;

    @Getter
    protected File file;

    @Getter
    protected FileConfiguration fileConfiguration;

    public YamlFile(File directory, String fileName) {
        this.directory = directory;
        this.fileName = fileName;

        this.file = new File(directory, fileName);

        this.fileConfiguration = new YamlConfiguration();

        if (file.exists()) {
            try {
                fileConfiguration.load(file);
            } catch (InvalidConfigurationException | IOException e) {
                e.printStackTrace();
            }
        } else {
            PluginLogger.atWarn()
                    .with("Could not find '%s'. A new one will be created.", fileName)
                    .print();
        }
    }
}
