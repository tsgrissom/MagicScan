package io.github.potterplus.magicscan.magic;

import com.elmakers.mine.bukkit.api.block.MaterialAndData;
import com.elmakers.mine.bukkit.api.entity.EntityData;
import io.github.potterplus.api.item.Icon;
import io.github.potterplus.api.string.StringUtilities;
import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.file.MagicDefFile;
import io.github.potterplus.magicscan.misc.Describable;
import io.github.potterplus.magicscan.misc.Utilities;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Art;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

/**
 * Represents a Magic mob.
 */
public class MagicMob extends MagicType<EntityData> implements Comparable<MagicMob> {

    /**
     * Represents a weighted set of spells that a mob can cast, mapped by spell key to probability.
     */
    static class WeightedCastSet {

        @Getter @NonNull
        private Map<String, Integer> probabilities;

        public WeightedCastSet(ConfigurationSection section) {
            this.probabilities = new TreeMap<>();

            for (String key : section.getKeys(false)) {
                this.probabilities.put(key, section.getInt(key));
            }

            this.probabilities = Utilities.sortByValue(probabilities);
        }

        /**
         * Converts the probabilities Map (Strings to their probabilities) into a map of SpellSnapshots to their probabilities.
         * WARNING: Can produce an entry with a null key if a "none" spell key is present.
         * @param controller The controller to resolve SpellSnapshots with.
         * @return The Map of SpellSnapshots to their probabilities.
         */
        public Map<MagicSpell, Integer> getProbabilitiesAsSpells(MagicScanController controller) {
            Map<MagicSpell, Integer> map = new HashMap<>();

            for (Map.Entry<String, Integer> entry : getProbabilities().entrySet()) {
                if (entry.getKey().equals("none")) {
                    map.put(null, entry.getValue());

                    continue;
                }

                controller.resolveSpell(entry.getKey()).ifPresent(spellSnapshot -> map.put(spellSnapshot, entry.getValue()));
            }

            return map;
        }
    }

    enum TriggerType {
        SPAWN,

        INTERVAL,

        DEATH,

        DAMAGE,

        LAUNCH,

        DEAL_DAMAGE,

        KILL
    }

    static class Trigger implements Describable {

        private TriggerType type;
        private WeightedCastSet castSet;
        private int minHealth, maxHealth, minHealthPercent, maxHealthPercent, minDamage, maxDamage;

        public Trigger(TriggerType type, ConfigurationSection section) {
            this.type = type;
        }

        public Trigger(ConfigurationSection section) {
            if (!section.isSet("type")) {
                throw new IllegalArgumentException("Cannot create trigger without type");
            }

            this.type = TriggerType.valueOf(section.getString("type"));

            ConfigurationSection castSection = section.getConfigurationSection("cast");

            if (castSection == null) {
                throw new NullPointerException("Cast section cannot be null");
            }

            this.castSet = new WeightedCastSet(castSection);
        }

        @Override
        public List<String> describe(CommandSender sender) {
            return null;
        }

        @Override
        public ItemStack describeAsItem(CommandSender sender) {
            throw new UnsupportedOperationException("Cannot describe Trigger as item");
        }
    }

    public MagicMob(@NonNull MagicScanController controller, @NonNull String key) {
        super(controller, key);

        EntityData template = controller.getMagicAPI().getController().getMob(key);

        if (template == null) {
            throw new NullPointerException(String.format("Could not find mob '%s' to create snapshot", key));
        }

        this.setTemplate(template);
    }

    public MagicDefFile getMobsDefaults() {
        return getController().getMobsDefaults();
    }

    public ConfigurationSection getSection() {
        return getMobsDefaults().getFileConfiguration().getConfigurationSection(getKey());
    }

    public String getName() {
        return getTemplate().getName();
    }

    public EntityType getType() {
        return getTemplate().getType();
    }

    public double getHealth() {
        return getTemplate().getHealth();
    }

