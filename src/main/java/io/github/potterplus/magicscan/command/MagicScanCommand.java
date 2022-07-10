package io.github.potterplus.magicscan.command;

import com.google.common.collect.ImmutableMap;
import io.github.potterplus.api.command.CommandBase;
import io.github.potterplus.api.command.CommandContext;
import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.MagicScanPlugin;
import io.github.potterplus.magicscan.command.sub.*;
import io.github.potterplus.magicscan.magic.MagicMob;
import io.github.potterplus.magicscan.magic.MagicPath;
import io.github.potterplus.magicscan.magic.MagicSpell;
import io.github.potterplus.magicscan.magic.MagicWand;
import io.github.potterplus.magicscan.magic.spell.SpellAction;
import io.github.potterplus.magicscan.magic.spell.SpellCategory;
import io.github.potterplus.magicscan.scan.Scan;
import io.github.potterplus.magicscan.scan.ScanController;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static io.github.potterplus.api.string.StringUtilities.color;
import static io.github.potterplus.api.string.StringUtilities.equalsAny;
import static org.apache.commons.lang.StringUtils.startsWithIgnoreCase;

public class MagicScanCommand extends CommandBase<MagicScanPlugin> {

    public MagicScanCommand(@NonNull MagicScanPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getLabel() {
        return "magicscan";
    }

    public static List<String> getHelp() {
        return color(
                "  &8>> &d/ms Help",
                "  &a[Optional Arg] &c<Required Arg> &3--Optional Flag",
                "  &8/&7ms help &8> &6Brings up these options",
                "  &8/&7ms reload &8> &6Reloads config and refreshes sources",
                "  &8/&7ms metrics &8> &6Numbers of stuff MagicScan tracks",
                "  &8/&7ms quickscan &8> &6Initiates and performs a quick scan",
                "  &8/&7ms scan &8> &6Scan related sub-commands",
                "    &dAliases&8: &7/mss",
                "  &8/&7ms desc &c<Type> <Key> &a[Key2...] &3-u &8> &6Describes elements of Magic",
                "    &cType&8: &7Any of&8:",
                "      &espell&8, &ecategory&8, &epath&8, &ewand&8, &eaction&8, &emob&8, &eprogression",
                "    &cKey&8: &7The key of the thing you want to describe&8.",
                "    &aKey2&8: &7Any more keys are delineated by a space&8.",
                "    &3--ui&8: &7View in GUI&8.",
                "  &8/&7ms list &c<Type> &3-u &8> &6Lists elements of Magic",
                "    &cType&8: &7Any of&8:",
                "      &espells&8, &ecategories&8, &epaths&8, &ewands&8, &eactions&8, &emobs",
                "    &3--ui&8: &7View in GUI&8."
        );
    }

    public static String createUsage(String usage) {
        return color("&dMS&5> &cUsage&8: &4" + usage + "&8. &cDo &4/ms ? &cfor help&8.");
    }

    @Override
    public void execute(CommandContext context) {
        if (!equalsAny(context.getLabel(), "magicscan", "ms", "mss")) {
            return;
        }

        MagicScanController controller = getPlugin().getController();

        if (!context.hasPermission("magicscan.command")) {
            controller.sendMessage(context, "no_permission");

            return;
        }

        if (equalsAny(context.getLabel(), "mss")) {
            String[] args = context.getArgs();
            StringBuilder qualifiedCommand = new StringBuilder("magicscan scan");

            if (args.length > 0) {
                for (String arg : args) {
                    qualifiedCommand.append(" ")
                            .append(arg);
                }
            }

            context.performCommand(qualifiedCommand.toString().trim());

            return;
        }

        if (!context.hasSub()) {
            context.sendMessage("&dMS&5> &cDo &4/ms ? &cto view available sub-commands.");

            return;
        }

        String sub = context.getSub();

        if (equalsAny(sub, "help", "?")) {
            context.sendMessage(getHelp());
        } else if (equalsAny(sub, "reload", "load")) {
            if (!context.hasPermission("magicscan.command.reload")) {
                controller.sendMessage(context, "no_permission");

                return;
            }

            // TODO A reload method in controller which passes a CommandSender and automatically handles all this

            context.sendMessage("&7Reloading configuration and refreshing sources...");

            try {
                getPlugin().reloadConfig();

                controller.getRulesFile().reload();
                controller.getMessages().reload();

                controller.refresh();

                context.sendMessage("&aReloaded MagicScan!");
            } catch (Exception exception) {
                context.sendMessage("&cFailed to reload MagicScan! Check console for the full error.");

                exception.printStackTrace();
            }
        } else if (equalsAny(sub, "scan", "s")) {
            context.delegate(new ScanSubCommand(controller));
        } else if (equalsAny(sub, "list", "l")) {
            context.delegate(new ListSubCommand(controller));
        } else if (equalsAny(sub, "describe", "desc", "d")) {
            context.delegate(new DescribeSubCommand(controller));
        } else if (equalsAny(sub, "metrics", "numbers")) {
            context.delegate(new MetricsSubCommand(controller));
        } else if (equalsAny(sub, "quickscan", "qs")) {
            context.delegate(new QuickScanSubCommand(controller));
        } else if (equalsAny(sub, "actions", "mobs", "paths", "categories", "spells", "wands")) {
            context.performCommand("magicscan list " + sub + (context.isPlayer() ? " -u" : ""));
        } else if (equalsAny(sub, "action", "mob", "path", "category", "spell", "progression", "wand")) {
            String key = context.getArg(1);

            if (key == null) {
                context.sendMessage(createUsage("/ms describe <type> <key>"));

                return;
            }

            context.performCommand("magicscan describe <type> <key>", ImmutableMap.of("<type>", sub, "<key>", key));
        } else {
            context.sendMessage("&dMS&5> &cUnknown sub-command &4'" + sub + "'&c. Do &4/ms ? &cfor help.");
        }
    }

    @Override
    public List<String> tab(CommandContext context) {
        List<String> options = new ArrayList<>();

        if (!context.hasPermission("magicscan.command")) {
            return options;
        }

        MagicScanController controller = getPlugin().getController();
        List<String> subcommands = Arrays.asList("help", "describe", "list", "scan", "quickscan", "reload", "metrics");
        List<String> scanSubcommands = Arrays.asList("create", "delete", "list", "manage", "clear", "toggle", "override", "enable", "disable", "rules", "help");
        String[] args = context.getArgs();

        if (equalsAny(context.getLabel(), "mss")) {
            if (args.length == 0) {
                options.addAll(scanSubcommands);
            } else {
                String sub = args[0];

                if (args.length == 1) {
                    for (String s : scanSubcommands) {
                        if (startsWithIgnoreCase(sub, s)) {
                            options.add(s);
                        }
                    }
                }
            }
        } else if (equalsAny(context.getLabel(), "magicscan", "ms")) {
            // /mss prop <prop> [bool] - Describes property or sets the value
            // /mss rule <type> <rule> [bool] - Describes the rule
            // /mss

            List<String> singularDescribables = Arrays.asList(
                    "action",
                    "mob",
                    "path",
                    "category",
                    "spell",
                    "progression",
                    "wand"
            );
            List<String> pluralDescribables = Arrays.asList(
                    "actions",
                    "mobs",
                    "paths",
                    "categories",
                    "spells",
                    "wands"
            );
            List<String> bools = Arrays.asList("true", "false", "toggle");

            if (args.length == 0) {
                options.addAll(subcommands);
            } else if (args.length == 1) {
                String sub = args[0];

                if (equalsAny(sub,"describe", "desc", "d")) {
                    options.addAll(singularDescribables);
                } else if (equalsAny(sub, "list", "l")) {
                    options.addAll(pluralDescribables);
                } else if (equalsAny(sub, "scan", "s")) {
                    options.addAll(scanSubcommands);
                } else {
                    for (String s : subcommands) {
                        if (startsWithIgnoreCase(s, sub) && !s.equalsIgnoreCase(sub)) {
                            options.add(s);
                        }
                    }
                }
            } else {
                String sub = args[0];
                String type = args[1];

                if (equalsAny(sub,"describe", "desc", "d")) {
                    if (args.length == 2) {
                        options.addAll(singularDescribables);
                    } else {
                        if (equalsAny(type, "actions", "action")) {
                            for (int i = 2; i < args.length; i++) {
                                for (SpellAction action : controller.getActions()) {
                                    if (options.contains(action.getClassName())) continue;

                                    if (startsWithIgnoreCase(action.getClassName(), args[i]) && !action.getClassName().equalsIgnoreCase(args[i])) {
                                        options.add(action.getClassName());
                                    }
                                }
                            }
                        } else if (equalsAny(type, "mobs", "mob")) {
                            for (int i = 2; i < args.length; i++) {
                                for (MagicMob mob : controller.getMobs()) {
                                    if (options.contains(mob.getKey())) continue;

                                    if (startsWithIgnoreCase(mob.getKey(), args[i]) && !mob.getKey().equalsIgnoreCase(args[i])) {
                                        options.add(mob.getKey());
                                    }
                                }
                            }
                        } else if (equalsAny(type, "paths", "path")) {
                            for (int i = 2; i < args.length; i++) {
                                for (MagicPath path : controller.getPaths()) {
                                    if (options.contains(path.getKey())) continue;

                                    if (startsWithIgnoreCase(path.getKey(), args[i]) && !path.getKey().equalsIgnoreCase(args[i])) {
                                        options.add(path.getKey());
                                    }
                                }
                            }
                        } else if (equalsAny(type, "categories", "category", "spellcategories", "spellcategory")) {
                            for (int i = 2; i < args.length; i++) {
                                for (SpellCategory cat : controller.getSpellCategories()) {
                                    if (options.contains(cat.getKey())) continue;

                                    if (startsWithIgnoreCase(cat.getKey(), args[i]) && !cat.getKey().equalsIgnoreCase(args[i])) {
                                        options.add(cat.getKey());
                                    }
                                }
                            }
                        } else if (equalsAny(type, "spells", "spell", "progression", "levels", "level")) {
                            for (int i = 2; i < args.length; i++) {
                                for (MagicSpell spell : controller.getSpells()) {
                                    if (options.contains(spell.getKey())) continue;

                                    if (startsWithIgnoreCase(spell.getKey(), args[i]) && !spell.getKey().equalsIgnoreCase(args[i])) {
                                        options.add(spell.getKey());
                                    }
                                }
                            }
                        } else if (equalsAny(type, "wands", "wand")) {
                            for (int i = 2; i < args.length; i++) {
                                for (MagicWand wand : controller.getWands()) {
                                    if (options.contains(wand.getKey())) continue;

                                    if (startsWithIgnoreCase(wand.getKey(), args[i]) && !wand.getKey().equalsIgnoreCase(args[i])) {
                                        options.add(wand.getKey());
                                    }
                                }
                            }
                        }

                        if (args.length > 3 && context.isPlayer()) {
                            options.add("-u");
                        }
                    }
                } else if (equalsAny(sub, "list", "l")) {
                    if (args.length == 2) {
                        options.addAll(pluralDescribables);
                    } else {
                        for (String s : pluralDescribables) {
                            if (startsWithIgnoreCase(args[1], s) && !s.equalsIgnoreCase(args[1])) {
                                options.add(s);
                            }
                        }

                        if (!options.contains("-u") && !options.contains("--ui") && context.isPlayer()) {
                            options.add("-u");
                        }
                    }
                } else if (equalsAny(sub, "scan", "s")) {
                    ScanController scans = controller.getScanController();

                    sub = args[1];

                    if (args.length == 2) {
                        for (String s : scanSubcommands) {
                            if (startsWithIgnoreCase(sub, s) && !s.equalsIgnoreCase(sub)) {
                                options.add(s);
                            }
                        }
                    } else {
                        String arg2 = args[2];

                        if (args.length == 3) {
                            if (equalsAny(sub, "delete", "del")) {
                                if (scans.getQueuedScans() != null && !scans.getQueuedScans().isEmpty()) {
                                    for (Map.Entry<String, Scan> entry : scans.getQueuedScans().entrySet()) {
                                        String key = entry.getKey();

                                        if (startsWithIgnoreCase(arg2, key) && !options.contains(arg2)) {
                                            options.add(key);
                                        }
                                    }
                                }
                            } else if (equalsAny(sub, "property", "prop")) {
                                for (Scan.Options.Property prop : Scan.Options.Property.values()) {
                                    if (startsWithIgnoreCase(arg2, prop.getShortName()) && !options.contains(prop.getShortName())) {
                                        options.add(prop.getShortName());
                                    }
                                }
                            }
                        } else if (args.length == 4) {
                            String arg3 = args[3];

                            if (equalsAny(sub, "property", "prop")) {
                                if (Scan.Options.Property.getProperty(arg2) != null) {
                                    for (String b : bools) {
                                        if (startsWithIgnoreCase(arg3, b) && !options.contains(b)) {
                                            options.add(b);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return options;
    }
}
