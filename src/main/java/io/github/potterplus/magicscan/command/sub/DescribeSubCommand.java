package io.github.potterplus.magicscan.command.sub;

import com.google.common.collect.ImmutableMap;
import io.github.potterplus.api.command.CommandBase;
import io.github.potterplus.api.command.CommandContext;
import io.github.potterplus.api.command.CommandFlag;
import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.MagicScanPlugin;
import io.github.potterplus.magicscan.command.MagicScanCommand;
import io.github.potterplus.magicscan.gui.describable.ListDescribablesGUI;
import io.github.potterplus.magicscan.gui.describable.describe.*;
import io.github.potterplus.magicscan.magic.MagicMob;
import io.github.potterplus.magicscan.magic.MagicPath;
import io.github.potterplus.magicscan.magic.MagicSpell;
import io.github.potterplus.magicscan.magic.MagicWand;
import io.github.potterplus.magicscan.magic.spell.SpellAction;
import io.github.potterplus.magicscan.magic.spell.SpellCategory;
import io.github.potterplus.magicscan.magic.spell.SpellProgression;
import io.github.potterplus.magicscan.misc.Utilities;
import io.github.potterplus.magicscan.task.DescribeCollectionTask;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.github.potterplus.api.string.StringUtilities.equalsAny;

/**
 * TODO Write docs
 */
@RequiredArgsConstructor
public class DescribeSubCommand extends CommandBase.SubCommand {

    @NonNull
    private final MagicScanController controller;

    public MagicScanPlugin getPlugin() {
        return controller.getPlugin();
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getArgs().length < 3) {
            context.sendMessage(MagicScanCommand.createUsage("/ms describe <type> <key>"));

            return;
        }

        MagicScanController controller = getPlugin().getController();
        String sub = context.getArg(1);
        String key = context.getArg(2);
        CommandFlag uiFlag = new CommandFlag.UserInterfaceFlag();

