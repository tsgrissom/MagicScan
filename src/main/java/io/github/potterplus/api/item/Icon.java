package io.github.potterplus.api.item;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.github.potterplus.api.string.StringUtilities;
import lombok.Getter;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.function.Supplier;

/**
 * A fluent ItemStack builder-style class for creating, containing, reading, and easily manipulating ItemStacks.
 */
public class Icon implements Supplier<ItemStack>, Cloneable {

    /**
     * Creates a new ItemStackBuilder.
     * @return The instance.
     */
    public static Icon builder() {
        return new Icon();
    }

    /**
     * Creates an ItemStackBuilder from the provided item.
     * @param itemStack The item.
     * @return The instance.
     */
    public static Icon of(ItemStack itemStack) {
        return new Icon(itemStack);
    }

    /**
     * Creates an ItemStackBuilder from the provided item.
     * @param item The item.
     * @return The instance.
     */
    public static Icon of(Supplier<ItemStack> item) {
        return Icon.of(item.get());
    }

    /**
     * Shorthand for creating a builder with a specified type.
     * @param material The material.
     * @return The instance.
     */
    public static Icon start(Material material) {
        return Icon.builder().type(material);
    }

    /**
     * Shorthand for creating simple items.
     * @param material The material.
     * @param name The display name.
     * @param lore The lore.
     * @return The instance.
     */
    public static Icon simple(Material material, String name, List<String> lore) {
        if (material == null) {
            return null;
        }

        Icon builder = Icon.builder()
                .name(name);

        if (lore != null) {
            builder.lore(lore);
        }

        return builder;
    }

    /**
     * Shorthand for creating simple items.
     * @param material The material.
     * @param name The display name.
     * @return The instance.
     */
    public static Icon simple(Material material, String name) {
        return Icon.simple(material, name, null);
    }

    /**
     * Shorthand for creating a skull and setting its owning player.
     * @param player The player to use.
     * @return The instance.
     */
    public static Icon skull(OfflinePlayer player) {
        return Icon
                .start(Material.PLAYER_HEAD)
                .owningPlayer(player);
    }

    /**
     * Shorthand for creating a skull and setting its owning player.
     * @param uuid The UUID of the player to use.
     * @return The instance.
     */
    public static Icon skull(UUID uuid) {
        return Icon
                .start(Material.PLAYER_HEAD)
                .owningPlayer(uuid);
    }

    @Getter
    private Material type;

    /**
     * Sets the item's Material.
     * @param material The Material.
     * @return The instance.
     */
    public Icon type(Material material) {
        this.type = material;

        return this;
    }

    /**
     * Alias for ItemStackBuilder#type.
     * @param material The Material.
     * @return The instance.
     */
    public Icon material(Material material) {
        return this.type(material);
    }

    /**
     * Checks the type of the item.
     * @param material The type.
     * @return Whether or not the types match.
     */
    public boolean isType(Material material) {
        return getType().equals(material);
    }

    public int getMaxStackSize() {
        return getType().getMaxStackSize();
    }

    @Getter
    private short durability;

    /**
     * Sets the item's durability.
     * @param durability The durability.
     * @return The instance.
     */
    public Icon durability(short durability) {
        this.durability = durability;

        return this;
    }

    @Getter
    private int amount;

    /**
     * Sets the amount of the item.
     * @param amount The amount.
     * @return The instance.
     */
    public Icon amount(int amount) {
        this.amount = amount;

        return this;
    }

    /**
     * Sets the item amount to 1.
     * @return The instance.
     */
    public Icon single() {
        return this.amount(1);
    }

    /**
     * Sets the item amount to the max stack size for the Material.
     * @return The instance.
     */
    public Icon fullStack() {
        return this.amount(this.getMaxStackSize());
    }

    /**
     * Sets the item amount to half of the max stack size for the Material.
     * @return The instance.
     */
    public Icon halfStack() {
        return this.amount(this.getMaxStackSize() / 2);
    }

    @Getter
    private int customModelData;

