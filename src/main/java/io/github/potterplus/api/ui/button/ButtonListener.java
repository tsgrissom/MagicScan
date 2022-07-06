package io.github.potterplus.api.ui.button;

import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * An event handler interface for easily-expressed button press logic.
 */
public interface ButtonListener {

    void onClick(InventoryClickEvent event);
}
