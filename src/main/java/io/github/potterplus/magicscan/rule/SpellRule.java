package io.github.potterplus.magicscan.rule;

import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.magic.MagicSpell;

/**
 * Represents a spell rule.
 */
public abstract class SpellRule extends Rule<MagicSpell> {

    public SpellRule(MagicScanController controller) {
        super(controller);
    }

    @Override
    public RuleType getRuleType() {
        return RuleType.SPELL;
    }
}
