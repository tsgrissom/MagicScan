package io.github.potterplus.magicscan.gui.describable.list;

import com.google.common.collect.ImmutableMap;
import io.github.potterplus.api.item.Icon;
import io.github.potterplus.api.misc.PluginLogger;
import io.github.potterplus.api.ui.button.AutoUIButton;
import io.github.potterplus.api.ui.button.UIButton;
import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.gui.describable.ListDescribablesGUI;
import io.github.potterplus.magicscan.magic.MagicMob;
import io.github.potterplus.magicscan.misc.Describable;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A simple paginated GUI listing available Magic mobs.
 */
public class ListMobsGUI extends ListDescribablesGUI {

    public ListMobsGUI(HumanEntity target, MagicScanController controller) {
        super("Mobs", target, controller);
    }

    void refreshToolbar() {
        MagicScanController controller = this.getController();
        UIButton clearAll = new UIButton(
                Icon
                        .of(controller.getConfig().getIcon("empty", Material.BARRIER))
                        .name(controller.getMessage("gui.list_mobs.clear_all.name"))
                        .lore(controller.getLore("gui.list_mobs.clear_all.lore"))
                        .build()
        );

        clearAll.setListener(event -> {
            event.setCancelled(true);

            if (event.getWhoClicked() instanceof Player) {
                ((Player) event.getWhoClicked()).performCommand("mmob clear");
            }
        });

        this.setToolbarItem(0, clearAll);
    }

    void refreshEntries() {
        MagicScanController controller = getController();
        List<MagicMob> mobs = new ArrayList<>(controller.getFilteredMobs());

        Collections.sort(mobs);

        mobs.forEach(this::populate);

        boolean empty = getInventory().getItem(0) == null;
        Map<String, String> countReplaceMap = ImmutableMap.of("$count", empty ? "&cNONE" : String.valueOf(getItems().size()));

        this.setTitle(controller.getMessage("gui.list_mobs.title", countReplaceMap));
    }

    @Override
    public void populate(Describable describable) {
        if (!(describable instanceof MagicMob)) {
            PluginLogger.atSevere("Cannot populate mob list GUI with incorrect describable type!");

            return;
        }

        MagicMob mob = (MagicMob) describable;
        UIButton button = new AutoUIButton(mob.describeAsItem(this.getTarget()));

        this.addButton(button);
    }

    @Override
    public void initialize() {
        this.refreshToolbar();
        this.refreshEntries();
    }
}
