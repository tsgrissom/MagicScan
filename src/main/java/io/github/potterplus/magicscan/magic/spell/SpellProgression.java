package io.github.potterplus.magicscan.magic.spell;

import io.github.potterplus.api.string.StringUtilities;
import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.magic.MagicSpell;
import io.github.potterplus.magicscan.misc.Describable;
import io.github.potterplus.magicscan.misc.Utilities;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Represents a spell's progression.
 */
public class SpellProgression implements Describable {

    @Getter @NonNull
    private final MagicSpell originSpell;

    @Getter @NonNull
    private final List<MagicSpell> progression;

    public SpellProgression(MagicScanController controller, MagicSpell originSpell) {
        this.originSpell = originSpell;
        this.progression = new ArrayList<>();

        Optional<MagicSpell> opt = controller.resolveSpell(getOriginSpell().getBaseKey());

        if (opt.isPresent()) {
            MagicSpell original = opt.get();

            getProgression().add(original);

            for (int i = 2; i <= original.getMaxLevel(); i++) {
                controller.resolveSpell(original.getBaseKey() + "|" + i).ifPresent(getProgression()::add);
            }
        } else {
            throw new IllegalArgumentException(String.format("Failed to resolve original spell from base key of spell '%s'", getOriginSpell().getKey()));
        }
    }

    public boolean exists() {
        return getProgression().size() > 1;
    }

    public boolean contains(MagicSpell spell) {
        return getProgression().contains(spell);
    }

    public int getMaxLevel() {
        return getOriginSpell().getMaxLevel();
    }

    public MagicSpell getMaxLevelSpell() {
        return getProgression().get(getProgression().size());
    }

    public String getPathString() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < getProgression().size(); i++) {
            builder.append(getProgression().get(i).getKey());

            if (i < getProgression().size()) {
                builder.append(" -> ");
            }
        }

        String str = builder.toString();

        str = str.substring(0, str.length() - 4);

        return str;
    }

    @Override
    public List<String> describe(CommandSender sender) {
        List<String> list = new ArrayList<>();

        Collections.sort(progression);

        list.add("&eProgression: &r" + this.getPathString());

        list.addAll(getProgression().get(0).describe(sender));

        for (int i = 1; i < getProgression().size(); i++) {
            List<String> sublist = getProgression().get(i).describe(sender);

            // TODO Actually fill out removeEntries?

            sublist = Utilities.removeEntries(sublist, "name", "description", "category");

            list.addAll(sublist);
        }

        return StringUtilities.color(list);
    }

    @Override
    public ItemStack describeAsItem(CommandSender sender) {
        throw new UnsupportedOperationException("Don't describe SpellProgressions as items!");
    }
}
