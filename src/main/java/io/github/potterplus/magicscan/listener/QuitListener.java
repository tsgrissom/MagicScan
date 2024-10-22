package io.github.potterplus.magicscan.listener;

import io.github.potterplus.magicscan.MagicScanController;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * When a player quits remove them from the scans map.
 */
@RequiredArgsConstructor
public class QuitListener implements Listener {

    @NonNull
    private MagicScanController controller;

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        controller.getScanController().getQueuedScans().remove(event.getPlayer().getUniqueId().toString());
    }
}
