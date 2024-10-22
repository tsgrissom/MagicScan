package io.github.potterplus.magicscan.rule;

import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.magic.MagicWand;

/**
 * Represents a wand rule.
 */
public abstract class WandRule extends Rule<MagicWand> {

    public WandRule(MagicScanController controller) {
        super(controller);
    }

    @Override
    public RuleType getRuleType() {
        return RuleType.WAND;
    }
}