    public ItemStack getItem() {
        return getTemplate().getItem();
    }

    public MaterialAndData getMaterial() {
        return getTemplate().getMaterial();
    }

    public Optional<MagicSpell> getInteractSpell() {
        return getController().resolveSpell(getTemplate().getInteractSpell());
    }

    public Art getArt() {
        return getTemplate().getArt();
    }

    public Optional<MagicMob> getParent() {
        ConfigurationSection cs = getSection();

        if (cs == null) {
            return Optional.empty();
        }

        if (cs.isSet("inherit")) {
            return getController().resolveMob(cs.getString("inherit"));
        } else {
            return Optional.empty();
        }
    }

    public List<String> getDrops() {
        return getSection().getStringList("drops");
    }

    public boolean hasDefaultDrops() {
        ConfigurationSection cs = getSection();

        if (cs == null) {
            return false;
        }

        return cs.getBoolean("default_drops", true);
    }

    public boolean isAggro() {
        ConfigurationSection cs = getSection();

        if (cs == null) {
            return false;
        }

        return cs.getBoolean("aggro", false);
    }

    public boolean isBaby() {
        ConfigurationSection cs = getSection();

        if (cs == null) {
            return false;
        }

        return cs.getBoolean("aggro", false);
    }

    public boolean hasAI() {
        ConfigurationSection cs = getSection();

        if (cs == null) {
            return false;
        }

        return cs.getBoolean("ai", false);
    }

    public int getTrackRadius() {
        ConfigurationSection cs = getSection();

        if (cs == null) {
            return 128;
        }

        return cs.getInt("track_radius", 128);
    }

    public Optional<Set<PotionEffect>> getPotionEffects() {
        ConfigurationSection cs = getSection();

        if (cs == null) {
            return Optional.empty();
        }

        if (!cs.isSet("potion_effects")) {
            return Optional.empty();
        }

        List<Map<?, ?>> mapList = cs.getMapList("potion_effects");
        Set<PotionEffect> set = new HashSet<>();

        for (Map<?, ?> map : mapList) {
            PotionEffectType type = null;
            int amp = 1;

            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() instanceof String) {
                    String key = (String) entry.getKey();

                    if (key.equals("type") && entry.getValue() instanceof String) {
                        String value = (String) entry.getValue();

                        type = PotionEffectType.getByName(value);
                    }

                    if (key.equals("amplifier") && entry.getValue() instanceof Integer) {
                        amp = (Integer) entry.getValue();
                    }
                }
            }

            if (type != null) {
                set.add(new PotionEffect(type, Integer.MAX_VALUE, amp));
            }
        }

        if (set.isEmpty()) set = null;

        return Optional.ofNullable(set);
    }

    public void spawn(Location location) {
        getController().getMageController().spawnMob(this.getKey(), location);
    }

    @Override
    public List<String> describe(CommandSender sender) {
        List<String> list = new ArrayList<>();

        list.add("&8- &7key&8: &e" + getKey());

        getParent().ifPresent((mob) -> list.add("  &7parent&8: &e" + mob.getKey()));
        getPotionEffects().ifPresent((set) -> {
            list.add("  &7potion_effects&8:");

            for (PotionEffect pe : set) {
                list.add("  &8- &7type&8: &e" + pe.getType().getName());
                list.add("    &7amplifier&8: &e" + pe.getAmplifier());
            }
        });

        list.add("");
        list.add("&aLeft-click &7to spawn");
        list.add("&cRight-click &7to clear");

        return StringUtilities.color(list);
    }

    @Override
    public ItemStack describeAsItem(CommandSender sender) {
        return Icon
                .start(Material.getMaterial(getTemplate().getType().toString() + "_SPAWN_EGG"))
                .name(getTemplate().getName())
                .lore(this.describe(sender))
                .build();
    }

    @Override
    public int compareTo(MagicMob that) {
        return this.getKey().compareTo(that.getKey());
    }
}
