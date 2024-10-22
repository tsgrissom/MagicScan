package io.github.potterplus.magicscan.scan;

import com.besaba.revonline.pastebinapi.Pastebin;
import com.besaba.revonline.pastebinapi.impl.factory.PastebinFactory;
import com.besaba.revonline.pastebinapi.paste.Paste;
import com.besaba.revonline.pastebinapi.paste.PasteBuilder;
import com.besaba.revonline.pastebinapi.paste.PasteVisiblity;
import com.besaba.revonline.pastebinapi.response.Response;
import com.google.common.collect.ImmutableMap;
import io.github.potterplus.api.item.Icon;
import io.github.potterplus.api.misc.BooleanFormatter;
import io.github.potterplus.api.misc.PluginLogger;
import io.github.potterplus.api.string.StringUtilities;
import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.MagicScanPlugin;
import io.github.potterplus.magicscan.gui.ScanResultsGUI;
import io.github.potterplus.magicscan.magic.MagicSpell;
import io.github.potterplus.magicscan.magic.MagicType;
import io.github.potterplus.magicscan.misc.Describable;
import io.github.potterplus.magicscan.misc.Utilities;
import io.github.potterplus.magicscan.rule.RuleType;
import io.github.potterplus.magicscan.rule.SpellRule;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static io.github.potterplus.api.string.StringUtilities.equalsAny;

/**
 * Represents a scan initiated by a CommandSender, all of its settings, and its results.
 */
@SerializableAs("Scan")
public class Scan implements ConfigurationSerializable, Describable {

    public static class Options {

        public enum Property {
            SCAN_ALL_RULE_TYPES,
            SCAN_HIDDEN,
            LOG_TO_FILE,
            VISUAL;

            public String getShortName() {
                return this.name().replace("_", "").toLowerCase();
            }

            public static List<String> getValidNames() {
                List<String> l = new ArrayList<>();

                for (Property p : Property.values()) {
                    l.add(p.name());
                    l.add(p.getShortName());
                    l.add(p.toString());
                }

                return l;
            }

            public static Property getProperty(String name) {
                for (Property p : Property.values()) {
                    String nameBase = p.name();
                    String nameReplaced = p.getShortName();
                    String toStr = p.toString();

                    if (equalsAny(name, nameBase, nameReplaced, toStr)) {
                        return p;
                    }
                }

                return null;
            }
        }

        @Getter @Setter
        private boolean scanAllRuleTypes, scanHidden, logToFile, visual;

        @Setter
        private List<RuleType> ruleTypes;

        @Getter @Setter
        private List<String> ruleOverrides;

        public void setProperty(Property prop, boolean bool) {
            switch (prop) {
                case SCAN_ALL_RULE_TYPES:
                    this.scanAllRuleTypes = bool;
                    break;
                case SCAN_HIDDEN:
                    this.scanHidden = bool;
                    break;
                case LOG_TO_FILE:
                    this.logToFile = bool;
                    break;
                case VISUAL:
                    this.visual = bool;
                    break;
            }
        }

        public void toggleProperty(Property prop) {
            this.setProperty(prop, !getProperty(prop));
        }

        public boolean getProperty(Property prop) {
            switch (prop) {
                case SCAN_ALL_RULE_TYPES: return this.scanAllRuleTypes;
                case SCAN_HIDDEN: return this.scanHidden;
                case LOG_TO_FILE: return this.logToFile;
                case VISUAL: return this.visual;
                default: return true;
            }
        }

        public List<RuleType> getRuleTypes() {
            if (isScanAllRuleTypes()) return Arrays.asList(RuleType.values());
            else return ruleTypes;
        }

        protected Options(MagicScanController controller) {
            this.scanAllRuleTypes = false;
            this.scanHidden = controller.getConfig().shouldLoadHidden();
            this.logToFile = false;
            this.visual = false;
            this.ruleTypes = new ArrayList<>();

            this.ruleTypes.add(RuleType.SPELL);

            this.ruleOverrides = new ArrayList<>();
        }
    }

    public static class Results {

        @Getter @Setter(AccessLevel.PROTECTED)
        private SortedMap<MagicSpell, List<Violation>> spellViolations;

        @Getter @Setter
        private List<String> finalList;

        @Getter @Setter(AccessLevel.PROTECTED)
        private int invalidSpellCount, validSpellCount, violationsCount;

        @Getter @Setter(AccessLevel.PROTECTED)
        private double elapsedTime;

        @Getter @Setter
        private String pastebinURL;

