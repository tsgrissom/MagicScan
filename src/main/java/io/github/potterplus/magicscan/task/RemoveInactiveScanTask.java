package io.github.potterplus.magicscan.task;

import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.misc.Utilities;
import io.github.potterplus.magicscan.scan.Scan;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * A task to automatically remove scans deemed inactive.
 */
@RequiredArgsConstructor
public class RemoveInactiveScanTask extends BukkitRunnable {

    @NonNull
    private MagicScanController controller;

    @NonNull
    private CommandSender owner;

    @Override
    public void run() {
        Scan queuedScan = controller.getScanController().getQueuedScan(owner);

        if (queuedScan == null) {
            return;
        }

        CommandSender owner = queuedScan.getSender();

        controller.getScanController().removeScan(owner);

        if (this.owner != null && this.owner.equals(owner)) {
            if (Utilities.isMessageable(owner)) {
                controller.sendMessage(owner, "scan_timed_out");
            }
        }
    }
}
