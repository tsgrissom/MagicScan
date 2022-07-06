package io.github.potterplus.api.item;

import lombok.Builder;
import lombok.Data;
import org.bukkit.enchantments.Enchantment;

/**
 * Represents an enchantment and the logic to use when applying it.
 */
@Builder @Data
public class Enchant {

    private Enchantment type;
    private int level;
    private boolean ignoreLevel;
}