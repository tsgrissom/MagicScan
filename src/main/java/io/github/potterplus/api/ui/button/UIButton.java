package io.github.potterplus.api.ui.button;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

/**
 * Represents a button on a GUI i.e. the container for the item and its subsequent event handling for button clicks.
 */
@RequiredArgsConstructor
public class UIButton {

    @Getter @Setter @NonNull
    private ItemStack item;

    @Getter @Setter
    private ButtonListener listener;

    public UIButton(Supplier<ItemStack> item) {
        this(item.get());
    }
}
