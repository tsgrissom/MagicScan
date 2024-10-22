package io.github.potterplus.magicscan.gui.describable.describe;

import com.google.common.collect.ImmutableMap;
import io.github.potterplus.magicscan.gui.describable.DescribeDescribableGUI;
import io.github.potterplus.magicscan.magic.MagicPath;
import org.bukkit.entity.HumanEntity;

/**
 * A GUI describing a specific Magic path.
 */
public class DescribePathGUI extends DescribeDescribableGUI {

    public DescribePathGUI(MagicPath path, HumanEntity human) {
        super(path.getController().getMessage("gui.describe_path.title", ImmutableMap.of("$key", path.getKey())), path, human);
    }
}
