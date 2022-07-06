package io.github.potterplus.api.misc;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Optional;

public class ServerUtilities {

    public static Optional<World> getWorld(String worldName) {
        World w = Bukkit.getWorld(worldName);

        if (w != null) {
            return Optional.of(w);
        } else {
            return Optional.empty();
        }
    }
}
