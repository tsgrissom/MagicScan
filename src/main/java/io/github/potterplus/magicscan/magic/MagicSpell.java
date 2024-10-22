package io.github.potterplus.magicscan.magic;

import com.elmakers.mine.bukkit.api.block.MaterialAndData;
import com.elmakers.mine.bukkit.api.item.Cost;
import com.elmakers.mine.bukkit.api.magic.MageController;
import com.elmakers.mine.bukkit.api.spell.CastingCost;
import com.elmakers.mine.bukkit.api.spell.SpellKey;
import com.elmakers.mine.bukkit.api.spell.SpellTemplate;
import com.google.common.base.Splitter;
import io.github.potterplus.api.misc.PluginLogger;
import io.github.potterplus.api.string.StringUtilities;
import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.magic.spell.SpellAction;
import io.github.potterplus.magicscan.magic.spell.SpellCategory;
import io.github.potterplus.magicscan.magic.spell.SpellProgression;
import io.github.potterplus.magicscan.magic.spell.SpellUpgradeDescription;
import io.github.potterplus.magicscan.misc.Utilities;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Represents a spell template.
 */
public class MagicSpell extends MagicType<SpellTemplate> implements Comparable<MagicSpell> {

    public static class Comparator implements java.util.Comparator<MagicSpell> {

        @Override
        public int compare(MagicSpell o1, MagicSpell o2) {
            return o1.compareTo(o2);
        }
    }

    public MagicSpell(MagicScanController controller, String key) {
        super(controller, key);

        SpellTemplate spell = controller.getMagicAPI().getSpellTemplate(key);

        if (spell == null) {
            throw new NullPointerException(String.format("Could not find spell '%s' to create snapshot", key));
        }

        this.setTemplate(spell);
    }

    public MagicSpell(MagicScanController controller, SpellKey spellKey) {
        this(controller, spellKey.getKey());
    }

    public MagicSpell(MagicScanController controller, SpellTemplate template) {
        this(controller, template.getKey());
    }

    public ConfigurationSection getSpellSection() {
        return getSpellsDefaults().getConfigurationSection(getKey());
    }

    public ConfigurationSection getParametersSection() {
        return getSpellSection().getConfigurationSection("parameters");
    }

    public ConfigurationSection getMessagesSection() {
        return getMessagesDefaults().getConfigurationSection("spells." + getKey());
    }

    public boolean isHidden() {
        return getTemplate().isHidden();
    }

    public String getAlphanumericKey() {
        return getKey().replace("|", "");
    }

    public String getBaseKey() {
        if (!isLeveledVariant()) return getKey();
        else return Splitter.on("|").split(getKey()).iterator().next();
    }

    public boolean isLeveledVariant() {
        return getKey().contains("|");
    }

    public boolean isVariantOf(MagicSpell that) {
        return new SpellProgression(getController(), this).contains(that);
    }

    public int getCurrentLevel() {
        if (!isLeveledVariant()) return 1;

        return Integer.parseInt(getKey().split(Pattern.quote("|"))[1]);
    }

    public int getMaxLevel() {
        int i = 2;

        while (getController().resolveSpell(getBaseKey() + "|" + i).isPresent()) {
            i++;
        }

        return i - 1;
    }

    public Optional<MagicSpell> getOriginalLevel() {
        return getController().resolveSpell(getBaseKey());
    }

    public Optional<MagicSpell> getPreviousLevel() {
        if (!isLeveledVariant()) return Optional.empty();

        String baseKey = getBaseKey();

        if (getCurrentLevel() == 2) {
            return getController().resolveSpell(getBaseKey());
        }

        int level = getCurrentLevel() - 1;

        return getController().resolveSpell(baseKey + "|" + level);
    }

    public Optional<MagicSpell> getNextLevel() {
        if (!isLeveledVariant()) {
            return getController().resolveSpell(getBaseKey() + "|2");
        } else {
            int level = getCurrentLevel() + 1;

            return getController().resolveSpell(getBaseKey() + "|" + level);
        }
    }

    public Optional<String> getName() {
        String name = getTemplate().getName();

        if (name == null || name.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(name);
        }
    }

    public String getNameString() {
        return getName().isPresent() ? "&e" + getName().get() : "&cUNSPECIFIED";
    }

    public Optional<String> getDescription() {
        String desc = getTemplate().getDescription();

        if (desc == null || desc.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(desc);
        }
    }

    public String getDescriptionString() {
        return getDescription().isPresent() ? "&e" + getDescription().get() : "&cUNSPECIFIED";
    }

