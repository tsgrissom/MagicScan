package io.github.potterplus.magicscan.command.sub;

import com.google.common.collect.ImmutableMap;
import io.github.potterplus.api.command.CommandBase;
import io.github.potterplus.api.command.CommandContext;
import io.github.potterplus.api.command.CommandFlag;
import io.github.potterplus.api.misc.BooleanFormatter;
import io.github.potterplus.api.misc.FriendlyBooleans;
import io.github.potterplus.api.string.StringUtilities;
import io.github.potterplus.api.ui.UserInterface;
import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.MagicScanPlugin;
import io.github.potterplus.magicscan.gui.ListScansGUI;
import io.github.potterplus.magicscan.gui.ManageScanGUI;
import io.github.potterplus.magicscan.gui.RuleListEditGUI;
import io.github.potterplus.magicscan.gui.prompt.ClearScansConfirmPrompt;
import io.github.potterplus.magicscan.gui.prompt.DeleteOtherScanConfirmPrompt;
import io.github.potterplus.magicscan.gui.prompt.ExecuteScanConfirmPrompt;
import io.github.potterplus.magicscan.gui.prompt.OverwriteExistingScanConfirmPrompt;
import io.github.potterplus.magicscan.misc.Utilities;
import io.github.potterplus.magicscan.rule.RuleType;
import io.github.potterplus.magicscan.rule.SpellRule;
import io.github.potterplus.magicscan.scan.Scan;
import io.github.potterplus.magicscan.scan.ScanController;
import io.github.potterplus.magicscan.task.RemoveInactiveScanTask;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.github.potterplus.api.string.StringUtilities.equalsAny;

/**
 * Handles the sub-command logic for /magicscan scan.
 */
@RequiredArgsConstructor
public class ScanSubCommand extends CommandBase.SubCommand {

    @NonNull
    private final MagicScanController controller;

    public MagicScanPlugin getPlugin() {
        return controller.getPlugin();
    }

    public List<String> getScanHelp() {
        return StringUtilities.color(
                "&d&l*** &b/mss command help &d&l***",
                "  &a[Optional Arg] &c<Required Arg> &3--Optional Flag",
                "  &8/&7mss create &3-u &8> &6Create a new scan",
                "    &3--ui&8: &7Auto open management GUI&8.",
                "  &8/&7mss clear &8> &6Forcibly clears queued scans",
                "  &8/&7mss delete &a[Target] &8> &6Deletes the current scan",
                "    &aTarget&8: &7Player name to delete scan for&8.",
                "  &8/&7mss describe &8> &6Describes the current scan",
                "  &8/&7mss execute &8> &6Executes the current scan",
                "  &8/&7mss rules &8> &6Edits the current scan's rules in a GUI",
                "  &8/&7mss list &3-u &8> &6Lists the queued scans",
                "    &3--ui&8: &7Display list in GUI&8.",
                "  &8/&7mss manage &8> &6Manage your current scan in a GUI",
                "  &8/&7mss override &c<Rule> &8> &6Toggles the rule",
                "    &cRule&8: &7A rule key&8.",
                "  &8/&7mss enable &c<Rule> &8> &6Enables the rule",
                "    &cRule&8: &7Either &eall &7or a rule key&8.",
                "  &8/&7mss disable &c<Rule> &8> &6Disables the rule",
                "    &cRule&8: &7Either &eall &7or a rule key&8.",
                "  &8/&7mss only &c<Rule> &8> &6Disables all rules and enables the provided",
                "    &cRule&8: &7The rule to enable&8.",
                "  &8/&7mss property &c<Property> &a[Bool] &8> &6Describe or set a property",
                "    &cProperty&8: &7Any of&8:",
                "      &elog&8, &evisual&8, &eallruletypes&8, &ehidden",
                "    &aBool&8: &7Any of&8:",
                "       &etrue&8, &efalse&8, &etoggle"
        );
    }

    public String createScanUsage(String usage) {
        return StringUtilities.color("&dMS&5> &cUsage&8: &4" + usage + "&8. &cDo &4/mss ? &cfor help&8.");
    }

