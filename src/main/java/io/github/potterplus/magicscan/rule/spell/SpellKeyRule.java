package io.github.potterplus.magicscan.rule.spell;

import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.magic.MagicSpell;
import io.github.potterplus.magicscan.rule.SpellRule;
import io.github.potterplus.magicscan.scan.Violation;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A simple rule which verifies that spell keys follow conventions.
 */
public class SpellKeyRule extends SpellRule {

    public SpellKeyRule(MagicScanController controller) {
        super(controller);
    }

    @Override
    public String getKey() {
        return "key";
    }

    private boolean containsCapitalLetter(String str) {
        for (char c : str.toCharArray()) {
            if (Character.isAlphabetic(c) && Character.toUpperCase(c) == c) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Collection<Violation> validate(MagicSpell spell) {
        ConfigurationSection options = getConfigurationSection();
        boolean enforceNoUnderscores = options == null || options.getBoolean("enforce_no_underscores", true);
        boolean enforceNoCaps = options == null || options.getBoolean("enforce_no_caps", true);
        boolean enforceNoNumbers = options == null || options.getBoolean("enforce_no_numbers", true);
        String key = spell.getKey();

        List<Violation> violations = new ArrayList<>();

        if (enforceNoUnderscores && key.contains("_")) {
            violations.add(
                    Violation.builder(this)
                            .message("key_underscore")
                            .build()
            );
        }

        if (enforceNoCaps && containsCapitalLetter(key)) {
            violations.add(
                    Violation.builder(this)
                            .message("key_uppercase")
                            .build()
            );
        }

        if (enforceNoNumbers && !key.contains("|") && key.matches(".*\\d.*")) {
            violations.add(
                    Violation.builder(this)
                            .message("key_numbers")
                            .build()
            );
        }

        return violations;
    }
}