    public Optional<MagicSpell> getParent() {
        MageController controller = getController().getMageController();
        String inherit = getSpellSection().getString("inherit");
        SpellTemplate template = controller.getSpellTemplate(inherit);

        if (inherit != null && template != null) return Optional.of(new MagicSpell(getController(), template));
        else return Optional.empty();
    }

    public Optional<SpellCategory> getCategory() {
        com.elmakers.mine.bukkit.api.spell.SpellCategory category = getTemplate().getCategory();

        if (category != null) return Optional.of(new SpellCategory(getController(), category));
        else return Optional.empty();
    }

    private CastingCost getCastingCost() {
        Collection<CastingCost> castingCosts = getTemplate().getCosts();

        if (castingCosts == null || castingCosts.isEmpty()) {
            return null;
        }

        return castingCosts.stream().findFirst().orElse(null);
    }

    public Optional<Integer> getManaCost() {
        ConfigurationSection spellSection = getSpellSection();

        if (spellSection != null && spellSection.isSet("costs")) {
            ConfigurationSection costsSection = spellSection.getConfigurationSection("costs");

            if (costsSection != null) {
                if (costsSection.isSet("mana")) {
                    int i = costsSection.getInt("mana", 0);

                    if (i > 0) {
                        return Optional.of(i);
                    }
                }
            }
        }

        CastingCost cost = getCastingCost();

        if (cost != null) {
            if (cost.getMana() > 0) {
                return Optional.of(cost.getMana());
            }
        }

        return Optional.empty();
    }

    public Optional<Long> getCooldown() {
        ConfigurationSection parameters = getParametersSection();

        if (parameters != null) {
            if (parameters.isSet("cooldown")) {
                long l = parameters.getLong("cooldown", 0);

                if (l > 0) {
                    return Optional.of(l);
                }
            }
        }

        SpellTemplate spell = getTemplate();

        if (spell != null) {
            long l = spell.getCooldown();

            if (l > 0) {
                return Optional.of(l);
            }
        }

        return Optional.empty();
    }

    public Optional<MagicPath> getUpgradeRequiredPath() {
        String key = getSpellSection().getString("upgrade_required_path");

        return getController().resolvePath(key);
    }

    public int getUpgradeRequiredCasts() {
        return getSpellSection().getInt("upgrade_required_casts");
    }

    public Optional<MagicPath> getContainingPath() {
        for (MagicPath path : getController().getPaths()) {
            if (path.containsSpell(getTemplate())) {
                return Optional.of(path);
            }
        }

        return Optional.empty();
    }

    public boolean hasTag(String tag) {
        return getTemplate().hasTag(tag);
    }

    public boolean hasAnyTag(Collection<String> tags) {
        return getTemplate().hasAnyTag(tags);
    }

    public Collection<CastingCost> getCastingCosts() {
        return getTemplate().getCosts();
    }

    public Collection<CastingCost> getActiveCosts() {
        return getTemplate().getActiveCosts();
    }

    public Cost getCost() {
        return getTemplate().getCost();
    }

    public List<MagicSpell> getChildren() {
        List<MagicSpell> list = new ArrayList<>();

        for (MagicSpell spell : getController().getSpells()) {
            String currentKey = spell.getKey();
            ConfigurationSection cs = spell.getSpellSection();

            if (currentKey.equals(getKey()) || cs == null || cs.get("inherit") == null) {
                continue;
            }

            String str = cs.getString("inherit");

            assert str != null;

            if (Objects.equals(cs.getString("inherit"), getKey())) {
                list.add(spell);
            }
        }

        return list;
    }

    public boolean hasProgression() {
        return getProgression().isPresent();
    }

    public Optional<SpellProgression> getProgression() {
        SpellProgression progression = new SpellProgression(getController(), this);

        if (progression.exists()) return Optional.of(progression);
        else return Optional.empty();
    }

    public Map<String, Object> getParameters() {
        ConfigurationSection section = getParametersSection();

        if (section == null) return null;

        return getParametersSection().getValues(true);
    }

    public List<String> getParametersList() {
        return new ArrayList<>(getParameters().keySet());
    }

