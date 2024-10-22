package io.github.potterplus.magicscan.gui.describable.describe;

import com.google.common.collect.ImmutableMap;
import io.github.potterplus.magicscan.gui.describable.DescribeDescribableGUI;
import io.github.potterplus.magicscan.magic.MagicWand;
import org.bukkit.entity.HumanEntity;

/**
 * A GUI describing a specific Magic wand.
 */
public class DescribeWandGUI extends DescribeDescribableGUI {

    public DescribeWandGUI(MagicWand wand, HumanEntity human) {
        super(wand.getController().getMessage("gui.describe_wand.title", ImmutableMap.of("$key", wand.getKey())), wand, human);
    }
}