        public Results() {
            this.spellViolations = new TreeMap<>(new MagicSpell.Comparator());
            this.invalidSpellCount = 0;
            this.validSpellCount = 0;
            this.violationsCount = 0;
            this.elapsedTime = 0.0D;
            this.pastebinURL = null;
        }

        public boolean exists() {
            return !spellViolations.isEmpty();
        }

        protected void addSpellViolations(MagicSpell spell, List<Violation> violations) {
            if (spellViolations.containsKey(spell)) {
                List<Violation> coll = spellViolations.get(spell);

                spellViolations.remove(spell);

                coll.addAll(violations);

                spellViolations.put(spell, coll);
            } else {
                spellViolations.put(spell, violations);
            }
        }
    }

    @Getter @NonNull
    private transient MagicScanController controller;

    @Getter
    private long createdAtUnix;

    @Getter @NonNull
    private String senderName;

    @Getter @NonNull
    private Options options;

    @Getter
    private Results results;

    public Scan(@NonNull MagicScanController controller, String senderName) {
        this.controller = controller;
        this.createdAtUnix = System.currentTimeMillis();
        this.senderName = senderName;
        this.options = new Options(controller);
        this.results = new Results();
    }

    public CommandSender getSender() {
        return Utilities.resolveCommandSenderByName(getSenderName());
    }

    public Date getCreatedAt() {
        return new Date(this.getCreatedAtUnix());
    }

    private boolean isEveryRuleOverridden() {
        boolean bool = true;

        for (SpellRule rule : getController().getSpellRules()) {
            if (!isOverridden(rule.getKey())) {
                bool = false;
            }
        }

        return bool;
    }

    public boolean isMeetingConditions() {
        return !getOptions().getRuleTypes().isEmpty() && !isEveryRuleOverridden();
    }

    public boolean isOverridden(String ruleKey) {
        return getOptions().getRuleOverrides().contains(ruleKey);
    }

    public void scan() {
        if (senderName.equals(Bukkit.getConsoleSender().getName())) {
            scan(Bukkit.getConsoleSender());
        } else {
            Player player = Bukkit.getPlayer(senderName);

            if (player != null) {
                scan(player);
            }
        }
    }

    public void scan(@NonNull CommandSender sender) {
        if (this.getOptions().isVisual()) {
            if (sender instanceof Player) {
                this.visualScan(((Player) sender));
            } else {
                controller.sendMessage(sender, "no_gui_console");
            }
        } else {
            this.textScan(sender);
        }
    }

    public void run() {
        Instant start = Instant.now();

        for (RuleType ruleType : RuleType.values()) {
            if (this.getOptions().getRuleTypes().contains(ruleType) || this.getOptions().isScanAllRuleTypes()) {
                if (ruleType.equals(RuleType.SPELL)) {
                    int invalidCount = 0, validCount = 0, violationsCount = 0;
                    List<MagicSpell> spells = controller.getSpells();
                    List<MagicSpell> newSpells;

                    if (this.getOptions().isScanHidden()) {
                        newSpells = new ArrayList<>(spells);
                    } else {
                        newSpells = new ArrayList<>();

                        for (MagicSpell spell : spells) {
                            if (spell.isHidden()) continue;

                            newSpells.add(spell);
                        }
                    }

                    Collections.sort(newSpells);

                    for (MagicSpell spell : newSpells) {
                        for (SpellRule rule : controller.getSpellRules()) {
                            if (!rule.shouldValidateRule() || this.isOverridden(rule.getKey())) continue;

                            Collection<Violation> coll = rule.validate(spell);

                            if (coll != null && !coll.isEmpty()) {
                                getResults().addSpellViolations(spell, new ArrayList<>(coll));
                            }
                        }

                        List<Violation> violations = getResults().getSpellViolations().get(spell);

                        if (violations != null && violations.size() > 0) {
                            invalidCount++;
                            violationsCount += getResults().getSpellViolations().get(spell).size();
                        } else {
                            validCount++;
                        }

                        getResults().setInvalidSpellCount(invalidCount);
                        getResults().setValidSpellCount(validCount);
                        getResults().setViolationsCount(violationsCount);
                    }
                }
            }
        }

        Instant finish = Instant.now();

        getResults().setElapsedTime(Duration.between(start, finish).toMillis() / 1000D);

        if (controller.getConfig().isUsingPastebinIntegration() && getResults().getPastebinURL() == null) {
            this.saveAsPaste();
        }
    }

