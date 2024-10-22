package io.github.potterplus.magicscan.gui.prompt;

import com.google.common.collect.ImmutableMap;
import io.github.potterplus.api.item.Icon;
import io.github.potterplus.api.ui.prompt.ConfirmPrompt;
import io.github.potterplus.magicscan.MagicScanController;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Copyright (c) 2013-2020 Tyler Grissom
 */
public class DeleteOtherScanConfirmPrompt extends ConfirmPrompt {

    private final MagicScanController controller;
    private final CommandSender target, initiator;

    public DeleteOtherScanConfirmPrompt(MagicScanController controller, CommandSender target, CommandSender initiator) {
        super("Delete &e" + target.getName() + "'s Scan?", Icon
                .start(Material.NAME_TAG)
                .name("&7Are you sure you want to delete &e" + target.getName() + "'s &7scan?")
                .lore("&4Warning: &cThis cannot be undone!"));

        this.controller = controller;
        this.target = target;
        this.initiator = initiator;
    }

    @Override
    public void onConfirm(InventoryClickEvent event) {
        controller.getScanController().removeScan(target);
        controller.sendMessage(initiator, "scan_deleted_other", ImmutableMap.of("$name", target.getName()));
        controller.sendMessage(target, "scan_cleared", ImmutableMap.of("$name", initiator.getName()));
    }

    @Override
    public void onCancel(Player player) {
        controller.sendMessage(initiator, "scan_not_deleted_other", ImmutableMap.of("$name", target.getName()));
    }
}
