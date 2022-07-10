package io.github.potterplus.magicscan.command.sub;

import io.github.potterplus.api.command.CommandBase;
import io.github.potterplus.api.command.CommandContext;
import io.github.potterplus.api.string.HoverMessage;
import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.MagicScanPlugin;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static io.github.potterplus.api.string.StringUtilities.equalsAny;
import static io.github.potterplus.api.string.StringUtilities.replace;

/**
 * TODO Write docs
 */
@RequiredArgsConstructor
public class MetricsSubCommand extends CommandBase.SubCommand {

    @NonNull
    private final MagicScanController controller;

    public MagicScanPlugin getPlugin() {
        return controller.getPlugin();
    }

    private void doOverallMetrics(CommandContext context) {
        CommandSender s = context.getSender();

        context.sendMessage("&8&m----------------------------------------");
        context.sendMessage(" &bMagicScan Metrics");
        context.sendMessage(" &7Some numbers about your Magic setup");
        context.sendMessage("&8&m----------------------------------------");

        final Map<String, String> replace = new HashMap<>();

        replace.put("%spells_tracking%", String.valueOf(controller.getSpells().size()));
        replace.put("%spells_all%", String.valueOf(controller.getAllSpells().size()));
        replace.put("%spell_cats%", String.valueOf(controller.getSpellCategories().size()));
        replace.put("%spell_actions%", String.valueOf(controller.getActions().size()));
        replace.put("%paths%", String.valueOf(controller.getPaths().size()));
        replace.put("%mobs%", String.valueOf(controller.getMobs().size()));
        replace.put("%wands%", String.valueOf(controller.getWands().size()));

        HoverMessage spellsHm = HoverMessage
                .compose(" &dSpells")
                .hoverText(replace(Arrays.asList(
                        " &8> &bMS &7is tracking &e%spells_tracking% &7out of &e%spells_all% &7spells.",
                        " &8> &e%spell_cats% &7spell categories.",
                        " &8> &e%spell_actions% &7spell actions."
                ), replace));
        HoverMessage pathsHm = HoverMessage
                .compose(" &dPaths")
                .withHover(replace(" &8> &7There are &e%paths% &7paths.", replace));
        HoverMessage mobsHm = HoverMessage
                .compose(" &dMobs")
                .withHover(replace(" &8> &7There are &e%mobs% &7mobs.", replace));
        HoverMessage wandsHm = HoverMessage
                .compose(" &dWands")
                .withHover(replace(" &8> &7There are &e%wands% &7wands.", replace));

        spellsHm.send(s);
        pathsHm.send(s);
        mobsHm.send(s);
        wandsHm.send(s);
    }

    @Override
    public void execute(CommandContext context) {
        if (context.getArgs().length == 1) {
            doOverallMetrics(context);
        } else {
            String sub = context.getSub();

            if (equalsAny(sub, "spells")) {
                // TODO Do the rest of this subcommand
            }
        }
    }
}
