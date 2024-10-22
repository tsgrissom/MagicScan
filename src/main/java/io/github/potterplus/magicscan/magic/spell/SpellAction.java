package io.github.potterplus.magicscan.magic.spell;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.potterplus.api.item.Icon;
import io.github.potterplus.magicscan.misc.Describable;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Represents an action and its attributes, loaded from Magic's meta.json file.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpellAction implements Describable, Comparable<SpellAction> {

    @Getter
    private String category;

    private String className;

    @JsonProperty("class_name")
    public String getClassName() {
        return this.className;
    }

    @Getter
    private String[] description;

    @Getter
    private String[] examples;

    @Getter
    private int importance;

    @Getter
    private String name;

    private String shortClass;

    @JsonProperty("short_class")
    public String getShortClass() {
        return this.shortClass;
    }

    @Getter
    private Map<String, Object> parameters;

    public List<String> getParametersList() {
        return new ArrayList<>(getParameters().keySet());
    }

    @Override
    public List<String> describe(CommandSender sender) {
        List<String> list = new ArrayList<>();

        list.add("&8- &7name&8: &e" + name);
        list.add("  &7class&8: &e" + getClassName());
        list.add("  &7short_class&8: &e" + getShortClass());
        list.add("  &7category&8: &e" + category);
        list.add("  &7description&8: &e" + Arrays.toString(description));
        list.add("  &7examples&8: &e" + Arrays.toString(examples));
        list.add("  &7importance&8: &e" + importance);

        if (parameters != null && !parameters.isEmpty()) {
            list.add("  &7parameters&r:");

            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                list.add("    &e" + entry.getKey() + "&8: &7" + entry.getValue());
            }
        }

        return list;
    }

    @Override
    public ItemStack describeAsItem(CommandSender sender) {
        return Icon
                .start(Material.PAPER)
                .name(name)
                .lore(this.describe(sender))
                .get();
    }

    @Override
    public int compareTo(SpellAction that) {
        return this.name.compareTo(that.name);
    }
}
