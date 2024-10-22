package io.github.potterplus.magicscan.gui.prompt;

import io.github.potterplus.api.command.CommandContext;
import io.github.potterplus.api.command.CommandFlag;
import io.github.potterplus.api.item.Icon;
import io.github.potterplus.api.ui.prompt.ConfirmPrompt;
import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.scan.Scan;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * TODO Write docs
 */
public class OverwriteExistingScanConfirmPrompt extends ConfirmPrompt {

    private final MagicScanController controller;
    private final CommandContext context;

    public OverwriteExistingScanConfirmPrompt(MagicScanController controller, CommandContext context) {
        super("Overwrite existing scan?");

        this.controller = controller;
        this.context = context;

        if (!context.isPlayer()) {
            throw new IllegalArgumentException("Cannot create prompt for non-Player command context");
        }

        Player player = context.getPlayer();
        Scan scan = controller.getScanController().getQueuedScan(player);

        if (scan == null) {
            controller.sendMessage(player, "no_queued_scan");
            player.closeInventory();

            return;
        }

        this.setInfoItem(
                Icon
                        .of(scan.describeAsItem(player))
                        .name("&7Are you sure you want to overwrite your existing scan?")
        );
    }

    @Override
    public void onConfirm(InventoryClickEvent event) {
        controller.sendMessage(context, "previous_scan_deleted");
        controller.getScanController().removeScan(context.getPlayer());

        context.performCommand("magicscan scan create");

        CommandFlag uiFlag = new CommandFlag.UserInterfaceFlag();

        if (context.hasFlag(uiFlag)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    context.performCommand("magicscan scan manage");
                }
            }.runTaskLater(controller.getPlugin(), 20);
        }
    }

    @Override
    public void onCancel(Player player) {
        controller.sendMessage(player,"scan_not_deleted");
    }
}
