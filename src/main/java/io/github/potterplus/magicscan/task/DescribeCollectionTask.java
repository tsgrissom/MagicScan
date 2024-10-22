package io.github.potterplus.magicscan.task;

import com.google.common.collect.ImmutableMap;
import io.github.potterplus.api.string.HoverMessage;
import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.misc.Describable;
import io.github.potterplus.magicscan.misc.Utilities;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * A task to describe a collection of Describables to a CommandSender.
 */
@RequiredArgsConstructor
public class DescribeCollectionTask extends BukkitRunnable {

    @NonNull
    private final MagicScanController controller;

    @NonNull
    private final List<Describable> describables;

    @NonNull
    private final CommandSender to;

    @Override
    public void run() {
        controller.sendMessage(to, "describing_things", ImmutableMap.of("$amount", String.valueOf(describables.size())));

        new BukkitRunnable() {
            @Override
            public void run() {
                int step = 0;

                for (Describable d : describables) {
                    HoverMessage hm = Utilities.describeAsHoverMessage(to, d);

                    if (to instanceof Player) {
                        hm.send((Player) to);
                    } else {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                hm.sendUnfolded(to);
                            }
                        }.runTaskLater(controller.getPlugin(), (long) step * controller.getConfig().getInterval() * 4);
                    }

                    step++;
                }
            }
        }.runTaskLater(controller.getPlugin(), 40);
    }
}
