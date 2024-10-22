package io.github.potterplus.magicscan.gui.describable;

import io.github.potterplus.api.ui.UserInterface;
import io.github.potterplus.api.ui.button.AutoUIButton;
import io.github.potterplus.api.ui.button.UIButton;
import io.github.potterplus.magicscan.misc.Describable;
import lombok.Setter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

/**
 * TODO Write docs
 */
public class DescribeDescribableGUI extends UserInterface {

    private Describable describable;
    private HumanEntity human;
    @Setter
    private UIButton button;

    public DescribeDescribableGUI(String title, Describable describable, HumanEntity human) {
        super(title, 9);

        this.describable = describable;
        this.human = human;

        ItemStack desc = describable.describeAsItem(human);
        final UIButton def = new AutoUIButton(desc);

        this.setButton(4, button == null ? def : button);
    }

    public void activate() {
        this.activate(human);
    }
}
