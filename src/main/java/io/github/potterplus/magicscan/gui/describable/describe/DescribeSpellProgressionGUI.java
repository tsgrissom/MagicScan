package io.github.potterplus.magicscan.gui.describable.describe;

import com.google.common.collect.ImmutableMap;
import io.github.potterplus.api.item.Icon;
import io.github.potterplus.api.ui.UserInterface;
import io.github.potterplus.api.ui.button.AutoUIButton;
import io.github.potterplus.api.ui.button.UIButton;
import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.magic.MagicSpell;
import io.github.potterplus.magicscan.magic.spell.SpellProgression;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;

import java.util.List;

/**
 * A GUI describing a specific Magic spell's progression.
 */
public class DescribeSpellProgressionGUI extends UserInterface {

    private final HumanEntity target;

    public DescribeSpellProgressionGUI(MagicScanController controller, SpellProgression progression, HumanEntity target) {
        super(controller.getMessage("gui.describe_spell_progression.title", ImmutableMap.of("$key", progression.getOriginSpell().getKey())), 27);

        this.target = target;

        final UIButton arrow = new AutoUIButton(
                Icon
                .of(controller.getConfig().getIcon("right", Material.ARROW))
                .name(controller.getMessage("gui.describe_spell_progression.arrow.name"))
        );

        List<MagicSpell> progress = progression.getProgression();

        for (int i = 0; i < progress.size(); i++) {
            MagicSpell spell = progress.get(i);
            UIButton button = new AutoUIButton(spell.describeAsItem(target));

            this.addButton(button);

            if (i < progress.size()) {
                this.addButton(arrow);
            }
        }

        for (MagicSpell spell : progress) {
            UIButton button = new AutoUIButton(spell.describeAsItem(target));

            this.addButton(button);

            if (progress.indexOf(spell) != progress.size()) {
                this.addButton(arrow);
            }
        }
    }

    public void activate() {
        this.activate(target);
    }
}
