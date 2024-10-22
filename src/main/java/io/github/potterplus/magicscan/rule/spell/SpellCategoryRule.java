package io.github.potterplus.magicscan.rule.spell;

import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.magic.MagicSpell;
import io.github.potterplus.magicscan.magic.spell.SpellCategory;
import io.github.potterplus.magicscan.rule.SpellRule;
import io.github.potterplus.magicscan.scan.Violation;
import io.github.potterplus.magicscan.scan.Violations;

import java.util.Collection;
import java.util.Optional;

/**
 * A simple rule which verifies that the spell has a valid spell category.
 */
public class SpellCategoryRule extends SpellRule {

    public SpellCategoryRule(MagicScanController controller) {
        super(controller);
    }

    @Override
    public String getKey() {
        return "category";
    }

    @Override
    public Collection<Violation> validate(MagicSpell spell) {
        Optional<SpellCategory> opt = spell.getCategory();

        if (!opt.isPresent()) {
            return Violations.none();
        }

        Collection<com.elmakers.mine.bukkit.api.spell.SpellCategory> validCategories = getController().getMageController().getCategories();

        if (validCategories == null) {
            return Violations.none();
        }

        boolean valid = false;

        for (com.elmakers.mine.bukkit.api.spell.SpellCategory category : validCategories) {
            if (category.getKey().equals(opt.get().getKey())) {
                valid = true;
            }
        }

        if (valid) {
            return Violations.none();
        } else {
            return Violations.forRule(this);
        }
    }
}
