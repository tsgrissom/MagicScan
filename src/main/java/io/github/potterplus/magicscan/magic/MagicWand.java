package io.github.potterplus.magicscan.magic;

import com.elmakers.mine.bukkit.api.wand.Wand;
import com.elmakers.mine.bukkit.api.wand.WandTemplate;
import io.github.potterplus.api.string.StringUtilities;
import io.github.potterplus.magicscan.MagicScanController;
import lombok.NonNull;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a wand template.
 */
public class MagicWand extends MagicType<WandTemplate> implements Comparable<MagicWand> {

    public MagicWand(@NonNull MagicScanController controller, @NonNull String key) {
        super(controller, key);

        WandTemplate wand = controller.getMageController().getWandTemplate(key);

        if (wand == null) {
            throw new NullPointerException(String.format("Could not find wand template '%s' to create snapshot", key));
        }

        this.setTemplate(wand);
    }

    public ConfigurationSection getSection() {
        return getWandsDefaults().getConfigurationSection(getKey());
    }

    public boolean isHidden() {
        return getSection().getBoolean("hidden", false);
    }

    public Optional<MagicWand> getParent() {
        return getController().resolveWand(getSection().getString("inherit"));
    }

    @Override
    public List<String> describe(CommandSender sender) {
        List<String> list = new ArrayList<>();

        // TODO Describe more

        list.add("&8- &7key&8: &e" + getKey());

        getParent().ifPresent(wand -> list.add("  &7parent&8: &e" + wand.getKey()));

        return StringUtilities.color(list);
    }

    @Override
    public ItemStack describeAsItem(CommandSender sender) {
        Wand wand = getController().getMagicAPI().createWand(this.getKey());

        if (wand == null) return null;

        ItemStack item = wand.getItem();

        if (item == null) return null;

        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setLore(this.describe(sender));
            item.setItemMeta(meta);
        }

        return item;
    }

    @Override
    public int compareTo(MagicWand that) {
        return this.getKey().compareTo(that.getKey());
    }
}
