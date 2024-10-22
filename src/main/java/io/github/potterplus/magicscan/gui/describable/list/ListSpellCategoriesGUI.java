package io.github.potterplus.magicscan.gui.describable.list;

import com.google.common.collect.ImmutableMap;
import io.github.potterplus.api.ui.button.UIButton;
import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.gui.describable.ListDescribablesGUI;
import io.github.potterplus.magicscan.magic.spell.SpellCategory;
import org.bukkit.entity.HumanEntity;

import java.util.List;

/**
 * A GUI listing all of the spell categories.
 */
public class ListSpellCategoriesGUI extends ListDescribablesGUI {

    public ListSpellCategoriesGUI(HumanEntity target, MagicScanController controller) {
        super("Spell Categories", target, controller);
    }

    @Override
    public void initialize() {
        MagicScanController controller = getController();
        List<SpellCategory> cats = controller.getSpellCategories();

        for (SpellCategory cat : cats) {
            UIButton button = new UIButton(cat.describeAsItem(getTarget()));

            button.setListener(event -> {
                event.setCancelled(true);

                ListSpellsGUI gui = new ListSpellsGUI(getTarget(), getController(), cat.getKey());

                gui.update(event);
                gui.activate();
            });

            this.addButton(button);
        }

        this.setTitle(controller.getMessage("gui.list_spell_categories.title", ImmutableMap.of("$count", String.valueOf(getItems().size()))));
    }
}
