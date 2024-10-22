package io.github.potterplus.magicscan.rule.spell;

import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.magic.MagicSpell;
import io.github.potterplus.magicscan.rule.SpellRule;
import io.github.potterplus.magicscan.scan.Violation;
import io.github.potterplus.magicscan.scan.Violations;

import java.util.Collection;

/**
 * A simple rule which verifies that all spells have names.
 */
public class SpellNameRule extends SpellRule {

    public SpellNameRule(MagicScanController controller) {
        super(controller);
    }

    @Override
    public String getKey() {
        return "name";
    }

    @Override
    public Collection<Violation> validate(MagicSpell spell) {
        return spell.getName().isPresent() ? Violations.none() : Violations.forRule(this);
    }
}
