package io.github.potterplus.magicscan.gui.describable;

import io.github.potterplus.api.ui.PaginatedUserInterface;
import io.github.potterplus.api.ui.button.AutoUIButton;
import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.misc.Describable;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.HumanEntity;

import java.util.List;

/**
 * Represents a paginated GUI which is intended to be populated with Describables.
 */
public abstract class ListDescribablesGUI extends PaginatedUserInterface {

    public static ListDescribablesGUI custom(MagicScanController controller, HumanEntity target, List<Describable> describables, String titleKey) {
        if (describables == null || describables.isEmpty()) {
            throw new NullPointerException("Cannot create custom list from null Describables!");
        }

        return new ListDescribablesGUI(controller.getMessage(titleKey), target, controller) {
            @Override
            public void initialize() {
                for (Describable d : describables) {
                    this.populate(d);
                }
            }
        };
    }

    @Getter @NonNull
    private final HumanEntity target;

    @Getter @NonNull
    private final MagicScanController controller;

    public ListDescribablesGUI(String name, HumanEntity target, MagicScanController controller) {
        super(name);

        this.target = target;
        this.controller = controller;

        this.initialize();
    }

    public void populate(Describable describable) {
        this.addButton(new AutoUIButton(describable.describeAsItem(this.target)));
    }

    public void activate() {
        this.target.openInventory(this.getInventory());
    }

    public abstract void initialize();
}
