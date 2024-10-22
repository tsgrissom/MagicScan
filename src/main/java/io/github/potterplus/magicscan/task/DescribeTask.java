package io.github.potterplus.magicscan.task;

import io.github.potterplus.magicscan.misc.Describable;
import io.github.potterplus.magicscan.misc.Utilities;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * A task to describe a Describable to a CommandSender.
 */
@RequiredArgsConstructor
public class DescribeTask extends BukkitRunnable {

    @NonNull
    private Describable describable;

    @NonNull
    private CommandSender to;

    @Override
    public void run() {
        if (to instanceof Player) {

        }
        Utilities.describe(to, describable);
    }
}
