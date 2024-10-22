package io.github.potterplus.magicscan.rule.spell;

import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.magic.MagicSpell;
import io.github.potterplus.magicscan.rule.SpellRule;
import io.github.potterplus.magicscan.scan.Violation;
import io.github.potterplus.magicscan.scan.Violations;

import java.util.Collection;

/**
 * A rule which verifies that a spell has a primary icon.
 */
public class SpellIconRule extends SpellRule {

    public SpellIconRule(MagicScanController controller) {
        super(controller);
    }

    @Override
    public String getKey() {
        return "icon";
    }

    @Override
    public Collection<Violation> validate(MagicSpell spell) {
        if (spell.getIcon().isPresent()) return Violations.none();
        else return Violations.forRule(this);
    }
}
