package io.github.potterplus.magicscan.rule.spell;

import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.magic.MagicSpell;
import io.github.potterplus.magicscan.rule.SpellRule;
import io.github.potterplus.magicscan.scan.Violation;
import io.github.potterplus.magicscan.scan.Violations;

import java.util.Collection;

/**
 * A simple rule which verifies if a spell has leveling/progression.
 */
public class SpellLevelsRule extends SpellRule {

    public SpellLevelsRule(MagicScanController controller) {
        super(controller);
    }

    @Override
    public String getKey() {
        return "levels";
    }

    @Override
    public Collection<Violation> validate(MagicSpell spell) {
        if (spell.isLeveledVariant() || spell.hasTag("noprogression")) {
            return Violations.none();
        }

        String key = spell.getKey();

        if (getController().resolveSpell(key + "|2").isPresent()) {
            return Violations.none();
        } else {
            return Violations.forRule(this);
        }
    }
}
