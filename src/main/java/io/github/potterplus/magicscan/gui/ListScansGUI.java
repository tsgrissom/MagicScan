package io.github.potterplus.magicscan.gui;

import io.github.potterplus.api.item.Icon;
import io.github.potterplus.api.misc.PluginLogger;
import io.github.potterplus.api.ui.button.AutoUIButton;
import io.github.potterplus.api.ui.button.UIButton;
import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.file.ConfigFile;
import io.github.potterplus.magicscan.gui.describable.ListDescribablesGUI;
import io.github.potterplus.magicscan.gui.prompt.ClearScansConfirmPrompt;
import io.github.potterplus.magicscan.misc.Describable;
import io.github.potterplus.magicscan.scan.Scan;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO Write docs
 */
public class ListScansGUI extends ListDescribablesGUI {

    @Getter
    private Map<String, Scan> scans;

    public ListScansGUI(Player target, MagicScanController controller) {
        super(controller.getMessage("gui.list_scans.title"), target, controller);
    }

    public void refreshScans() {
        if (scans == null) {
            scans = new HashMap<>();
        } else {
            scans.clear();
        }

        for (Map.Entry<String, Scan> entry : getController().getScanController().getQueuedScans().entrySet()) {
            String name = entry.getKey();
            Scan scan = entry.getValue();

            if (!name.equals(getTarget().getName())) {
                scans.put(name, scan);
            }
        }
    }

    public UIButton createButton(Scan scan) {
        Icon item = Icon.of(scan.describeAsItem(getTarget()));
        boolean isOwned = scan.getSender().equals(getTarget());

        if (isOwned) {
            item.addLore("&8> &aLeft-click &7to manage");
            item.addLore("&8> &cRight-click &7to cancel");
        } else {
            if (getTarget().hasPermission("magicscan.command.scan.delete")) {
                item.addLore("&8> &cRight-click &7to force delete");
            }
        }

        UIButton button = new UIButton(item);

        button.setListener(event -> {
            event.setCancelled(true);

            if (!(getTarget() instanceof Player)) {
                return;
            }

            Player target = (Player) getTarget();
            ClickType click = event.getClick();

            if (click.equals(ClickType.LEFT)) {
                if (isOwned) {
                    target.performCommand("magicscan scan manage");
                    target.closeInventory();
                }
            } else if (click.equals(ClickType.RIGHT)) {
                if (isOwned) {
                    target.performCommand("magicscan scan delete");

                    this.refreshInventory(event.getWhoClicked());
                } else {
                    target.performCommand("magicscan scan delete " + scan.getSender().getName());

                    this.refreshInventory(event.getWhoClicked());
                }
            }
        });

        return button;
    }

    @Override
    public void populate(Describable describable) {
        if (!(describable instanceof Scan)) {
            PluginLogger.atSevere()
                    .with("Cannot populate scan list GUI with incorrect type!")
                    .print();

            return;
        }

        Scan scan = (Scan) describable;
        UIButton button = this.createButton(scan);

        this.addButton(button);
    }

    public void update(HumanEntity human) {
        this.refreshScans();
        this.refreshToolbar();
        this.resetPage();
        this.refreshEntries();
        this.refreshInventory(human);
    }

    public void update(InventoryClickEvent event) {
        this.update(event.getWhoClicked());
    }

    public void refreshToolbar() {
        ConfigFile config = getController().getConfig();

        if (getTarget().hasPermission("magicscan.command.scan.clear")) {
            UIButton clear = new UIButton(
                    Icon
                            .of(config.getIcon("empty", Material.BARRIER))
                            .name("&cClear all scans")
                            .lore("&8> &7Click to clear all scans")
            );

            clear.setListener(event -> {
                event.setCancelled(true);

                HumanEntity human = event.getWhoClicked();

                human.closeInventory();

                if (human instanceof Player) {
                    Player player = (Player) human;

                    if (player.hasPermission("magicscan.command.scan.clear")) {
                        new ClearScansConfirmPrompt(getController(), this).activate(player);
                    }
                }
            });

            this.setToolbarItem(0, clear);
        }

        UIButton create = getController().getScanController().hasScan(getTarget())
                ? createButton(getController().getScanController().getQueuedScan(getTarget()))
                : new UIButton(
                Icon
                        .start(Material.EMERALD)
                        .name("&aCreate new scan")
                        .lore("&8> &7Click to create a new scan")
        );

        if (!getController().getScanController().hasScan(getTarget())) {
            create.setListener(event -> {
                event.setCancelled(true);

                HumanEntity human = event.getWhoClicked();

                if (human instanceof Player) {
                    Player player = (Player) human;

                    player.performCommand("magicscan scan create");
                }

                this.update(event);
            });
        }

        this.setToolbarItem(8, create);
    }

    public void refreshEntries() {
        this.getScans().values().forEach(this::populate);
    }

    @Override
    public void initialize() {
        this.refreshScans();
        this.refreshToolbar();
        this.refreshEntries();

        if (getInventory().getItem(0) == null) {
            Icon item = Icon.start(Material.BARRIER);

            if (getController().getScanController().hasScan(getTarget())) {
                item.name("&cNo other scans found");
            } else {
                item.name("&cNo scans found");
            }

            this.addButton(new AutoUIButton(item));
        }
    }
}
