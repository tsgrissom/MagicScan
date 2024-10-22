package io.github.potterplus.magicscan.rule.spell;

import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.magic.MagicSpell;
import io.github.potterplus.magicscan.rule.SpellRule;
import io.github.potterplus.magicscan.scan.Violation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A rule which indicates extra parameters, etc.
 */
public class SpellActionsRule extends SpellRule {

    public SpellActionsRule(MagicScanController controller) {
        super(controller);
    }

    @Override
    public String getKey() {
        return "actions";
    }

    @Override
    public Collection<Violation> validate(MagicSpell spell) {
        List<String> unmatched = spell.getUnmatchedParameters();
        List<Violation> violations = new ArrayList<>();

        if (unmatched != null && !unmatched.isEmpty()) {
            for (String str : unmatched) {
                Violation.Builder builder = Violation.builder(this)
                        .replace("$param", str);

                violations.add(builder.build());
            }
        }

        return violations;
    }
}
