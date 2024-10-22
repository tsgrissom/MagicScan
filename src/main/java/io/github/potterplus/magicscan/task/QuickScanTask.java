package io.github.potterplus.magicscan.task;

import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.scan.Scan;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * A task to quickly initiate and show the results of a scan.
 */
@RequiredArgsConstructor
public class QuickScanTask extends BukkitRunnable {

    @NonNull
    private MagicScanController controller;

    @NonNull
    private CommandSender initiator;

    @Getter
    private Scan scan;

    @Override
    public void run() {
        this.scan = new Scan(controller, initiator.getName());

        scan.getOptions().setScanAllRuleTypes(true);
        scan.getOptions().setVisual(initiator instanceof Player);
        scan.scan();
    }
}
