package io.github.potterplus.magicscan.magic.spell;

import com.elmakers.mine.bukkit.api.spell.SpellTemplate;
import io.github.potterplus.api.item.Icon;
import io.github.potterplus.api.string.StringUtilities;
import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.magic.MagicSpell;
import io.github.potterplus.magicscan.magic.MagicType;
import io.github.potterplus.magicscan.misc.Describable;
import io.github.potterplus.magicscan.misc.Utilities;
import lombok.NonNull;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents a spell category as defined in Magic's messages.
 */
public class SpellCategory extends MagicType<com.elmakers.mine.bukkit.api.spell.SpellCategory> implements Describable, Comparable<SpellCategory> {

    public SpellCategory(@NonNull MagicScanController controller, @NonNull String key) {
        super(controller, key);

        com.elmakers.mine.bukkit.api.spell.SpellCategory cat = controller.getMageController().getCategory(key);

        if (cat == null) {
            throw new NullPointerException(String.format("Could not find spell category '%s' to create snapshot", key));
        }

        this.setTemplate(cat);
    }

    public SpellCategory(@NonNull MagicScanController controller, @NonNull com.elmakers.mine.bukkit.api.spell.SpellCategory category) {
        this(controller, category.getKey());
    }

    public Optional<String> getName() {
        return Optional.ofNullable(getTemplate().getName());
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(getTemplate().getDescription());
    }

    public Optional<Color> getColor() {
        return Optional.ofNullable(getTemplate().getColor());
    }

    public List<MagicSpell> getSpells() {
        List<MagicSpell> spells = new ArrayList<>();

        for (MagicSpell spell : getController().getSpells()) {
            if (!spell.getCategory().isPresent()) continue;

            SpellCategory cat = spell.getCategory().get();

            if (cat.getKey().equals(this.getKey())) spells.add(spell);
        }

        return spells;
    }

    public List<String> getSpellKeys() {
        return getSpells().stream().map(MagicType::getKey).collect(Collectors.toList());
    }

    public boolean containsSpell(MagicSpell spell) {
        return getSpells().contains(spell);
    }

    public boolean containsSpell(SpellTemplate spell) {
        return getSpells().contains(new MagicSpell(getController(), spell));
    }

    public boolean containsSpell(String spell) {
        return getSpells().contains(new MagicSpell(getController(), spell));
    }

    @Override
    public List<String> describe(CommandSender sender) {
        List<String> list = new ArrayList<>();

        list.add("&8- &7key&8: &e" + this.getKey());

        getName().ifPresent((name) -> list.add("  &7name&8: &e" + name));
        getDescription().ifPresent((description) -> list.add("  &7description&8: &e" + description));

        if (getSpells() != null && !getSpells().isEmpty()) {
            list.add("  &3spells ("  + getSpells().size() + ")&8:");
            list.addAll(Utilities.formatMultiLineStrings(getSpellKeys()));
        }

        return StringUtilities.color(list);
    }

    @Override
    public ItemStack describeAsItem(CommandSender sender) {
        Icon builder = Icon
                .start(Material.PAPER)
                .lore(this.describe(sender));

        getName().ifPresent(builder::name);

        return builder.build();
    }

    @Override
    public int compareTo(SpellCategory that) {
        return this.getKey().compareTo(that.getKey());
    }
}
