package io.github.potterplus.api.ui.button;

import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

/**
 * A simple automatically canceled GUI button.
 */
public class AutoUIButton extends UIButton {

    public AutoUIButton(ItemStack item) {
        super(item);

        this.setListener(event -> event.setCancelled(true));
    }

    public AutoUIButton(Supplier<ItemStack> item) {
        this(item.get());
    }
}
