package io.github.potterplus.api.ui;

import io.github.potterplus.api.misc.PotterPlusServer;
import io.github.potterplus.api.ui.button.UIButton;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.Collection;

public abstract class PlayerSelectorUI extends PaginatedUserInterface {

    @NonNull private final Collection<Player> players;

    public PlayerSelectorUI(String name, Collection<Player> players) {
        super(name);

        this.players = players;

        refreshButtons();
    }

    public PlayerSelectorUI(String name) {
        this(name, PotterPlusServer.getOnlinePlayers());
    }

    public void refreshButtons() {
        clearButtons();

        for (Player p : players) {
            addButton(createButton(p));
        }
    }

    public abstract UIButton createButton(Player target);
}
