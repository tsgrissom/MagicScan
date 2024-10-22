package io.github.potterplus.magicscan.gui;

import com.google.common.collect.ImmutableMap;
import io.github.potterplus.api.item.Icon;
import io.github.potterplus.api.misc.BooleanFormatter;
import io.github.potterplus.api.misc.PluginLogger;
import io.github.potterplus.api.ui.UserInterface;
import io.github.potterplus.api.ui.button.AutoUIButton;
import io.github.potterplus.api.ui.button.UIButton;
import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.file.ConfigFile;
import io.github.potterplus.magicscan.rule.SpellRule;
import io.github.potterplus.magicscan.scan.Scan;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;

/**
 * TODO Write docs
 */
public class RuleListEditGUI extends UserInterface {

    @NonNull
    private final MagicScanController controller;

    private final ManageScanGUI returnTo;

    public RuleListEditGUI(MagicScanController controller, ManageScanGUI returnTo) {
        super(controller.getMessage("gui.rule_list_edit.title"), 54);

        this.controller = controller;
        this.returnTo = returnTo;

        this.refreshButtons();
    }

    public void update(InventoryClickEvent event) {
        this.refreshButtons();
        this.refreshInventory(event.getWhoClicked());
    }

    public void refreshButtons() {
        this.clearButtons();

        ConfigFile config = controller.getConfig();

        Icon enabled = Icon.of(config.getIcon("enabled", Material.GREEN_STAINED_GLASS));
        Icon disabled = Icon.of(config.getIcon("disabled", Material.RED_STAINED_GLASS));

        Player owner = returnTo.getOwner();
        Scan scan = controller.getScanController().getQueuedScan(owner);

        if (scan == null) {
            PluginLogger.atSevere()
                    .with("Cannot refresh rule list GUI buttons from null scan!")
                    .print();

            return;
        }

        Icon spells = Icon
                .of(config.getIcon("info", Material.NAME_TAG))
                .name(controller.getMessage("gui.rule_list_edit.spell_rules.name"));

        for (SpellRule rule : controller.getSpellRules()) {
            Map<String, String> replace = ImmutableMap.of(
                    "$rule", rule.getKey(),
                    "$bool", BooleanFormatter.ENABLED_DISABLED.format(!scan.isOverridden(rule.getKey()))
            );

            spells.addLore(controller.getMessage("gui.rule_list_edit.spell_rules.lore_line_format", replace));
        }

        this.addButton(new AutoUIButton(spells));

        for (SpellRule rule : controller.getSpellRules()) {
            Map<String, String> replace = ImmutableMap.of("$rule", rule.getKey());
            UIButton button = new UIButton(
                    scan.isOverridden(rule.getKey())
                            ? disabled
                            .name(controller.getMessage("gui.rule_list_edit.spell_rule_disabled.name", replace))
                            .lore(controller.getLore("gui.rule_list_edit.spell_rule_disabled.lore"))
                            : enabled
                            .name(controller.getMessage("gui.rule_list_edit.spell_rule_enabled.name", replace))
                            .lore(controller.getLore("gui.rule_list_edit.spell_rule_enabled.lore"))
            );

            button.setListener(event -> {
                event.setCancelled(true);

                HumanEntity human = event.getWhoClicked();

                if (human instanceof Player) {
                    Player player = (Player) human;

                    ClickType click = event.getClick();

                    if (click.equals(ClickType.LEFT)) {
                        player.performCommand("magicscan scan override " + rule.getKey());
                    } else if (click.equals(ClickType.RIGHT)) {
                        for (SpellRule r : controller.getSpellRules()) {
                            if (r.getKey().equals(rule.getKey())) {
                                player.performCommand("magicscan scan enable " + rule.getKey());

                                continue;
                            }

                            player.performCommand("magicscan scan disable " + r.getKey());
                        }
                    }
                }

                this.update(event);
            });

            this.addButton(button);
        }

        UIButton enableAll = new UIButton(
                Icon
                .of(config.getIcon("enable", Material.EMERALD))
                .name(controller.getMessage("gui.rule_list_edit.enable_all.name"))
                .lore(controller.getLore("gui.rule_list_edit.enable_all.lore"))
        );

        enableAll.setListener(event -> {
            event.setCancelled(true);

            HumanEntity human = event.getWhoClicked();

            if (human instanceof Player) {
                Player player = (Player) human;

                for (SpellRule rule : controller.getSpellRules()) {
                    player.performCommand("magicscan scan enable " + rule.getKey());
                }

                this.update(event);
            }
        });

        UIButton disableAll = new UIButton(
                Icon
                        .of(config.getIcon("disable", Material.REDSTONE))
                        .name(controller.getMessage("gui.rule_list_edit.disable_all.name"))
                        .lore(controller.getLore("gui.rule_list_edit.disable_all.lore"))
        );

        disableAll.setListener(event -> {
            event.setCancelled(true);

            HumanEntity human = event.getWhoClicked();

            if (human instanceof Player) {
                Player player = (Player) human;

                for (SpellRule rule : controller.getSpellRules()) {
                    player.performCommand("magicscan scan disable " + rule.getKey());
                }

                this.update(event);
            }
        });

        UIButton toggleAll = new UIButton(
                Icon
                        .of(config.getIcon("toggle", Material.NETHER_STAR))
                        .name(controller.getMessage("gui.rule_list_edit.toggle_all.name"))
                        .lore(controller.getLore("gui.rule_list_edit.toggle_all.lore"))
        );

        toggleAll.setListener(event -> {
            event.setCancelled(true);

            HumanEntity human = event.getWhoClicked();

            if (human instanceof Player) {
                Player player = (Player) human;

                for (SpellRule rule : controller.getSpellRules()) {
                    player.performCommand("magicscan scan override " + rule.getKey());
                }

                this.update(event);
            }
        });

        if (returnTo != null) {
            UIButton returnToPage = new UIButton(
                    Icon
                            .of(config.getIcon("back", Material.ARROW))
                            .name(controller.getMessage("gui.rule_list_edit.return.name"))
                            .lore(controller.getLore("gui.rule_list_edit.return.lore"))
            );

            returnToPage.setListener(event -> returnTo.update(event.getWhoClicked()));

            this.setButton(this.getSize() - 1, returnToPage);
        }

        this.setButton(45, enableAll);
        this.setButton(46, disableAll);
        this.setButton(47, toggleAll);
    }
}
