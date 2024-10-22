package io.github.potterplus.magicscan.scan;

import com.google.common.collect.ImmutableMap;
import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.misc.Utilities;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * A controller for containing and managing scans.
 */
public class ScanController {

    @NonNull
    private final MagicScanController controller;

    @Getter @Setter
    private Map<String, Scan> queuedScans;

    public ScanController(MagicScanController controller) {
        this.controller = controller;
        this.queuedScans = new HashMap<>();
    }

    /**
     * Gets the CommandSender's queued scan if it exists.
     * @param sender The CommandSender.
     * @return The Scan of the CommandSender.
     */
    public Scan getQueuedScan(CommandSender sender) {
        return this.queuedScans.get(sender.getName());
    }

    /**
     * Checks if the CommandSender has a queued scan.
     * @param sender The CommandSender to check.
     * @return Whether or not the CommandSender has a queued scan.
     */
    public boolean hasScan(CommandSender sender) {
        return this.queuedScans.containsKey(sender.getName());
    }

    /**
     * Updates the CommandSender's queued scan.
     * @param sender The CommandSender to update.
     * @param scan The Scan to use.
     */
    public void putScan(CommandSender sender, Scan scan) {
        String name = sender.getName();

        removeScan(sender);

        this.queuedScans.put(name, scan);
    }

    /**
     * Removes the CommandSender's queued scan if it exists.
     * @param sender The CommandSender.
     */
    public void removeScan(CommandSender sender) {
        this.queuedScans.remove(sender.getName());
    }

    /**
     * Clears all queued scans and attempts to notify the creators.
     * @param sender Who is clearing the scans.
     */
    public void clearScans(CommandSender sender) {
        for (Map.Entry<String, Scan> entry : getQueuedScans().entrySet()) {
            String key = entry.getKey();
            Player player = Bukkit.getPlayer(key);

            if (Utilities.isMessageable(player)) {
                controller.sendMessage(player, "scan_cleared", ImmutableMap.of("$name", sender.getName()));
            }
        }

        this.queuedScans.clear();
    }
}
