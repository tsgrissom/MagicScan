package io.github.potterplus.magicscan.gui.describable.describe;

import com.google.common.collect.ImmutableMap;
import io.github.potterplus.magicscan.gui.describable.DescribeDescribableGUI;
import io.github.potterplus.magicscan.magic.MagicMob;
import org.bukkit.entity.HumanEntity;

/**
 * A GUI describing a specific Magic mob.
 */
public class DescribeMobGUI extends DescribeDescribableGUI {

    public DescribeMobGUI(MagicMob mob, HumanEntity human) {
        super(mob.getController().getMessage("gui.describe_mob.title", ImmutableMap.of("$key", mob.getKey())), mob, human);
    }
}
