package io.github.potterplus.magicscan.gui.prompt;

import io.github.potterplus.api.ui.prompt.ConfirmPrompt;
import io.github.potterplus.magicscan.scan.Scan;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * TODO Write docs
 */
public class ExecuteScanConfirmPrompt extends ConfirmPrompt {

    private final Scan scan;
    private final HumanEntity target;

    public ExecuteScanConfirmPrompt(Scan scan, HumanEntity target) {
        super("Execute scan?", scan.describeAsItem(target));

        this.scan = scan;
        this.target = target;
    }

    @Override
    public void onConfirm(InventoryClickEvent event) {
        scan.scan();
    }

    @Override
    public void onCancel(Player player) {
        scan.getController().sendMessage(target, "scan_not_executed");
    }
}
