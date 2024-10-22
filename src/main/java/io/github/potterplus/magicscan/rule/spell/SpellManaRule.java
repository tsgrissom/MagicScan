package io.github.potterplus.magicscan.rule.spell;

import com.elmakers.mine.bukkit.api.spell.CastingCost;
import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.magic.MagicSpell;
import io.github.potterplus.magicscan.rule.SpellRule;
import io.github.potterplus.magicscan.scan.Violation;
import io.github.potterplus.magicscan.scan.Violations;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;

/**
 * A simple rule which verifies that the spell has a mana cost. If no casting mana cost is found, an active mana cost is looked for.
 */
public class SpellManaRule extends SpellRule {

    public SpellManaRule(MagicScanController controller) {
        super(controller);
    }

    @Override
    public String getKey() {
        return "mana";
    }

    @Override
    public Collection<Violation> validate(MagicSpell spell) {
        boolean valid = false;
        ConfigurationSection options = getConfigurationSection();
        boolean fallbackActiveCosts = options == null || options.getBoolean("fallback_active_costs", true);
        Collection<CastingCost> castingCosts = spell.getCastingCosts();
        Collection<CastingCost> activeCosts = spell.getActiveCosts();

        if (castingCosts == null && activeCosts == null) {
            return Violations.none();
        }

        if (castingCosts != null) {
            for (CastingCost cost : castingCosts) {
                if (cost.getMana() > 0) {
                    valid = true;
                }
            }
        }

        // Look for an active cost if no casting cost is found
        if (!valid && fallbackActiveCosts) {
            if (activeCosts != null) {
                for (CastingCost cost : activeCosts) {
                    if (cost.getMana() > 0) valid = true;
                }
            }
        }

        if (valid) {
            return Violations.none();
        } else {
            return Violations.forRule(this);
        }
    }
}
