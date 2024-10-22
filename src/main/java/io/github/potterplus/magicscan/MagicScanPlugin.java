package io.github.potterplus.magicscan;

import io.github.potterplus.api.ui.UserInterface;
import io.github.potterplus.magicscan.command.MagicScanCommand;
import io.github.potterplus.magicscan.listener.QuitListener;
import io.github.potterplus.magicscan.scan.Scan;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.command.Command;
import org.bukkit.plugin.java.annotation.command.Commands;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.dependency.DependsOn;
import org.bukkit.plugin.java.annotation.permission.ChildPermission;
import org.bukkit.plugin.java.annotation.permission.Permission;
import org.bukkit.plugin.java.annotation.permission.Permissions;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Description;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.Website;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

@Plugin(
        name = "MagicScan",
        version = "1.0.0"
)
@Description("Scans Magic configurations for potential issues.")
@Author("T0xicTyler")
@Website("https://github.com/PotterPlus/MagicScan")
@DependsOn({
        @Dependency("Magic")
})
@ApiVersion(ApiVersion.Target.v1_13)
@Commands({
        @Command(
                name = "magicscan",
                desc = "Initiates a scan of Magic configurations.",
                usage = "/magicscan <sub>",
                aliases = {"ms", "mss"}
        )
})
@Permissions({
        @Permission(name = "magicscan.*", desc = "Wildcard permission for /magicscan", children = {
                @ChildPermission(name = "magicscan.command"),
                @ChildPermission(name = "magicscan.command.reload"),
                @ChildPermission(name = "magicscan.command.scan"),
                @ChildPermission(name = "magicscan.command.scan.clear")
        }),
        @Permission(name = "magicscan.command", desc = "Permits to use /magicscan"),
        @Permission(name = "magicscan.command.reload", desc = "Permits to use /magicscan reload"),
        @Permission(name = "magicscan.command.scan", desc = "Permits to use /magicscan scan"),
        @Permission(name = "magicscan.command.scan.clear", desc = "Permits to use /magicscan scan clear"),
        @Permission(name = "magicscan.command.scan.delete", desc = "Permits to use /magicscan scan delete on others")
})
public class MagicScanPlugin extends JavaPlugin {

    @Getter @NonNull
    private MagicScanPlugin plugin;

    @Getter @NonNull
    private MagicScanController controller;

    @Getter @NonNull
    private MagicScanCommand command;

    @Override
    public void onEnable() {
        plugin = this;
        controller = new MagicScanController(this);

        ConfigurationSerialization.registerClass(Scan.class, "Scan");

        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new QuitListener(controller), this);

        UserInterface.prepare(this);

        this.command = new MagicScanCommand(this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}
