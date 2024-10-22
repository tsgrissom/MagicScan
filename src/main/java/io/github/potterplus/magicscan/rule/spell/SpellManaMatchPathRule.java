package io.github.potterplus.magicscan.rule.spell;

import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.magic.MagicPath;
import io.github.potterplus.magicscan.magic.MagicSpell;
import io.github.potterplus.magicscan.rule.SpellRule;
import io.github.potterplus.magicscan.scan.Violation;
import io.github.potterplus.magicscan.scan.Violations;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collection;
import java.util.Optional;

/**
 * A rule which verifies that the spell's mana cost is achievable for the path the spell is found on.
 */
public class SpellManaMatchPathRule extends SpellRule {

    public SpellManaMatchPathRule(MagicScanController controller) {
        super(controller);
    }

    @Override
    public String getKey() {
        return "mana_match_path";
    }

    @Override
    public Collection<Violation> validate(MagicSpell spell) {
        Optional<MagicPath> opt = spell.getContainingPath();

        if (opt.isPresent()) {
            FileConfiguration pathsFile = getController().getPathsDefaults().getFileConfiguration();
            MagicPath path = opt.get();
            ConfigurationSection section = pathsFile.getConfigurationSection(path.getKey());

            if (section == null) {
                return Violations.none();
            }

            Optional<Integer> optMana = spell.getManaCost();

            if (optMana.isPresent()) {
                if (optMana.get() >= section.getInt("max_mana")) {
                    return Violations.forRule(this);
                }
            }
        }

        return Violations.none();
    }
}
