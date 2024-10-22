package io.github.potterplus.magicscan.rule.spell;

import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.magic.MagicSpell;
import io.github.potterplus.magicscan.rule.SpellRule;
import io.github.potterplus.magicscan.scan.Violation;
import io.github.potterplus.magicscan.scan.Violations;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collection;

/**
 * A simple rule which verifies that all spells that are level variants have an upgrade_description section in messages.
 */
public class SpellUpgradeDescriptionRule extends SpellRule {

    public SpellUpgradeDescriptionRule(MagicScanController controller) {
        super(controller);
    }

    @Override
    public String getKey() {
        return "upgrade_description";
    }

    @Override
    public Collection<Violation> validate(MagicSpell spell) {
        if (!spell.isLeveledVariant()) {
            return Violations.none();
        }

        String key = spell.getKey();
        FileConfiguration messagesDef = getController().getMessagesDefaults().getFileConfiguration();
        Object upgradeDescription = messagesDef.get("spells." + key + ".upgrade_description");

        if (upgradeDescription != null) {
            return Violations.none();
        } else {
            return Violations.forRule(this);
        }
    }
}
