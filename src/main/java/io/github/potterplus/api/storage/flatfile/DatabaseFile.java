package io.github.potterplus.api.storage.flatfile;

import org.bukkit.plugin.java.JavaPlugin;

public class DatabaseFile<T extends JavaPlugin> extends PluginYamlFile<T> {

    public DatabaseFile(T plugin) {
        super(plugin, "db.yml");
    }

    public String getHost() {
        return getFileConfiguration().getString("mysql.host");
    }

    public String getDatabase() {
        return getFileConfiguration().getString("mysql.db");
    }

    public String getUsername() {
        return getFileConfiguration().getString("mysql.user");
    }

    public String getPassword() {
        return getFileConfiguration().getString("mysql.pass");
    }
}
