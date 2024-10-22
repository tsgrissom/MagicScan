package io.github.potterplus.magicscan.gui;

import com.google.common.collect.ImmutableMap;
import io.github.potterplus.api.item.Icon;
import io.github.potterplus.api.misc.BooleanFormatter;
import io.github.potterplus.api.ui.UserInterface;
import io.github.potterplus.api.ui.button.AutoUIButton;
import io.github.potterplus.api.ui.button.UIButton;
import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.file.ConfigFile;
import io.github.potterplus.magicscan.rule.SpellRule;
import io.github.potterplus.magicscan.scan.Scan;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;

/**
 * TODO Write docs
 */
public class ManageScanGUI extends UserInterface {

    @NonNull
    private MagicScanController controller;

    @Getter @NonNull
    private Player owner;

    public ManageScanGUI(MagicScanController controller, Player owner) {
        super(controller.getMessage("gui.manage_scan.title"), 27);

        if (!controller.getScanController().hasScan(owner)) {
            controller.sendMessage(owner, "no_queued_scan");

            return;
        }

        this.controller = controller;
        this.owner = owner;

        this.refreshButtons();
    }

    public void update(InventoryClickEvent event) {
        this.update(event.getWhoClicked());
    }

    public void update(HumanEntity human) {
        this.refreshButtons();
        this.refreshInventory(human);
    }

    public void refreshButtons() {
        this.clearButtons();

        ConfigFile config = controller.getConfig();
        Scan scan = controller.getScanController().getQueuedScan(owner);

        Icon enabled = Icon
                .of(config.getIcon("enabled", Material.GREEN_STAINED_GLASS))
                .lore(controller.getLore("gui.manage_scan.enabled_toggle.lore"));
        Icon disabled = Icon
                .of(config.getIcon("disabled", Material.RED_STAINED_GLASS))
                .lore(controller.getLore("gui.manage_scan.disabled_toggle.lore"));

        Icon readyIcon = scan.isMeetingConditions()
                ? Icon
                .of(config.getIcon("confirm", Material.EMERALD))
                .name(controller.getMessage("gui.manage_scan.execute_ready.name"))
                .lore(controller.getLore("gui.manage_scan.execute_ready.lore"))
                : Icon
                .of(config.getIcon("deny", Material.REDSTONE))
                .name(controller.getMessage("gui.manage_scan.execute_not_ready.name"))
                .lore(controller.getLore("gui.manage_scan.execute_not_ready.lore"));

        UIButton execute = new UIButton(readyIcon);

        execute.setListener(event -> {
            event.setCancelled(true);

            HumanEntity human = event.getWhoClicked();

            if (human instanceof Player) {
                human.closeInventory();

                ((Player) human).performCommand("magicscan scan execute");
            }
        });

        this.setButton(0, execute);

        UIButton time = new AutoUIButton(
                Icon
                        .of(config.getIcon("time", Material.CLOCK))
                        .name(controller.getMessage("gui.manage_scan.time.name", ImmutableMap.of("$time", controller.getConfig().getDateFormat().format(scan.getCreatedAt())))
                        )
        );

        this.setButton(8, time);

        UIButton logToFile = new UIButton(
                scan.getOptions().isLogToFile()
                        ? enabled.name(controller.getMessage("gui.manage_scan.log_to_file_enabled.name"))
                        : disabled.name(controller.getMessage("gui.manage_scan.log_to_file_disabled.name"))
        );

        logToFile.setListener(event -> {
            event.setCancelled(true);

            scan.getOptions().setLogToFile(!scan.getOptions().isLogToFile());

            controller.getScanController().putScan(owner, scan);

            this.update(event);
        });

        UIButton scanAllRuleTypes = new UIButton(
                scan.getOptions().isScanAllRuleTypes()
                        ? enabled.name(controller.getMessage("gui.manage_scan.scan_all_rule_types_enabled.name"))
                        : disabled.name(controller.getMessage("gui.manage_scan.scan_all_rule_types_disabled.name"))
        );

        scanAllRuleTypes.setListener(event -> {
            event.setCancelled(true);

            scan.getOptions().setScanAllRuleTypes(!scan.getOptions().isScanAllRuleTypes());

            controller.getScanController().putScan(owner, scan);

            this.update(event);
        });

        UIButton scanHidden = new UIButton(
                scan.getOptions().isScanHidden()
                        ? enabled.name(controller.getMessage("gui.manage_scan.scan_hidden_enabled.name"))
                        : disabled.name(controller.getMessage("gui.manage_scan.scan_hidden_disabled.name"))
        );

        scanHidden.setListener(event -> {
            event.setCancelled(true);

            scan.getOptions().setScanHidden(!scan.getOptions().isScanHidden());

            controller.getScanController().putScan(owner, scan);

            this.update(event);
        });

        UIButton visual = new UIButton(
                scan.getOptions().isVisual()
                        ? enabled.name(controller.getMessage("gui.manage_scan.visual_enabled.name"))
                        : disabled.name(controller.getMessage("gui.manage_scan.visual_disabled.name"))
        );

        visual.setListener(event -> {
            event.setCancelled(true);

            scan.getOptions().setVisual(!scan.getOptions().isVisual());

            controller.getScanController().putScan(owner, scan);

            this.update(event);
        });

        this.setButton(18, logToFile);
        this.setButton(19, scanAllRuleTypes);
        this.setButton(20, scanHidden);
        this.setButton(21, visual);

        Icon editRulesItem = Icon
                .of(config.getIcon("edit", Material.WRITABLE_BOOK))
                .name(controller.getMessage("gui.manage_scan.edit_rules.name"))
                .lore(controller.getLore("gui.manage_scan.edit_rules.lore"));

        for (SpellRule rule : controller.getSpellRules()) {
            Map<String, String> replace = ImmutableMap.of(
                    "$type", StringUtils.capitalize(rule.getRuleType().name().toLowerCase()),
                    "$rule", rule.getKey(),
                    "$bool", BooleanFormatter.ENABLED_DISABLED.format(!scan.isOverridden(rule.getKey()))
            );

            editRulesItem.addLore(controller.getMessage("gui.manage_scan.edit_rules.lore_line_format", replace));
        }

        UIButton editRules = new UIButton(editRulesItem);

        editRules.setListener(event -> {
            event.setCancelled(true);

            new RuleListEditGUI(controller, this).activate(event.getWhoClicked());
        });

        this.setButton(getSize() - 1, editRules);
    }
}
