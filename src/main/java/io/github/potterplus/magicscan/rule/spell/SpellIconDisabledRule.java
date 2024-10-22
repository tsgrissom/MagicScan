package io.github.potterplus.magicscan.rule.spell;

import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.magic.MagicSpell;
import io.github.potterplus.magicscan.rule.SpellRule;
import io.github.potterplus.magicscan.scan.Violation;
import io.github.potterplus.magicscan.scan.Violations;

import java.util.Collection;

/**
 * A rule which verifies that a spell has a disabled icon.
 */
public class SpellIconDisabledRule extends SpellRule {

    public SpellIconDisabledRule(MagicScanController controller) {
        super(controller);
    }

    @Override
    public String getKey() {
        return "icon_disabled";
    }

    @Override
    public Collection<Violation> validate(MagicSpell spell) {
        if (spell.getDisabledIcon().isPresent()) return Violations.none();
        else return Violations.forRule(this);
    }
}
