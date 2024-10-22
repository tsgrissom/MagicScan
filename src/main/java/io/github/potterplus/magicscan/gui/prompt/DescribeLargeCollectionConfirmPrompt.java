package io.github.potterplus.magicscan.gui.prompt;

import io.github.potterplus.api.ui.prompt.ConfirmPrompt;
import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.misc.Describable;
import io.github.potterplus.magicscan.task.DescribeCollectionTask;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.Collection;

/**
 * TODO Write docs
 */
public class DescribeLargeCollectionConfirmPrompt extends ConfirmPrompt {

    private final MagicScanController controller;
    private final Collection<Describable> describables;
    private final CommandSender target;

    public DescribeLargeCollectionConfirmPrompt(MagicScanController controller, String title, Collection<Describable> describables, CommandSender target) {
        super(title);

        this.controller = controller;
        this.describables = describables;
        this.target = target;
    }

    @Override
    public void onConfirm(InventoryClickEvent event) {
        new DescribeCollectionTask(controller, new ArrayList<>(describables), target).runTask(controller.getPlugin());
    }

    @Override
    public void onCancel(Player player) {

    }
}
