package io.github.potterplus.magicscan.command.sub;

import io.github.potterplus.api.command.CommandBase;
import io.github.potterplus.api.command.CommandContext;
import io.github.potterplus.api.command.CommandFlag;
import io.github.potterplus.api.item.Icon;
import io.github.potterplus.api.ui.prompt.ConfirmPrompt;
import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.MagicScanPlugin;
import io.github.potterplus.magicscan.command.MagicScanCommand;
import io.github.potterplus.magicscan.gui.describable.list.*;
import io.github.potterplus.magicscan.gui.prompt.DescribeLargeCollectionConfirmPrompt;
import io.github.potterplus.magicscan.task.DescribeCollectionTask;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

import java.util.ArrayList;

import static io.github.potterplus.api.string.StringUtilities.equalsAny;

/**
 * TODO Write docs
 */
@RequiredArgsConstructor
public class ListSubCommand extends CommandBase.SubCommand {

    @NonNull
    private MagicScanController controller;

    public MagicScanPlugin getPlugin() {
        return controller.getPlugin();
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getArgs().length < 2) {
            context.sendMessage(MagicScanCommand.createUsage("/ms list <type>"));

            return;
        }

        MagicScanController controller = getPlugin().getController();
        String sub = context.getArg(1);
        CommandFlag uiFlag = new CommandFlag.UserInterfaceFlag();

        if (equalsAny(sub, "paths", "path")) {
            if (context.isPlayer()) {
                if (context.hasFlag(uiFlag)) {
                    new ListPathsGUI(context.getPlayer(), controller).activate();
                } else {
                    new DescribeCollectionTask(controller, new ArrayList<>(controller.getPaths()), context.getSender()).runTask(getPlugin());
                }
            } else {
                if (context.hasFlag(uiFlag)) {
                    controller.sendMessage(context, "no_gui_console");
                } else {
                    new DescribeCollectionTask(controller, new ArrayList<>(controller.getPaths()), context.getSender()).runTask(getPlugin());
                }
            }
        } else if (equalsAny(sub, "category", "cat", "categories", "spellcategory", "spellcat")) {
            if (context.isPlayer()) {
                if (context.hasFlag(uiFlag)) {
                    new ListSpellCategoriesGUI(context.getPlayer(), controller).activate();
                } else {
                    new DescribeCollectionTask(controller, new ArrayList<>(controller.getSpellCategories()), context.getSender()).runTask(getPlugin());
                }
            } else {
                if (context.hasFlag(uiFlag)) {
                    controller.sendMessage(context, "no_gui_console");
                } else {
                    new DescribeCollectionTask(controller, new ArrayList<>(controller.getSpellCategories()), context.getSender()).runTask(getPlugin());
                }
            }
        } else if (equalsAny(sub, "spells", "spell")) {
            if (context.isPlayer()) {
                if (context.hasFlag(uiFlag)) {
                    new ListSpellsGUI(context.getPlayer(), controller).activate();
                } else {
                    ConfirmPrompt confirm = new DescribeLargeCollectionConfirmPrompt(
                            controller,
                            "&7Describe &e" + controller.getSpells().size() + " &7spells?",
                            new ArrayList<>(controller.getSpells()),
                            context.getSender()
                    );

                    confirm.setInfoItem(
                            Icon
                            .start(Material.PAPER)
                            .name("&cAre you sure you want to describe that many spells?")
                            .lore("&7You can do &e/ms list spells --gui &7in-game to", "&7list them in a GUI&8.")
                    );
                    confirm.activate(context.getPlayer());
                }
            } else {
                if (context.hasFlag(uiFlag)) {
                    controller.sendMessage(context, "no_gui_console");
                } else {
                    new DescribeCollectionTask(controller, new ArrayList<>(controller.getSpells()), context.getSender()).runTask(getPlugin());
                }
            }
        } else if (equalsAny(sub, "wands", "wand")) {
            if (context.isPlayer()) {
                if (context.hasFlag(uiFlag)) {
                    new ListWandsGUI(context.getPlayer(), controller).activate();
                } else {
                    new DescribeCollectionTask(controller, new ArrayList<>(controller.getWands()), context.getSender()).runTask(getPlugin());
                }
            } else {
                if (context.hasFlag(uiFlag)) {
                    controller.sendMessage(context, "no_gui_console");
                } else {
                    new DescribeCollectionTask(controller, new ArrayList<>(controller.getWands()), context.getSender()).runTask(getPlugin());
                }
            }
        } else if (equalsAny(sub, "actions", "action")) {
            if (context.isPlayer()) {
                if (context.hasFlag(uiFlag)) {
                    new ListActionsGUI(context.getPlayer(), controller).activate();
                } else {
                    new DescribeCollectionTask(controller, new ArrayList<>(controller.getActions()), context.getSender()).runTask(getPlugin());
                }
            } else {
                if (context.hasFlag(uiFlag)) {
                    controller.sendMessage(context, "no_gui_console");
                } else {
                    new DescribeCollectionTask(controller, new ArrayList<>(controller.getActions()), context.getSender()).runTask(getPlugin());
                }
            }
        } else if (equalsAny(sub, "mobs", "mob")) {
            if (context.isPlayer()) {
                if (context.hasFlag(uiFlag)) {
                    new ListMobsGUI(context.getPlayer(), controller).activate();
                } else {
                    new DescribeCollectionTask(controller, new ArrayList<>(controller.getMobs()), context.getSender()).runTask(getPlugin());
                }
            } else {
                if (context.hasFlag(uiFlag)) {
                    controller.sendMessage(context, "no_gui_console");
                } else {
                    new DescribeCollectionTask(controller, new ArrayList<>(controller.getMobs()), context.getSender()).runTask(getPlugin());
                }
            }
        } else {
            context.sendMessage("&dMS&5> &cUnknown Magic type &4'" + sub + "'&c. Do &4/ms ? &cfor help.");
        }
    }
}