    @Override
    public void execute(CommandContext context) {
        if (!context.hasPermission("magicscan.command.scan")) {
            controller.sendMessage(context, "no_permission");

            return;
        }

        String[] args = context.getArgs();

        if (args.length < 2) {
            context.sendMessage(createScanUsage("/mss <sub>"));

            return;
        }

        CommandSender sender = context.getSender();
        ScanController scans = controller.getScanController();
        String sub = context.getArg(1);
        CommandFlag uiFlag = new CommandFlag.UserInterfaceFlag();

        if (sub.equalsIgnoreCase("create")) {
            if (scans.hasScan(sender)) {
                if (context.isPlayer()) {
                    new OverwriteExistingScanConfirmPrompt(controller, context).activate(context.getPlayer());
                } else if (context.isConsole()) {
                    controller.sendMessage(context, "previous_scan_deleted");
                    scans.removeScan(sender);

                    context.performCommand("magicscan scan create");
                }
            } else {
                Scan scan = new Scan(controller, sender.getName());

                scan.getOptions().setRuleTypes(Collections.singletonList(RuleType.SPELL));

                scans.putScan(sender, scan);
                controller.sendMessage(context,"scan_queued");

                new RemoveInactiveScanTask(controller, sender).runTaskLater(getPlugin(), controller.getConfig().getInactiveScanTimeout());
            }

            if (context.hasFlag(uiFlag) && context.isPlayer()) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        context.performCommand("magicscan scan manage");
                    }
                }.runTaskLater(getPlugin(), 10);
            }
        } else if (equalsAny(sub, "clear")) {
            if (!context.hasPermission("magicscan.command.scan.clear")) {
                controller.sendMessage(context, "no_permission");

                return;
            }

            if (context.isPlayer()) {
                new ClearScansConfirmPrompt(controller, null)
                        .activate(context.getPlayer());
            } else if (context.isConsole()) {
                scans.clearScans(context.getConsole());
                controller.sendMessage(context, "scans_cleared");
            }
        } else if (equalsAny(sub, "delete", "del")) {
            if (!context.hasPermission("magicscan.command.scan.delete")) {
                controller.sendMessage(context,"no_permission");

                return;
            }

            // ms s delete [player]

            if (args.length < 3) {
                if (scans.hasScan(sender)) {
                    scans.removeScan(sender);
                    controller.sendMessage(context, "scan_deleted");
                } else {
                    controller.sendMessage(context, "no_queued_scan");
                }
            } else {
                Optional<Player> opt = context.resolveTarget(context.getArg(2));

                if (opt.isPresent()) {
                    Player target = opt.get();

                    if (scans.hasScan(target)) {
                        if (context.isPlayer()) {
                            new DeleteOtherScanConfirmPrompt(controller, target, sender).activate(context.getPlayer());
                        }

                        if (context.isConsole()) {
                            scans.removeScan(sender);
                            controller.sendMessage(sender, "scan_deleted_other", ImmutableMap.of("$name", target.getName()));
                            controller.sendMessage(target, "scan_cleared", ImmutableMap.of("$name", sender.getName()));
                        }
                    } else {
                        controller.sendMessage(context, "no_queued_scan_other");
                    }
                } else {
                    controller.sendMessage(context, "no_target", ImmutableMap.of("$name", context.getArg(2)));
                }
            }
        } else if (equalsAny(sub, "describe", "desc", "d", "info")) {
            Scan scan = scans.getQueuedScan(sender);

            if (scan == null) {
                controller.sendMessage(context, "no_queued_scan");
            } else {
                Utilities.describe(context, scan);

                context.sendMessage("");
                context.sendMessage("&e&l>> &7Perform &d/ms scan perform &7to initiate this scan.");
            }
        } else if (equalsAny(sub, "list", "l")) {
            if (context.hasFlag(uiFlag) && context.isPlayer()) {
                new ListScansGUI(context.getPlayer(), controller).activate();
            } else {
                if (scans.getQueuedScans().isEmpty()) {
                    controller.sendMessage(context, "no_queued_scan");
                } else {
                    for (Map.Entry<String, Scan> entry : scans.getQueuedScans().entrySet()) {
                        Scan scan = entry.getValue();

                        context.sendMessage(scan.describe(sender));
                    }
                }
            }
        } else if (equalsAny(sub, "execute", "perform", "start")) {
            Scan scan = scans.getQueuedScan(sender);

            if (scan == null) {
                controller.sendMessage(sender, "no_queued_scan");

                return;
            }

            if (scan.isMeetingConditions()) {
                if (context.isConsole()) {
                    scan.scan();
                } else if (context.isPlayer()) {
                    new ExecuteScanConfirmPrompt(scan, context.getPlayer())
                            .activate(context.getPlayer());
                }
            } else {
                controller.sendMessage(context, "not_meeting_conditions");
            }

            scans.removeScan(sender);
        } else if (equalsAny(sub,"manage", "configure", "edit")) {
            if (context.isPlayer()) {
                Player player = context.getPlayer();

                if (scans.hasScan(player)) {
                    UserInterface ui = new ManageScanGUI(controller, player);

                    ui.activate(player);
                } else {
                    controller.sendMessage(context, "no_queued_scan");
                }
            }

            if (context.isConsole()) {
                controller.sendMessage(context, "no_console");
            }
        } else if (equalsAny(sub, "property", "prop")) {
            // ms s prop <key> [bool]

            if (args.length < 3) {
                context.sendMessage(createScanUsage("/mss prop <key> [true|false]"));
            } else {
                if (!scans.hasScan(context.getSender())) {
                    controller.sendMessage(context, "no_queued_scan");

                    return;
                }

                Scan scan = scans.getQueuedScan(context.getSender());

                String key = args[2];
                Scan.Options.Property prop = Scan.Options.Property.getProperty(key);

                if (prop == null) {
                    controller.sendMessage(context, "invalid_property");

                    return;
                }

                if (args.length == 3) {
                    controller.sendMessage(context, "scan_property", ImmutableMap.of("$name", prop.name(), "$bool", BooleanFormatter.ENABLED_DISABLED.format(scan.getOptions().getProperty(prop))));
                } else {
                    String s = args[3];
                    String usage = createScanUsage("/mss prop <prop> [true|false|toggle]");

                    if (!equalsAny(s, "true", "false", "toggle")) {
                        context.sendMessage(usage);
                        return;
                    }

                    boolean b = FriendlyBooleans.getFriendlyBoolean(s);
                    boolean bool = s.equalsIgnoreCase("toggle")
                            ? !scan.getOptions().getProperty(prop)
                            : b;

                    scan.getOptions().setProperty(prop, bool);
                    scans.putScan(context.getSender(), scan);

                    controller.sendMessage(context, "scan_property_updated", ImmutableMap.of("$name", prop.name(), "$bool", BooleanFormatter.ENABLED_DISABLED.format(bool)));
                }
            }
        } else if (equalsAny(sub, "override")) {
            Scan scan = scans.getQueuedScan(sender);

            if (scan == null) {
                controller.sendMessage(context, "no_queued_scan");

                return;
            }

            if (args.length == 2) {
                context.sendMessage(createScanUsage("/mss override <rule>"));
            } else {
                String key = context.getArg(2);
                Optional<SpellRule> opt = controller.resolveSpellRule(key);

                if (opt.isPresent()) {
                    if (scan.getOptions().getRuleOverrides().contains(key)) {
                        scan.getOptions().getRuleOverrides().remove(key);

                        controller.sendMessage(context, "rule_toggled", ImmutableMap.of(
                                "$key", key,
                                "$bool", BooleanFormatter.ENABLED_DISABLED.format(true)
                        ));
                    } else {
                        scan.getOptions().getRuleOverrides().add(key);

                        controller.sendMessage(context, "rule_toggled", ImmutableMap.of(
                                "$key", key,
                                "$bool", BooleanFormatter.ENABLED_DISABLED.format(false)
                        ));
                    }
                } else {
                    controller.sendMessage(context, "invalid_rule", ImmutableMap.of("$key", key));
                }
            }
        } else if (equalsAny(sub, "yes", "enable")) {
            Scan scan = scans.getQueuedScan(sender);

            if (scan == null) {
                controller.sendMessage(context, "no_queued_scan");

                return;
            }

            if (args.length == 2) {
                context.sendMessage(createScanUsage("/mss enable <rule>"));
            } else {
                String key = context.getArg(2);
                Map<String, String> replace = ImmutableMap.of("$key", key, "$bool", BooleanFormatter.ENABLED_DISABLED.format(true));

                if (key.equalsIgnoreCase("all")) {
                    for (SpellRule rule : controller.getSpellRules()) {
                        context.performCommand("magicscan scan enable " + rule.getKey());
                    }
                } else {
                    Optional<SpellRule> opt = controller.resolveSpellRule(key);

                    if (opt.isPresent()) {
                        scan.getOptions().getRuleOverrides().remove(key);

                        controller.sendMessage(context, "rule_toggled", replace);
                    } else {
                        controller.sendMessage(context, "invalid_rule", replace);
                    }
                }
            }
        } else if (equalsAny(sub, "no", "disable")) {
            Scan scan = scans.getQueuedScan(sender);

            if (scan == null) {
                controller.sendMessage(context, "no_queued_scan");

                return;
            }

            if (args.length == 2) {
                context.sendMessage(createScanUsage("/mss disable <rule>"));
            } else {
                String key = context.getArg(2);
                Map<String, String> replace = ImmutableMap.of("$key", key, "$bool", BooleanFormatter.ENABLED_DISABLED.format(false));

                if (key.equalsIgnoreCase("all")) {
                    for (SpellRule rule : controller.getSpellRules()) {
                        context.performCommand("magicscan scan disable " + rule.getKey());
                    }
                } else {
                    Optional<SpellRule> opt = controller.resolveSpellRule(key);

                    if (opt.isPresent()) {
                        scan.getOptions().getRuleOverrides().add(key);

                        controller.sendMessage(context, "rule_toggled", replace);
                    } else {
                        controller.sendMessage(context, "invalid_rule", replace);
                    }
                }
            }
        } else if (equalsAny(sub, "only")) {
            Scan scan = scans.getQueuedScan(sender);

            if (scan == null) {
                controller.sendMessage(context, "no_queued_scan");

                return;
            }

            if (args.length == 2) {
                context.sendMessage(createScanUsage("/mss only <rule>"));
            } else {
                String key = context.getArg(2);
                Map<String, String> replace = ImmutableMap.of("$key", key, "$bool", BooleanFormatter.ENABLED_DISABLED.format(false));

                Optional<SpellRule> opt = controller.resolveSpellRule(key);

                if (opt.isPresent()) {
                    for (SpellRule rule : controller.getSpellRules()) {
                        context.performCommand("magicscan scan disable " + rule.getKey());
                    }

                    context.performCommand("magicscan scan enable " + key);
                } else {
                    controller.sendMessage(context, "invalid_rule", replace);
                }
            }
        } else if (equalsAny(sub, "rules", "editrules")) {
            if (context.isPlayer()) {
                Player player = context.getPlayer();

                if (scans.hasScan(player)) {
                    new RuleListEditGUI(controller, null).activate(player);
                } else {
                    controller.sendMessage(context, "no_queued_scan");
                }

            } else if (context.isConsole()) {
                ConsoleCommandSender console = context.getConsole();

                if (scans.hasScan(console)) {
                    Scan scan = scans.getQueuedScan(console);

                    context.sendMessage(createScanUsage("/mss override <rule>"));

                    for (SpellRule rule : controller.getSpellRules()) {
                        context.sendMessage("&8- &7" + rule.getKey() + " &r" + BooleanFormatter.ENABLED_DISABLED.format(!scan.isOverridden(rule.getKey())));
                    }
                } else {
                    controller.sendMessage(context, "no_queued_scan");
                }
            }
        } else if (equalsAny(sub, "help", "?")) {
            context.sendMessage(getScanHelp());
        } else {
            context.sendMessage("&dMS&5> &cUnknown sub-command &4'" + sub + "'&c. Do &4/mss ? &cfor help.");
        }
    }
}
