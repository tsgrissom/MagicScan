package io.github.potterplus.magicscan.magic;

import com.elmakers.mine.bukkit.api.block.MaterialAndData;
import com.elmakers.mine.bukkit.api.spell.SpellTemplate;
import com.elmakers.mine.bukkit.api.wand.WandUpgradePath;
import com.google.common.base.Splitter;
import io.github.potterplus.api.item.Icon;
import io.github.potterplus.api.string.StringUtilities;
import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.misc.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Represents a wand upgrade path from Magic.
 */
public class MagicPath extends MagicType<WandUpgradePath> implements Comparable<MagicPath> {

    public enum SpellGrouping {

        REGULAR,

        REQUIRED,

        EXTRA
    }

    public MagicPath(MagicScanController controller, String key) {
        super(controller, key);

        WandUpgradePath path = getController().getMageController().getPath(key);

        if (path == null) {
            throw new NullPointerException(String.format("Could not find path '%s' to create snapshot", key));
        }

        this.setTemplate(path);
    }

    public MagicPath(MagicScanController controller, WandUpgradePath path) {
        this(controller, path.getKey());
    }

    public ConfigurationSection getSection() {
        return getPathsDefaults().getConfigurationSection(getKey());
    }

    public String getName() {
        return getTemplate().getName();
    }

    public String getDescription() {
        return getTemplate().getDescription();
    }

    public MaterialAndData getIcon() {
        return getTemplate().getIcon();
    }

    public int getMaxMana() {
        return getSection().getInt("max_mana");
    }

    public int getMaxManaRegeneration() {
        return getSection().getInt("max_mana_regeneration");
    }

    public List<String> getUpgradeCommands() {
        return getSection().getStringList("upgrade_commands");
    }

    public Set<String> getTags() {
        return getTemplate().getTags();
    }

    public Collection<String> getSpells() {
        return getTemplate().getSpells();
    }

    public Collection<String> getRequiredSpells() {
        return getTemplate().getRequiredSpells();
    }

    public Collection<String> getExtraSpells() {
        return getTemplate().getExtraSpells();
    }

    public Collection<String> getSpells(SpellGrouping grouping) {
        switch (grouping) {
            case REGULAR:
                return getSpells();
            case REQUIRED:
                return getRequiredSpells();
            case EXTRA:
                return getExtraSpells();
        }

        return getAllSpells();
    }

    public Collection<String> getCurrentSpells(SpellGrouping grouping) {
        switch (grouping) {
            case REGULAR:
                return getCurrentSpells();
            case REQUIRED:
                return getRequiredSpells();
            case EXTRA:
                return getExtraSpells();
        }

        return getAllCurrentSpells();
    }

    public Collection<String> getAllCurrentSpells() {
        Collection<String> spells = getCurrentSpells();

        spells.addAll(getRequiredSpells());
        spells.addAll(getExtraSpells());

        return spells;
    }

    public Collection<String> getCurrentSpells() {
        return getSection().getStringList("spells");
    }

    public Collection<String> getCurrentRequiredSpells() {
        return getSection().getStringList("required_spells");
    }

    public Collection<String> getCurrentExtraSpells() {
        return getSection().getStringList("extra_spells");
    }

    public Collection<String> getAllSpells() {
        Collection<String> spells = getSpells();

        spells.addAll(getRequiredSpells());
        spells.addAll(getExtraSpells());

        return spells;
    }

    public boolean containsSpell(SpellTemplate spell) {
        return getAllSpells().contains(spell.getKey());
    }

    public boolean containsSpell(SpellGrouping grouping, boolean current, SpellTemplate spell) {
        return current ? getCurrentSpells(grouping).contains(spell.getKey()) : getSpells(grouping).contains(spell.getKey());
    }

    public Collection<String> getUpgradableSpells() {
        Collection<MagicSpell> spells = getController().getSpells();
        List<String> list = new ArrayList<>();

        for (MagicSpell spell : spells) {
            if (spell.isLeveledVariant() && spell.getUpgradeRequiredPath().isPresent()) {
                MagicPath path = spell.getUpgradeRequiredPath().get();

                if (path.equals(this)) {
                    list.add(spell.getKey());
                }
            }
        }

        return list;
    }

    public Optional<MagicPath> getParent() {
        String inheritKey = getSection().getString("inherit");
        WandUpgradePath path = getController().getMageController().getPath(inheritKey);

        if (path == null) {
            return Optional.empty();
        } else {
            return Optional.of(new MagicPath(getController(), path));
        }
    }

    public double getAverageSpellWorth() {
        Collection<String> spellKeys = getCurrentSpells();
        List<MagicSpell> spells = new ArrayList<>();

        getCurrentSpells().forEach(s -> getController().resolveSpell(s).ifPresent(spells::add));

        double worth = 0;

        for (MagicSpell spell : spells) {
            worth += spell.getTemplate().getWorth();
        }

        return worth / spells.size();
    }

    public List<String> describe(CommandSender sender) {
        List<String> list = new ArrayList<>();

        list.add("&8- &7key&8: &e" + getTemplate().getKey());
        list.add("  &7name&8: &e" + getTemplate().getName());
        list.add("  &7description&8:");

        if (getDescription() != null && !getDescription().isEmpty()) {
            Iterable<String> description = Splitter.fixedLength(50).split(getDescription());

            for (String s : description) {
                list.add("    " + ChatColor.YELLOW + s);
            }
        }

        list.add("  &7average spell worth&8: " + getAverageSpellWorth());

        list.add("  &7spell count&8: &b(" + getAllCurrentSpells().size() + " to learn) &e" + getAllSpells().size() + " total&8/&c" + getRequiredSpells().size() + " required&8/&d" + getSpells().size() + " regular&8/&a" + getExtraSpells().size() + " extra&8/&6" + getUpgradableSpells().size() + " upgradable");
        list.add("  &3required to learn &c(" + getCurrentRequiredSpells().size() + ")&8:");

        if (getCurrentRequiredSpells() != null && !getCurrentRequiredSpells().isEmpty()) {
            list.addAll(Utilities.formatMultiLineStrings(getCurrentRequiredSpells()));
        }

        list.add("  &3regular spells to learn &d(" + getCurrentSpells().size() + ")&8:");

        if (getCurrentSpells() != null && !getCurrentSpells().isEmpty()) {
            list.addAll(Utilities.formatMultiLineStrings(getCurrentSpells()));
        }

        list.add("  &3extra spells to learn &a(" + getCurrentExtraSpells().size() + ")&8:");

        if (getCurrentExtraSpells() != null && !getCurrentExtraSpells().isEmpty()) {
            list.addAll(Utilities.formatMultiLineStrings(getCurrentExtraSpells()));
        }

        list.add("  &3spells to upgrade &6(" + getUpgradableSpells().size() + ")&8:");

        if (getUpgradableSpells() != null && !getUpgradableSpells().isEmpty()) {
            list.addAll(Utilities.formatMultiLineStrings(getUpgradableSpells()));
        }

        return StringUtilities.color(list);
    }

    @Override
    public ItemStack describeAsItem(CommandSender sender) {
        MaterialAndData mat = getTemplate().getIcon();

        return Icon
                .start(mat == null ? Material.PAPER : mat.getMaterial())
                .name(getTemplate().getName())
                .lore(this.describe(sender))
                .build();
    }

    @Override
    public int compareTo(MagicPath other) {
        int xTotal = this.getAllSpells().size();
        int yTotal = other.getAllSpells().size();

        if (xTotal == 0 && yTotal != 0) return -1;
        if (yTotal == 0 && xTotal != 0) return 1;

        return xTotal - yTotal;
    }
}