    @SuppressWarnings("unchecked")
    public List<SpellAction> addActions(List<Map<?, ?>> list) {
        List<SpellAction> list1 = new ArrayList<>();

        for (Map<?, ?> map : list) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
               if (entry.getValue() instanceof MemorySection) continue;

                if (entry.getKey() instanceof String) {
                    String key = (String) entry.getKey();

                    if (key.equals("class") && entry.getValue() instanceof String) {
                        String value = (String) entry.getValue();

                        getController().resolveAction(value).ifPresent(list1::add);
                    }

                    if (key.equals("actions") && entry.getValue() instanceof List) {
                        list1.addAll(addActions((List<Map<?, ?>>) entry.getValue()));
                    }
                }
            }
        }

        return list1;
    }

    public List<SpellAction> getActions() {
        List<SpellAction> list = new ArrayList<>();
        ConfigurationSection section = getSpellSection().getConfigurationSection("actions");

        if (section == null) return null;

        for (String actionKey : section.getKeys(true)) {
            list.addAll(addActions(section.getMapList(actionKey)));
        }

        return list;
    }

    public Map<SpellAction, List<String>> getActionsToParameters() {
        Map<SpellAction, List<String>> map = new HashMap<>();
        List<SpellAction> actions = getActions();

        if (actions == null) return null;

        for (SpellAction action : actions) {
            List<String> list = new ArrayList<>();
            List<String> actionParameters = action.getParametersList();

            for (String param : actionParameters) {
                if (this.getParameters().containsKey(param)) {
                    list.add(param);
                }
            }

            if (!list.isEmpty()) {
                map.put(action, list);
            }
        }

        return map;
    }

    public List<String> getMatchedParameters() {
        List<String> list = new ArrayList<>();
        Map<SpellAction, List<String>> actionsToParams = getActionsToParameters();

        if (actionsToParams == null) return null;

        for (Map.Entry<SpellAction, List<String>> entry : actionsToParams.entrySet()) {
            list.addAll(entry.getValue());
        }

        return list;
    }

    public List<String> getUnmatchedParameters() {
        List<SpellAction> actions = getActions();
        Map<String, Object> map = getParameters();
        List<String> matched = getMatchedParameters();
        List<String> exclude = getController().getDefaultSpellParameters();
        List<String> toRemove = new ArrayList<>();

        {
            // Some special cases

            Optional<SpellAction> opt = getController().resolveAction("PotionEffectAction");

            if (!opt.isPresent()) {
                PluginLogger.atWarn()
                        .with("Why isn't PotionEffectAction parsing?")
                        .print();
                // TODO Remove if working
            } else {
                if (actions != null && map != null && !map.isEmpty() && actions.contains(opt.get())) {
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        String str = entry.getKey();

                        if (str.startsWith("effect_") || str.startsWith("add_effects")) {
                            toRemove.add(str);
                        }
                    }
                }
            }
        }

        if (map == null || matched == null) return null;

        toRemove.addAll(matched);
        toRemove.addAll(exclude);

        toRemove.forEach(map::remove);

        return new ArrayList<>(map.keySet());
    }

    public Optional<ItemStack> getIcon() {
        MaterialAndData mat = getTemplate().getIcon();

        if (mat == null || (mat.getMaterial() != null && !mat.getMaterial().isItem())) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(getController().getMageController().createSpellItem(this.getKey()));
        }
    }

    public Optional<ItemStack> getDisabledIcon() {
        MaterialAndData mat = getTemplate().getDisabledIcon();

        if (mat == null || (mat.getMaterial() != null && !mat.getMaterial().isItem())) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(mat.getItemStack(1));
        }
    }

    public Optional<SpellUpgradeDescription> getUpgradeDescription() {
        if (!this.isLeveledVariant()) {
            return Optional.empty();
        }

        ConfigurationSection cs = getMessagesSection();

        if (cs == null) {
            return Optional.empty();
        }

        if (!cs.isSet("upgrade_description")) {
            return Optional.empty();
        }

        String s = cs.getString("upgrade_description");

        return s == null || s.isEmpty() ? Optional.empty() : Optional.of(new SpellUpgradeDescription(getController(), this));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MagicSpell that = (MagicSpell) o;

        return Objects.equals(getKey(), that.getKey());
    }

    @Override
    public int compareTo(MagicSpell that) {
        if (this.isVariantOf(that)) {
            return Integer.compare(this.getCurrentLevel(), that.getCurrentLevel());
        }

        return this.getAlphanumericKey().compareTo(that.getAlphanumericKey());
    }

    @Override
    public List<String> describe(CommandSender sender) {
        // TODO Describe more

        List<String> list = new ArrayList<>();

        // So this weird bit is just to make sure the lists look right on console/player sides

        if (sender instanceof ConsoleCommandSender) {
            list.add("&8- &7key&8: &e" + this.getKey());
            list.add("  &7name&8: &e" + getNameString());
        } else {
            list.add("&8- &7name&8: &e" + getNameString());
        }

        list.add("  &7description&8:");
        list.add("    " + getDescriptionString());

        String icon;

        if (getIcon().isPresent()) {
            icon = getTemplate().getIcon().getKey();
        } else {
            icon = null;
        }

        list.add("  &7icon&8: " + (icon == null ? "&cUNSPECIFIED" : "&e" + icon));

        String iconDisabled;

        if (getDisabledIcon().isPresent()) {
            iconDisabled = getTemplate().getDisabledIcon().getKey();
        } else {
            iconDisabled = null;
        }

        list.add("  &7disabled icon&8: " + (iconDisabled == null ? "&cUNSPECIFIED" : "&e" + iconDisabled));

        {
            Optional<Integer> opt = getManaCost();
            String s = opt.map(mana -> "&e" + mana).orElse("&cNOT SET");

            list.add("  &7mana cost&8: " + s);
        }

        {
            Optional<Long> opt = getCooldown();
            String s = opt.map(cd -> "&e" + cd).orElse("&cNOT SET");

            list.add("  &7cooldown&8: " + s);
        }

        list.add("  &7max level&8: &e" + getMaxLevel());

        getCategory().ifPresent(category -> list.add("  &7category&8: &e" + category.getKey()));
        getParent().ifPresent(spell -> list.add("  &7parent&8: &e" + spell.getKey()));
        getPreviousLevel().ifPresent(spell -> list.add("  &7previous level&8: &e" + spell.getKey()));
        getNextLevel().ifPresent(spell -> list.add("  &7next level&8: &e" + spell.getKey()));
        getContainingPath().ifPresent(path -> list.add("  &7learnable at path&8: &e" + path.getKey()));
        getUpgradeRequiredPath().ifPresent(path -> list.add("  &7upgradable at path&8: &e" + path.getKey()));

        if (getUpgradeRequiredCasts() > 0) {
            list.add("  &7casts to upgrade&8: &e" + getUpgradeRequiredCasts());
        }

        if (this.isLeveledVariant()) {
            Optional<SpellUpgradeDescription> opt = getUpgradeDescription();

            list.add("  &7upgrade description&8:");

            if (opt.isPresent()) {
                list.addAll(opt.get().getFormatted());
            } else {
                list.add("    &cUNSPECIFIED");
            }
        }

        if (getChildren() != null && !getChildren().isEmpty()) {
            list.add("  &7children&8: &e" + Utilities.spellList(getChildren()));
        }

        List<SpellAction> actions = getActions();

        if (actions != null && !actions.isEmpty()) {
            list.add("  &7actions&8:");

            for (SpellAction action : actions) {
                list.add("    &8- &6&o" + action.getClassName());
            }
        }

        ConfigurationSection parameters = getParametersSection();

        if (parameters != null) {
            list.add(StringUtilities.color("  &7parameters&8:"));

            if (getActionsToParameters() != null && !getActionsToParameters().isEmpty()) {
                for (Map.Entry<SpellAction, List<String>> entry : getActionsToParameters().entrySet()) {
                    list.add("   &efrom &6&o" + entry.getKey().getClassName());

                    for (String str : entry.getValue()) {
                        Object obj = parameters.get(str);

                        list.add("    &7" + str + "&8: &e" + (obj instanceof MemorySection ? "null" : obj));
                    }
                }
            }

            List<String> unmatched = getUnmatchedParameters();

            if (unmatched != null && !unmatched.isEmpty()) {
                for (String key : unmatched) {
                    Object value = parameters.get(key);

                    if (value instanceof MemorySection) continue;

                    list.add("    &7" + key + "&8: &e" + parameters.get(key));
                }
            }
        }

        return list;
    }

    @Override
    public ItemStack describeAsItem(CommandSender sender) {
        ItemStack item;

        if (getIcon().isPresent()) {
            item = getIcon().get();
        } else {
            item = new ItemStack(Material.BARRIER);
        }

        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            List<String> lore;

            if (meta.hasLore() && meta.getLore() != null) {
                lore = meta.getLore();
            } else {
                lore = new ArrayList<>();
            }

            if (lore.size() > 0) {
                lore.add(0, " &7&m--------------------");
                lore.add(1, " &8&lFROM ITEM LORE");
                lore.add(2, " &7&m--------------------");
            }

            lore.add(" &7&m--------------------");
            lore.add(" &8&lFROM MAGICSCAN");
            lore.add(" &7&m--------------------");

            lore.addAll(this.describe(sender));

            meta.setLore(StringUtilities.color(lore));

            getName().ifPresent(name -> meta.setDisplayName(StringUtilities.color("&6" + name + " &8(&7key&8: &e" + this.getKey() + "&8)")));

            item.setItemMeta(meta);
        }

        return item;
    }
}