    /**
     * Sets the item's custom model data.
     * @param customModelData The custom model data.
     * @return The instance.
     */
    public Icon customModelData(int customModelData) {
        this.customModelData = customModelData;

        return this;
    }

    @Getter
    private boolean unbreakable;

    /**
     * Set the unbreakable status.
     * @param unbreakable Whether or not the item is unbreakable.
     * @return The instance.
     */
    public Icon unbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;

        return this;
    }

    /**
     * Apply the unbreakable status.
     * @return The instance.
     */
    public Icon unbreakable() {
        return this.unbreakable(true);
    }

    /**
     * Remove the unbreakable status.
     * @return The instance.
     */
    public Icon removeUnbreakable() {
        return this.unbreakable(false);
    }

    /**
     * Toggles the unbreakable status.
     * @return The instance.
     */
    public Icon toggleUnbreakable() {
        return this.unbreakable(!unbreakable);
    }

    @Getter
    private Set<ItemFlag> flags;

    /**
     * Sets the item's flags.
     * @param flags The flags.
     * @return The instance.
     */
    public Icon setFlags(Set<ItemFlag> flags) {
        this.flags = flags;

        return this;
    }

    /**
     * Adds item flags.
     * @param flags The flags.
     * @return The instance.
     */
    public Icon flag(ItemFlag... flags) {
        if (flags == null) return this;

        this.flags.addAll(Arrays.asList(flags));

        return this;
    }

    /**
     * Removes item flags.
     * @param flags The flags to remove.
     * @return The instance.
     */
    public Icon removeFlag(ItemFlag... flags) {
        if (flags == null) return this;

        this.flags.removeAll(Arrays.asList(flags));

        return this;
    }

    /**
     * Clears flags from item.
     * @return The instance.
     */
    public Icon clearFlags() {
        this.flags = new HashSet<>();

        return this;
    }

    /**
     * Checks if the item has any flags.
     * @return Whether or not the item has any flags.
     */
    public boolean hasFlags() {
        return this.flags != null && !this.flags.isEmpty();
    }

    /**
     * Checks if the item has all of the supplied flags.
     * @param flags The flags to check for.
     * @return The instance.
     */
    public boolean hasAllFlags(ItemFlag... flags) {
        for (ItemFlag flag : flags) {
            if (!this.flags.contains(flag)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the item has any of the supplied flags.
     * @param flags The flags to check for.
     * @return The instance.
     */
    public boolean hasAnyFlags(ItemFlag... flags) {
        for (ItemFlag flag : this.flags) {
            if (Arrays.asList(flags).contains(flag)) return true;
        }

        return false;
    }

    @Getter
    private String name;

    /**
     * Sets the item's display name.
     * @param name The name.
     * @return The instance.
     */
    public Icon name(String name) {
        this.name = StringUtilities.color(name);

        return this;
    }

    @Getter
    private List<String> lore;

    /**
     * Sets the item's lore.
     * @param lore The lore.
     * @return The instance.
     */
    public Icon lore(String... lore) {
        this.lore = StringUtilities.color(lore);

        return this;
    }

    /**
     * Sets the item's lore.
     * @param lore The lore.
     * @return The instance.
     */
    public Icon lore(List<String> lore) {
        this.lore = StringUtilities.color(lore);

        return this;
    }

    /**
     * Prepends entries to the lore list.
     * @param lore The lore to prepend.
     * @return The instance.
     */
    public Icon prependLore(List<String> lore) {
        List<String> list = new ArrayList<>(lore);

        list.addAll(this.lore);

        this.lore = StringUtilities.color(list);

        return this;
    }

    /**
     * Prepends entries to the lore list.
     * @param lore The lore to prepend.
     * @return The instance.
     */
    public Icon prependLore(String... lore) {
        return this.prependLore(Arrays.asList(lore));
    }

    /**
     * Adds lines of lore.
     * @param lore The lore to add.
     * @return The instance.
     */
    public Icon addLore(String... lore) {
        if (this.lore == null) {
            this.lore = new ArrayList<>();
        }

        List<String> add = StringUtilities.color(lore);

        this.lore.addAll(add);

        return this;
    }

    /**
     * Removes a line of lore.
     * @param index The index of the line to remove.
     * @return The instance.
     */
    public Icon removeLore(int index) {
        this.lore.remove(index);

        return this;
    }

    // TODO Remove first line, last line, range of lines, etc. Insert lore at index

    /**
     * Removes a line of lore.
     * @param line The line to remove.
     * @param strip Whether or not to strip colors when comparing.
     * @return The instance.
     */
    public Icon removeLore(String line, boolean strip) {
        line = strip ? ChatColor.stripColor(line) : line;

        for (int i = 0; i < lore.size(); i++) {
            String entry = lore.get(i);

            entry = strip ? ChatColor.stripColor(entry) : entry;

            if (line.equals(entry)) {
                this.lore.remove(lore.get(i));
            }
        }

        return this;
    }

    /**
     * Removes a line of lore.
     * @param line The line to remove.
     * @return The instance.
     */
    public Icon removeLore(String line) {
        return removeLore(line, false);
    }

    /**
     * Clears the item's lore.
     * @return The instance.
     */
    public Icon clearLore() {
        this.lore = new ArrayList<>();

        return this;
    }

    @Getter
    private Multimap<Attribute, AttributeModifier> attributeModifiers;

    /**
     * Sets the attribute modifiers for the item.
     * @param attributeModifiers The attribute modifiers.
     * @return The instance.
     */
    public Icon attributes(Multimap<Attribute, AttributeModifier> attributeModifiers) {
        if (attributeModifiers == null) return this;

        this.attributeModifiers = attributeModifiers;

        return this;
    }

    /**
     * Adds attribute and modifier to the item.
     * @param attribute The attribute.
     * @param modifier The modifier.
     * @return The instance.
     */
    public Icon attribute(Attribute attribute, AttributeModifier modifier) {
        this.attributeModifiers.put(attribute, modifier);

        return this;
    }

    /**
     * Removes all instances of the attribute.
     * @param attribute The attribute to remove.
     * @return The instance.
     */
    public Icon removeAttribute(Attribute attribute) {
        this.attributeModifiers.removeAll(attribute);

        return this;
    }

    /**
     * Removes the attribute and its attribute modifier.
     * @param attribute The attribute to remove.
     * @param attributeModifier The attribute modifier to remove.
     * @return The instance.
     */
    public Icon removeAttribute(Attribute attribute, AttributeModifier attributeModifier) {
        this.attributeModifiers.remove(attribute, attributeModifier);

        return this;
    }

    /**
     * Clears the items attribute modifiers.
     * @return The instance.
     */
    public Icon clearAttributes() {
        this.attributeModifiers = ArrayListMultimap.create();

        return this;
    }

    /**
     * Checks if the item has an attribute.
     * @param attribute The attribute.
     * @return Whether or not the item has the supplied attribute.
     */
    public boolean hasAttribute(Attribute attribute) {
        return this.attributeModifiers.containsKey(attribute);
    }

    /**
     * Checks if the item has an attribute modifier.
     * @param attributeModifier The attribute modifier.
     * @return Whether or not the item has the supplied modifier.
     */
    public boolean hasAttributeModifier(AttributeModifier attributeModifier) {
        return this.attributeModifiers.containsValue(attributeModifier);
    }

    /**
     * Checks if the item has an attribute and modifier.
     * @param attribute The attribute.
     * @param attributeModifier The attribute modifier.
     * @return Whether or not the item has the supplied attribute and modifier.
     */
    public boolean hasAttribute(Attribute attribute, AttributeModifier attributeModifier) {
        return this.attributeModifiers.containsEntry(attribute, attributeModifier);
    }

    @Getter
    private List<Enchant> enchants;

    /**
     * Sets the enchantments.
     * @param enchants The enchantments.
     * @return The instance.
     */
    public Icon enchants(List<Enchant> enchants) {
        this.enchants = enchants;

        return this;
    }

    /**
     * Sets the enchantments.
     * @param enchants The enchantments.
     * @return The instance.
     */
    public Icon enchants(Map<Enchantment, Integer> enchants) {
        List<Enchant> list = new ArrayList<>();

        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            list.add(
                    Enchant.builder()
                    .type(entry.getKey())
                    .level(entry.getValue())
                    .build()
            );
        }

        this.enchants = list;

        return this;
    }

    /**
     * Applies the supplied Enchants to the item.
     * @param enchants The Enchants to apply.
     * @return The instance.
     */
    public Icon enchant(Enchant... enchants) {
        this.enchants.addAll(Arrays.asList(enchants));

        return this;
    }

    /**
     * Applies the specified enchantment at the specified level.
     * @param enchantment The enchantment.
     * @param level The level.
     * @param ignoreLevel Whether or not to ignore level restrictions.
     * @return The instance.
     */
    public Icon enchant(Enchantment enchantment, int level, boolean ignoreLevel) {
        this.enchants.add(
                Enchant.builder()
                        .type(enchantment)
                        .level(level)
                        .ignoreLevel(ignoreLevel)
                        .build()
        );

        return this;
    }

    /**
     * Applies the specified enchantments at their starting levels.
     * @param enchantments The enchantments.
     * @return The instance.
     */
    public Icon enchant(Enchantment... enchantments) {
        for (Enchantment e : enchantments) {
            this.enchant(
                    Enchant.builder()
                    .type(e)
                    .level(e.getStartLevel())
                    .build()
            );
        }

        return this;
    }

    /**
     * Applies the specified enchantment at the specified level.
     * @param enchantment The enchantment.
     * @param level The level.
     * @return The instance.
     */
    public Icon enchant(Enchantment enchantment, int level) {
        return this.enchant(enchantment, level, false);
    }

    /**
     * Removes the specified enchantments from the item.
     * @param enchantments The enchantments to remove.
     * @return The instance.
     */
    public Icon removeEnchant(Enchantment... enchantments) {
        List<Enchant> list = new ArrayList<>(this.enchants);

        for (Enchant e : this.enchants) {
            if (Arrays.asList(enchantments).contains(e.getType())) {
                list.remove(e);
            }
        }

        this.enchants = list;

        return this;
    }

    /**
     * Clears all enchantments from the item.
     * @return The instance.
     */
    public Icon clearEnchants() {
        this.enchants = new ArrayList<>();

        return this;
    }

    /**
     * Checks if the item has any enchantments.
     * @return Whether or not the item has any enchantments.
     */
    public boolean isEnchanted() {
        return !this.enchants.isEmpty();
    }

    /**
     * Checks if the item has the specified enchantment.
     * @param enchantment The enchantment to check for.
     * @return The instance.
     */
    public boolean hasEnchant(Enchantment enchantment) {
        for (Enchant enchant : this.enchants) {
            if (enchant.getType().equals(enchantment)) return true;
        }

        return false;
    }

    /**
     * Checks if the item has the specified Enchant. Maybe use hasEnchant(Enchantment)?
     * @param enchant The Enchant to check for.
     * @return The instance.
     */
    public boolean hasEnchant(Enchant enchant) {
        return this.enchants.contains(enchant);
    }

    @Getter
    private UUID owningPlayer;

    /**
     * Sets the owning player for skulls.
     * @param uuid The UUID of the player to use.
     * @return The instance.
     */
    public Icon owningPlayer(UUID uuid) {
        this.owningPlayer = uuid;

        return this;
    }

    /**
     * Sets the owning player for skulls.
     * @param player The player to use.
     * @return The instance.
     */
    public Icon owningPlayer(OfflinePlayer player) {
        this.owningPlayer = player.getUniqueId();

        return this;
    }

    @Getter
    private Color color;

    /**
     * Applies the supplied color to leather armor pieces.
     * @param color The color.
     * @return The instance.
     */
    public Icon color(Color color) {
        this.color = color;

        return this;
    }

    public Icon() {
        this.type = Material.DIRT;
        this.amount = 1;
        this.flags = new HashSet<>();
        this.lore = new ArrayList<>();
        this.attributeModifiers = ArrayListMultimap.create();
        this.enchants = new ArrayList<>();
    }

    public Icon(ItemStack itemStack) {
        this.type = itemStack.getType();
        this.amount = itemStack.getAmount();

        if (itemStack.hasItemMeta() && itemStack.getItemMeta() != null) {
            ItemMeta meta = itemStack.getItemMeta();
            Damageable damageable = (Damageable) meta;

            this.durability = (short) damageable.getDamage();

            if (meta.hasCustomModelData()) {
                this.customModelData = meta.getCustomModelData();
            }

            this.name = meta.getDisplayName();
            this.lore = meta.getLore();
            this.flags = meta.getItemFlags();
            this.unbreakable = itemStack.getItemMeta().isUnbreakable();
            this.attributeModifiers = meta.getAttributeModifiers();
            this.enchants = new ArrayList<>();

            if (meta.hasEnchants()) {
                for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                    this.enchants.add(
                            Enchant.builder()
                            .type(entry.getKey())
                            .level(entry.getValue())
                            .build()
                    );
                }
            }
        }

        if (itemStack.getType().equals(Material.PLAYER_HEAD) && itemStack.getItemMeta() instanceof SkullMeta) {
            SkullMeta sm = (SkullMeta) itemStack.getItemMeta();
            OfflinePlayer owning = sm.getOwningPlayer();

            if (owning == null) {
                return;
            }

            this.owningPlayer = sm.getOwningPlayer().getUniqueId();
        }

        if (itemStack.getItemMeta() instanceof LeatherArmorMeta) {
            this.color = ((LeatherArmorMeta) itemStack.getItemMeta()).getColor();
        }
    }

    public Icon(Supplier<ItemStack> item) {
        this(item.get());
    }

    public ItemStack build() {
        ItemStack itemStack = new ItemStack(type, amount);
        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) {
            return itemStack;
        }

        if (durability >= NumberUtils.SHORT_ZERO) {
            if (meta instanceof Damageable) {
                Damageable damageable = (Damageable) meta;

                damageable.setDamage(durability);
            }
        }

        if (customModelData >= NumberUtils.INTEGER_ZERO) {
            meta.setCustomModelData(customModelData);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }

        meta.setUnbreakable(unbreakable);

        if (flags != null && !flags.isEmpty()) {
            meta.addItemFlags(flags.toArray(new ItemFlag[0]));
        }

        if (name != null && !name.isEmpty()) {
            meta.setDisplayName(StringUtilities.color(name));
        }

        if (lore != null && !lore.isEmpty()) {
            meta.setLore(StringUtilities.color(lore));
        }

        if (attributeModifiers != null && !attributeModifiers.isEmpty()) {
            meta.setAttributeModifiers(attributeModifiers);
        }

        if (enchants != null && !enchants.isEmpty()) {
            for (Enchant e : this.enchants) {
                meta.addEnchant(e.getType(), e.getLevel(), e.isIgnoreLevel());
            }
        }

        itemStack.setItemMeta(meta);

        Material type = itemStack.getType();

        if (type.equals(Material.PLAYER_HEAD) && itemStack.getItemMeta() instanceof SkullMeta && owningPlayer != null) {
            SkullMeta sm = (SkullMeta) itemStack.getItemMeta();

            sm.setOwningPlayer(Bukkit.getOfflinePlayer(owningPlayer));

            itemStack.setItemMeta(sm);
        }

        if ((type.equals(Material.LEATHER_BOOTS) || type.equals(Material.LEATHER_LEGGINGS) || type.equals(Material.LEATHER_CHESTPLATE) || type.equals(Material.LEATHER_HELMET))
                        && itemStack.getItemMeta() instanceof LeatherArmorMeta
                        && this.color != null) {
            LeatherArmorMeta lm = (LeatherArmorMeta) itemStack.getItemMeta();

            lm.setColor(color);

            itemStack.setItemMeta(lm);
        }

        return itemStack;
    }

    @Override
    public ItemStack get() {
        return this.build();
    }
}