    private void sendEntries(CommandSender sender, Map<? extends MagicType<?>, List<Violation>> map) {
        if (map == null || map.isEmpty()) {
            return;
        }

        MagicScanPlugin plugin = controller.getPlugin();
        int step = 0;
        int interval = controller.getConfig().getInterval();

        for (Map.Entry<? extends MagicType<?>, List<Violation>> entry : map.entrySet()) {
            MagicType<?> spell = entry.getKey();
            List<Violation> violations = entry.getValue();

            new BukkitRunnable() {
                @Override
                public void run() {
                    controller.sendMessage(sender, "spells_header", ImmutableMap.of(
                            "$spell", spell.getKey(),
                            "$violations", String.valueOf(violations.size())
                    ));

                    for (Violation violation : violations) {
                        sender.sendMessage(StringUtilities.color(violation.toString()));
                    }
                }
            }.runTaskLater(plugin, (long) step * interval);

            step++;
        }

        int endTicks = step * interval;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (getResults().getInvalidSpellCount() > 0) {
                    controller.sendMessage(sender, "spells_found_issues", ImmutableMap.of(
                            "$violations", String.valueOf(getResults().getViolationsCount()),
                            "$invalid", String.valueOf(getResults().getInvalidSpellCount())
                    ));
                } else {
                    controller.sendMessage(sender, "spells_no_issues");
                }

                controller.sendMessage(sender, "spells_passed", ImmutableMap.of(
                        "$valid", String.valueOf(getResults().getValidSpellCount())
                ));

                controller.sendMessage(sender, "scan_time", ImmutableMap.of(
                        "$time", String.valueOf(getResults().getElapsedTime())
                ));

                if (controller.getConfig().isUsingPastebinIntegration() && getResults().getPastebinURL() != null) {
                    controller.sendMessage(getSender(), "pastebin_posted", ImmutableMap.of("$url", getResults().getPastebinURL()));
                }
            }
        }.runTaskLater(plugin, endTicks + interval);
    }

    public void textScan(@NonNull CommandSender sender) {
        if (!getResults().exists()) {
            run();
        }

        if (getOptions().getRuleTypes().contains(RuleType.SPELL) || getOptions().isScanAllRuleTypes()) {
            sendEntries(sender, getResults().getSpellViolations());
        }
    }

    public void visualScan(Player player) {
        if (!getResults().exists()) {
            run();
        }

        new ScanResultsGUI(controller, this).activate(player);
    }

    public Response<String> saveAsPaste() {
        PastebinFactory factory = controller.getPastebinFactory();
        Pastebin pastebin = controller.getPastebin();

        if (!controller.getConfig().isUsingPastebinIntegration()) {
            PluginLogger.atWarn()
                    .with("The Pastebin integration isn't enabled.")
                    .print();

            return null;
        }

        if (factory == null || pastebin == null) {
            PluginLogger.atWarn()
                    .with("Couldn't create PasteBuilder from Scan. Something went wrong with the Pastebin integration!")
                    .print();

            return null;
        }

        PasteBuilder builder = factory.createPaste()
                .setTitle("Scan Results " + controller.getConfig().getDateFormat().format(getCreatedAt()))
                .setRaw("Test")
                .setUserFriendlyLanguage("text")
                .setVisiblity(PasteVisiblity.Public);

        Paste paste = builder.build();
        Response<String> response = pastebin.post(paste);

        if (response.hasError()) {
            Bukkit.getLogger().severe(response.getError());
        }

        if (!response.hasError()) {
            getResults().setPastebinURL(response.get());
        }

        return response;
    }

    private String getLineBrokenString(List<String> list) {
        StringBuilder builder = new StringBuilder();

        for (String s : list) {
            builder.append(s)
                    .append("\n");
        }

        return builder.toString();
    }

    public String getPastebinFriendlyText() {
        List<String> list = new ArrayList<>();

        final String LINE = "===============";
        final String THIN_LINE = "---------------";

        list.add(LINE);
        list.add(" Scan Description");
        list.add(LINE);
        list.addAll(this.describe(getSender()));
        list.add(LINE);
        list.add(" Scan Results");
        list.add(LINE);

        if (!getResults().getSpellViolations().isEmpty()) {
            list.add(THIN_LINE);
            list.add(" Spell Violations");
            list.add(THIN_LINE);

            for (Map.Entry<MagicSpell, List<Violation>> entry : getResults().getSpellViolations().entrySet()) {
                MagicSpell spell = entry.getKey();
                List<Violation> violations = entry.getValue();

                list.add("  Spell: " + spell.getKey());

                for (Violation violation : violations) {
                    list.add("    " + ChatColor.stripColor(StringUtilities.color(violation.toString())));
                }
            }
        }

        return getLineBrokenString(list);
    }

    @SuppressWarnings("unchecked")
    public Scan(Map<String, Object> map) {
        this.options = new Options(MagicScanController.getInstance());
        this.results = new Results();

        if (Objects.nonNull(map.get("created_at"))) {
            this.createdAtUnix = (long) map.get("created_at");
        }

        if (Objects.nonNull(map.get("scan_all_rule_types"))) {
            this.getOptions().setScanAllRuleTypes((boolean) map.get("scan_all_rule_types"));
        }

        if (Objects.nonNull(map.get("scan_hidden"))) {
            this.getOptions().setScanHidden((boolean) map.get("scan_hidden"));
        }

        if (Objects.nonNull(map.get("log_to_file"))) {
            this.getOptions().setLogToFile((boolean) map.get("log_to_file"));
        }

        if (Objects.nonNull(map.get("visual"))) {
            this.getOptions().setVisual((boolean) map.get("visual"));
        }

        if (Objects.nonNull(map.get("rule_types"))) {
            List<RuleType> list = new ArrayList<>();

            for (String str : (List<String>) map.get("rule_types")) {
                list.add(RuleType.resolve(str));
            }

            this.getOptions().setRuleTypes(list);
        }

        if (Objects.nonNull(map.get("rule_overrides"))) {
            this.getOptions().setRuleOverrides((List<String>) map.get("rule_overrides"));
        }

        if (Objects.nonNull(map.get("sender"))) {
            this.senderName = (String) map.get("sender");
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("created_at", createdAtUnix);
        map.put("scan_all_rule_types", getOptions().isScanAllRuleTypes());
        map.put("scan_hidden", getOptions().isScanHidden());
        map.put("log_to_file", getOptions().isLogToFile());
        map.put("visual", getOptions().isVisual());
        map.put("rule_types", getOptions().getRuleTypes());
        map.put("rule_overrides", getOptions().getRuleOverrides());
        map.put("sender", senderName);

        return map;
    }

    @Override
    public List<String> describe(CommandSender sender) {
        List<String> list = new ArrayList<>();

        list.add("&8- &7Created by&8: &e" + getSender().getName());
        list.add("  &7Created at&8: &e" + controller.getConfig().getDateFormat().format(getCreatedAt()));
        list.add("  &7Will be logged&8: &e" + BooleanFormatter.format(getOptions().isLogToFile(), BooleanFormatter.YES_NO));
        list.add("  &7Scan all rule types&8: &e" + BooleanFormatter.format(getOptions().isScanAllRuleTypes(), BooleanFormatter.YES_NO));
        list.add("  &7Scan hidden things&8: &e" + BooleanFormatter.format(getOptions().isScanHidden(), BooleanFormatter.YES_NO));
        list.add("  &7Visual&8: &e" + BooleanFormatter.format(getOptions().isVisual(), BooleanFormatter.YES_NO));
        list.add("  &7Rule types&8: &e" + getOptions().getRuleTypes().toString());

        ConfigurationSection typesSection = getController().getRulesFile().getFileConfiguration();

        if (typesSection != null) {
            for (String key : typesSection.getKeys(false)) {
                list.add("  &7Rules &8(&e" + key + "&8)");

                ConfigurationSection rulesSection = typesSection.getConfigurationSection(key + ".rules");

                if (rulesSection != null) {
                    for (String rulesKey : rulesSection.getKeys(false)) {
                        ConfigurationSection section = rulesSection.getConfigurationSection(rulesKey);

                        if (section != null) {
                            boolean bool = section.getBoolean("enabled", true);

                            if (isOverridden(rulesKey)) {
                                bool = !bool;
                            }

                            list.add("    &8- &7" + rulesKey + "&8: " + BooleanFormatter.format(bool, BooleanFormatter.ENABLED_DISABLED));
                        }
                    }
                }
            }
        }

        return StringUtilities.color(list);
    }

    @Override
    public ItemStack describeAsItem(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return null;
        }

        Player player = (Player) sender;
        String name = "&e" + this.getSenderName() + "'s &7Scan";
        List<String> lore = this.describe(sender);

        lore = lore.subList(1, lore.size());

        for (int i = 0; i < lore.size(); i++) {
            String str = lore.get(i);

            lore.set(i, str.substring(2));
        }

        return Icon
                .skull(player)
                .name(name)
                .lore(lore)
                .build();

    }
}
