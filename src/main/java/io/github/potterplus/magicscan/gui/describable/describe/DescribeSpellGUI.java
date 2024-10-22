package io.github.potterplus.magicscan.gui.describable.describe;

import com.google.common.collect.ImmutableMap;
import io.github.potterplus.magicscan.gui.describable.DescribeDescribableGUI;
import io.github.potterplus.magicscan.magic.MagicSpell;
import org.bukkit.entity.HumanEntity;

/**
 * A GUI describing a specific Magic spell.
 */
public class DescribeSpellGUI extends DescribeDescribableGUI {

    public DescribeSpellGUI(MagicSpell spell, HumanEntity human) {
        super(spell.getController().getMessage("gui.describe_spell.title", ImmutableMap.of("$key", spell.getKey())), spell, human);
    }
}
