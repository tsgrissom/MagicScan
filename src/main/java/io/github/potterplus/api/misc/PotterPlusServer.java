package io.github.potterplus.api.misc;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PotterPlusServer {
    public static List<Player> getOnlinePlayers() {
        Collection<? extends Player> coll = Bukkit.getOnlinePlayers();

        return new ArrayList<>(coll);
    }

    public static List<String> getOnlinePlayerNames() {
        return getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
    }
}