        if (sub == null || key == null) {
            context.sendMessage(MagicScanCommand.createUsage("/ms <sub> [args]"));
        } else {
            Map<String, String> replace = ImmutableMap.of("$key", key);
            String[] keys = StringUtils.split(context.getMessage(2), " ");

            if (equalsAny(sub, "actions", "action")) {
                List<SpellAction> actions = new ArrayList<>();

                for (String k : keys) {
                    Optional<SpellAction> opt = controller.resolveAction(k);

                    if (opt.isPresent()) {
                        actions.add(opt.get());
                    } else {
                        controller.sendMessage(context, "invalid_action", ImmutableMap.of("$key", k));
                    }
                }

                if (context.isPlayer()) {
                    if (context.hasFlag(uiFlag)) {
                        if (actions.size() == 0) {
                            controller.sendMessage(context, "nothing_to_show");
                        } else if (actions.size() == 1) {
                            new DescribeActionGUI(controller, actions.get(0), context.getPlayer())
                                    .activate(context.getPlayer());
                        } else {
                            ListDescribablesGUI.custom(controller, context.getPlayer(), new ArrayList<>(actions), "gui.describing_actions.title").activate();
                        }
                    } else {
                        new DescribeCollectionTask(controller, new ArrayList<>(actions), context.getSender())
                                .runTask(getPlugin());
                    }
                } else if (context.isConsole()) {
                    if (context.hasFlag(uiFlag)) {
                        controller.sendMessage(context, "no_gui_console");
                    } else {
                        new DescribeCollectionTask(controller, new ArrayList<>(actions), context.getSender())
                                .runTask(getPlugin());
                    }
                }
            } else if (equalsAny(sub, "mobs", "mob")) {
                List<MagicMob> mobs = new ArrayList<>();

                for (String k : keys) {
                    Optional<MagicMob> opt = controller.resolveMob(k);

                    if (opt.isPresent()) {
                        mobs.add(opt.get());
                    } else {
                        controller.sendMessage(context, "invalid_mob", ImmutableMap.of("$key", k));
                    }
                }

                if (context.isPlayer()) {
                    if (context.hasFlag(uiFlag)) {
                        if (mobs.size() == 0) {
                            controller.sendMessage(context, "nothing_to_show");
                        } else if (mobs.size() == 1) {
                            new DescribeMobGUI(mobs.get(0), context.getPlayer())
                                    .activate(context.getPlayer());
                        } else {
                            ListDescribablesGUI.custom(controller, context.getPlayer(), new ArrayList<>(mobs), "gui.describing_mobs.title").activate();
                        }
                    } else {
                        new DescribeCollectionTask(controller, new ArrayList<>(mobs), context.getSender())
                                .runTask(getPlugin());
                    }
                } else if (context.isConsole()) {
                    if (context.hasFlag(uiFlag)) {
                        controller.sendMessage(context, "no_gui_console");
                    } else {
                        new DescribeCollectionTask(controller, new ArrayList<>(mobs), context.getSender())
                                .runTask(getPlugin());
                    }
                }
            } else if (equalsAny(sub, "paths", "path")) {
                List<MagicPath> paths = new ArrayList<>();

                for (String k : keys) {
                    Optional<MagicPath> opt = controller.resolvePath(k);

                    if (opt.isPresent()) {
                        paths.add(opt.get());
                    } else {
                        controller.sendMessage(context, "invalid_path", ImmutableMap.of("$key", k));
                    }
                }

                if (context.isPlayer()) {
                    if (context.hasFlag(uiFlag)) {
                        if (paths.size() == 0) {
                            controller.sendMessage(context, "nothing_to_show");
                        } else if (paths.size() == 1) {
                            new DescribePathGUI(paths.get(0), context.getPlayer())
                                    .activate(context.getPlayer());
                        } else {
                            ListDescribablesGUI.custom(controller, context.getPlayer(), new ArrayList<>(paths), "gui.describing_paths.title").activate();
                        }
                    } else {
                        new DescribeCollectionTask(controller, new ArrayList<>(paths), context.getSender())
                                .runTask(getPlugin());
                    }
                } else if (context.isConsole()) {
                    if (context.hasFlag(uiFlag)) {
                        controller.sendMessage(context, "no_gui_console");
                    } else {
                        new DescribeCollectionTask(controller, new ArrayList<>(paths), context.getSender())
                                .runTask(getPlugin());
                    }
                }
            } else if (equalsAny(sub, "categories", "category", "cat", "spellcategories", "spellcategory", "spellcat")) {
                List<SpellCategory> categories = new ArrayList<>();

                for (String k : keys) {
                    Optional<SpellCategory> opt = controller.resolveSpellCategory(k);

                    if (opt.isPresent()) {
                        categories.add(opt.get());
                    } else {
                        controller.sendMessage(context, "invalid_spell_category", ImmutableMap.of("$key", k));
                    }
                }

                if (context.isPlayer()) {
                    if (context.hasFlag(uiFlag)) {
                        if (categories.size() == 0) {
                            controller.sendMessage(context, "nothing_to_show");
                        } else if (categories.size() == 1) {
                            new DescribeSpellCategoryGUI(categories.get(0), context.getPlayer())
                                    .activate(context.getPlayer());
                        } else {
                            ListDescribablesGUI.custom(controller, context.getPlayer(), new ArrayList<>(categories), "gui.describing_spell_categories.title").activate();
                        }
                    } else {
                        new DescribeCollectionTask(controller, new ArrayList<>(categories), context.getSender())
                                .runTask(getPlugin());
                    }
                } else if (context.isConsole()) {
                    if (context.hasFlag(uiFlag)) {
                        controller.sendMessage(context, "no_gui_console");
                    } else {
                        new DescribeCollectionTask(controller, new ArrayList<>(categories), context.getSender())
                                .runTask(getPlugin());
                    }
                }
            } else if (equalsAny(sub, "spells", "spell")) {
                List<MagicSpell> spells = new ArrayList<>();

                for (String k : keys) {
                    Optional<MagicSpell> opt = controller.resolveSpell(k);

                    if (opt.isPresent()) {
                        spells.add(opt.get());
                    } else {
                        controller.sendMessage(context, "invalid_spell", ImmutableMap.of("$key", k));
                    }
                }

                if (context.isPlayer()) {
                    if (context.hasFlag(uiFlag)) {
                        if (spells.size() == 0) {
                            controller.sendMessage(context, "nothing_to_show");
                        } else if (spells.size() == 1) {
                            new DescribeSpellGUI(spells.get(0), context.getPlayer())
                                    .activate(context.getPlayer());
                        } else {
                            ListDescribablesGUI.custom(controller, context.getPlayer(), new ArrayList<>(spells), "gui.describing_spells.title").activate();
                        }
                    } else {
                        new DescribeCollectionTask(controller, new ArrayList<>(spells), context.getSender())
                                .runTask(getPlugin());
                    }
                } else if (context.isConsole()) {
                    if (context.hasFlag(uiFlag)) {
                        controller.sendMessage(context, "no_gui_console");
                    } else {
                        new DescribeCollectionTask(controller, new ArrayList<>(spells), context.getSender())
                                .runTask(getPlugin());
                    }
                }
            } else if (equalsAny(sub, "wands", "wand")) {
                List<MagicWand> wands = new ArrayList<>();

                for (String k : keys) {
                    Optional<MagicWand> opt = controller.resolveWand(k);

                    if (opt.isPresent()) {
                        wands.add(opt.get());
                    } else {
                        controller.sendMessage(context, "invalid_wand", ImmutableMap.of("$key", k));
                    }
                }

                if (context.isPlayer()) {
                    if (context.hasFlag(uiFlag)) {
                        if (wands.size() == 0) {
                            controller.sendMessage(context, "nothing_to_show");
                        } else if (wands.size() == 1) {
                            new DescribeWandGUI(wands.get(0), context.getPlayer())
                                    .activate(context.getPlayer());
                        } else {
                            ListDescribablesGUI.custom(controller, context.getPlayer(), new ArrayList<>(wands), "gui.describing_wands.title").activate();
                        }
                    } else {
                        new DescribeCollectionTask(controller, new ArrayList<>(wands), context.getSender())
                                .runTask(getPlugin());
                    }
                } else if (context.isConsole()) {
                    if (context.hasFlag(uiFlag)) {
                        controller.sendMessage(context, "no_gui_console");
                    } else {
                        new DescribeCollectionTask(controller, new ArrayList<>(wands), context.getSender())
                                .runTask(getPlugin());
                    }
                }
            } else if (equalsAny(sub, "progression", "levels", "level")) {
                if (context.getArgs().length > 2) {
                    controller.sendMessage(context, "only_one_progression");
                }

                Optional<MagicSpell> optSpell = controller.resolveSpell(key);

                if (!optSpell.isPresent()) {
                    controller.sendMessage(context, "invalid_spell", ImmutableMap.of("$key", key));

                    return;
                }

                MagicSpell spell = optSpell.get();
                Optional<SpellProgression> optProgression = spell.getProgression();

                if (!optProgression.isPresent() || !optProgression.get().exists()) {
                    controller.sendMessage(context, "no_progression");

                    return;
                }

                SpellProgression progression = optProgression.get();

                if (context.isPlayer()) {
                    if (context.hasFlag(uiFlag)) {
                        DescribeSpellProgressionGUI gui = new DescribeSpellProgressionGUI(controller, progression, context.getPlayer());

                        gui.activate();
                    } else {
                        Utilities.describe(context.getPlayer(), progression);
                    }
                } else {
                    if (context.hasFlag(uiFlag)) {
                        controller.sendMessage(context, "no_gui_console");
                    } else {
                        Utilities.describe(context.getPlayer(), progression);
                    }
                }
            } else {
                controller.sendMessage(context, "unknown_magic_type", ImmutableMap.of("$type", sub));
            }

            if (equalsAny(sub, "progression", "levels", "level")) {
                for (String k : keys) {
                    Optional<MagicSpell> opt = controller.resolveSpell(k);

                    if (opt.isPresent()) {
                        Optional<SpellProgression> optProgression = opt.get().getProgression();

                        if (optProgression.isPresent() && optProgression.get().exists()) {
                            Utilities.describe(context, optProgression.get());
                        } else {
                            controller.sendMessage(context, "no_progression");
                        }
                    } else {
                        controller.sendMessage(context, "invalid_spell", replace);
                    }
                }
            }
        }
    }
}
