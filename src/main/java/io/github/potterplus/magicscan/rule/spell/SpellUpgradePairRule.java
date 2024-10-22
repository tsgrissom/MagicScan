package io.github.potterplus.magicscan.rule.spell;

import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.magic.MagicSpell;
import io.github.potterplus.magicscan.rule.SpellRule;
import io.github.potterplus.magicscan.scan.Violation;
import io.github.potterplus.magicscan.scan.Violations;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;

/**
 * A simple rule which verifies that both the `upgrade_path` and `upgrade_required_casts` are set for leveled variants.
 */
public class SpellUpgradePairRule extends SpellRule {

    public SpellUpgradePairRule(MagicScanController controller) {
        super(controller);
    }

    @Override
    public String getKey() {
        return "upgrade_pair";
    }

    @Override
    public Collection<Violation> validate(MagicSpell spell) {
        if (!spell.isLeveledVariant()) {
            return Violations.none();
        }

        ConfigurationSection section = spell.getSpellSection();

        if (section == null) {
            throw new IllegalArgumentException("Spell section cannot be null!");
        }

        Violation path = null, casts = null;

        if (!section.isSet("upgrade_required_path")) {
            path = Violation.builder(this)
                    .message("upgrade_pair_path")
                    .build();
        }

        if (!section.isSet("upgrade_required_casts")) {
            path = Violation.builder(this)
                    .message("upgrade_pair_casts")
                    .build();
        }

        return Violations.ofNonNull(path, casts);
    }
}
