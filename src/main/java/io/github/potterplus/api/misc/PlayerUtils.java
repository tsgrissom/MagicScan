package io.github.potterplus.api.misc;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

/**
 * A bunch of shorthand player methods. Create one of these wherever you need it.
 */
public class PlayerUtils {

    public double getMaxHealth(Player target) {
        AttributeInstance maxHealth = target.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        double d;

        if (maxHealth == null) {
            d = 20;
        } else {
            d = maxHealth.getValue();
        }

        return d;
    }
}
