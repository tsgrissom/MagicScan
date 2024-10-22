package io.github.potterplus.magicscan.gui.prompt;

import io.github.potterplus.api.item.Icon;
import io.github.potterplus.api.ui.prompt.ConfirmPrompt;
import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.gui.ListScansGUI;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * TODO Write docs
 */
public class ClearScansConfirmPrompt extends ConfirmPrompt {

    private final MagicScanController controller;
    private ListScansGUI returnTo;

    public ClearScansConfirmPrompt(MagicScanController controller) {
        super("Clear all scans?", Icon
                .start(Material.NAME_TAG)
                .name("&7Are you sure you want to clear all scans?")
                .lore("&3There are currently &b" + controller.getScanController().getQueuedScans().size() + " &3queued scans.", "&4Warning: &cThis cannot be undone!")
                .build());

        this.controller = controller;
    }

    public ClearScansConfirmPrompt(MagicScanController controller, ListScansGUI returnTo) {
        this(controller);

        this.returnTo = returnTo;
    }

    @Override
    public void onConfirm(InventoryClickEvent event) {
        HumanEntity human = event.getWhoClicked();

        controller.getScanController().clearScans(human);
        controller.sendMessage(human, "scans_cleared");

        if (returnTo != null) {
            returnTo.update(event);
        }
    }

    @Override
    public void onCancel(Player player) {
        controller.sendMessage(player, "scans_not_cleared");

        player.closeInventory();

        if (returnTo != null) {
            returnTo.update(player);
        }
    }
}
