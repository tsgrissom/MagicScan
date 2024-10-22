package io.github.potterplus.magicscan.misc;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * A mixin for an element that can be described
 */
public interface Describable {

    List<String> describe(CommandSender sender);
    ItemStack describeAsItem(CommandSender sender);
}
