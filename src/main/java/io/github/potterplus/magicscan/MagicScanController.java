package io.github.potterplus.magicscan;

import com.besaba.revonline.pastebinapi.Pastebin;
import com.besaba.revonline.pastebinapi.impl.factory.PastebinFactory;
import com.elmakers.mine.bukkit.api.magic.MageController;
import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import com.elmakers.mine.bukkit.api.spell.SpellTemplate;
import com.elmakers.mine.bukkit.api.wand.WandTemplate;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.potterplus.api.command.CommandContext;
import io.github.potterplus.api.misc.PluginLogger;
import io.github.potterplus.api.storage.flatfile.MessagesFile;
import io.github.potterplus.api.string.StringUtilities;
import io.github.potterplus.magicscan.file.ConfigFile;
import io.github.potterplus.magicscan.file.MagicDefFile;
import io.github.potterplus.magicscan.file.RulesFile;
import io.github.potterplus.magicscan.magic.*;
import io.github.potterplus.magicscan.magic.spell.SpellAction;
import io.github.potterplus.magicscan.magic.spell.SpellCategory;
import io.github.potterplus.magicscan.rule.SpellRule;
import io.github.potterplus.magicscan.rule.spell.*;
import io.github.potterplus.magicscan.scan.Scan;
import io.github.potterplus.magicscan.scan.ScanController;
import io.github.potterplus.magicscan.task.QuickScanTask;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The primary controller for the entire plugin.
 */
public class MagicScanController {

    private static MagicScanController INSTANCE;
    public static final String META_URL;

    static {
        META_URL = "https://raw.githubusercontent.com/elBukkit/MagicMeta/master/src/web/common/meta.json";
    }

    public static MagicScanController getInstance() {
        return INSTANCE;
    }

    @Getter @NonNull
    private final MagicScanPlugin plugin;

    @Getter
    private final ConfigFile config;

    @Getter
    private final MessagesFile<MagicScanPlugin> messages;

    @Getter
    private final RulesFile rulesFile;

    @Getter @NonNull
    private MagicAPI magicAPI;

    @Getter
    private MagicDefFile attributesDefaults, automataDefaults, classesDefaults, configDefaults, craftingDefaults, effectsDefaults, itemsDefaults, materialsDefaults, messagesDefaults, mobsDefaults, modifiersDefaults, pathsDefaults, spellsDefaults, wandsDefaults;

    @Getter
    private List<MagicPath> paths;

    public List<MagicPath> getFilteredPaths() {
        List<String> filters = getConfig().getListFilters("paths");
        List<MagicPath> newPaths = new ArrayList<>(getPaths());

        for (MagicPath path : getPaths()) {
            if (filters.contains(path.getKey())) {
                newPaths.remove(path);
            }
        }

        return newPaths;
    }

    @Getter
    private List<SpellCategory> spellCategories;

    @Getter
    private List<MagicSpell> spells;

    public List<MagicSpell> getFilteredSpells() {
        List<String> filters = getConfig().getListFilters("spells");
        List<MagicSpell> newSpells = new ArrayList<>(getSpells());

        for (MagicSpell spell : getSpells()) {
            if (filters.contains(spell.getKey())) {
                newSpells.remove(spell);
            }
        }

        return newSpells;
    }

    @Getter
    private List<MagicWand> wands;

    public List<MagicWand> getFilteredWands() {
        List<String> filters = getConfig().getListFilters("wands");
        List<MagicWand> newWands = new ArrayList<>(getWands());

        for (MagicWand wand : getWands()) {
            if (filters.contains(wand.getKey())) {
                newWands.remove(wand);
            }
        }

        return newWands;
    }

    @Getter
    private List<MagicMob> mobs;

    public List<MagicMob> getFilteredMobs() {
        List<String> filters = getConfig().getListFilters("mobs");
        List<MagicMob> newMobs = new ArrayList<>(getMobs());

        for (MagicMob mob : getMobs()) {
            if (filters.contains(mob.getKey())) {
                newMobs.remove(mob);
            }
        }

        return newMobs;
    }

    @Getter
    private List<SpellRule> spellRules;

    @Getter
    private List<SpellAction> actions;

    @Getter
    private List<String> defaultSpellParameters;

    @Getter
    private final ScanController scanController;

    @Getter
    private PastebinFactory pastebinFactory;

    @Getter
    private Pastebin pastebin;

    public URL getMetaURL() {
        try {
            return new URL(META_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public MageController getMageController() {
        return getMagicAPI().getController();
    }

    protected MagicScanController(MagicScanPlugin plugin) {
        INSTANCE = this;

        this.plugin = plugin;

        Plugin magicPlugin = getPlugin().getServer().getPluginManager().getPlugin("Magic");

        if (magicPlugin instanceof MagicAPI) {
            this.magicAPI = (MagicAPI) magicPlugin;

            PluginLogger.atInfo("[MagicScan] Successfully integrated with Magic API.");
        } else {
            PluginLogger.atSevere("[MagicScan] Failed to integrate with Magic API! MagicScan has been disabled! Please install Magic to use MagicScan.");
            getPlugin().getServer().getPluginManager().disablePlugin(plugin);
        }

        this.config = new ConfigFile(this);
        this.messages = new MessagesFile<>(getPlugin());
        this.rulesFile = new RulesFile(this);

        plugin.getConfig().options().copyDefaults(true);
        plugin.saveDefaultConfig();

        messages.getFileConfiguration().options().copyDefaults(true);
        messages.saveDefault();

        rulesFile.getFileConfiguration().options().copyDefaults(true);
        messages.saveDefault();

        this.scanController = new ScanController(this);

        this.refresh();
    }

    public void refresh() {
        getMessages().saveDefault();
        getRulesFile().saveDefault();

        this.attributesDefaults = new MagicDefFile(this, "attributes.defaults.yml");
        this.automataDefaults = new MagicDefFile(this, "automata.defaults.yml");
        this.classesDefaults = new MagicDefFile(this, "classes.defaults.yml");
        this.configDefaults = new MagicDefFile(this, "config.defaults.yml");
        this.craftingDefaults = new MagicDefFile(this, "crafting.defaults.yml");
        this.effectsDefaults = new MagicDefFile(this, "effects.defaults.yml");
        this.itemsDefaults = new MagicDefFile(this, "items.defaults.yml");
        this.materialsDefaults = new MagicDefFile(this, "materials.defaults.yml");
        this.messagesDefaults = new MagicDefFile(this, "messages.defaults.yml");
        this.mobsDefaults = new MagicDefFile(this, "mobs.defaults.yml");
        this.modifiersDefaults = new MagicDefFile(this, "modifiers.defaults.yml");
        this.pathsDefaults = new MagicDefFile(this, "paths.defaults.yml");
        this.spellsDefaults = new MagicDefFile(this, "spells.defaults.yml");
        this.wandsDefaults = new MagicDefFile(this, "wands.defaults.yml");

        this.refreshSpellRules();
        this.refreshActions();
        this.refreshSpells();
        this.refreshWands();
        this.refreshPaths();
        this.refreshMobs();

        if (config.getFileConfiguration().getBoolean("pastebin_integration", false)) {
            this.pastebinFactory = new PastebinFactory();

            String devKey = config.getPastebinKey();

            if (devKey == null) {
                PluginLogger.atWarn("[MagicScan] Failed to set up Pastebin integration with invalid dev key!");
            } else {
                this.pastebin = pastebinFactory.createPastebin(devKey);

                PluginLogger.atInfo("[MagicScan] Pastebin integration successfully set up.");
            }

            this.pastebin = pastebinFactory.createPastebin(config.getPastebinKey());
        } else {
            if (config.isVerbose()) {
                PluginLogger.atInfo("[MagicScan] Pastebin integration is disabled.");
            }
        }

        CommandSender console = Bukkit.getConsoleSender();

        scanController.clearScans(console);

        if (config.shouldCreateScanOnStart()) {
            if (config.shouldPerformScanOnStart()) {
                new QuickScanTask(this, console).runTaskLater(getPlugin(), 40);
            } else {
                MagicScanController controller = this;

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Scan scan = new Scan(controller, console.getName());

                        scanController.putScan(console, scan);

                        sendMessage(console, "scan_auto_queued");
                    }
                }.runTaskLater(getPlugin(), 40);
            }
        }
    }

    protected void refreshSpellRules() {
        this.spellRules = Arrays.asList(
                new SpellActionsRule(this),
                new SpellCategoryRule(this),
                new SpellCooldownRule(this),
                new SpellDescriptionRule(this),
                new SpellIconRule(this),
                new SpellIconDisabledRule(this),
                new SpellKeyRule(this),
                new SpellLevelsRule(this),
                new SpellManaMatchPathRule(this),
                new SpellManaRule(this),
                new SpellNameRule(this),
                new SpellPathRule(this),
                new SpellUpgradeDescriptionRule(this)
        );

        if (getConfig().isVerbose()) {
            PluginLogger.atInfo()
                    .with("[MagicScan] Loaded %d spell rules", this.spellRules.size())
                    .print();
        }
    }

    protected void refreshActions() {
        if (actions == null) {
            actions = new ArrayList<>();
        }

        if (defaultSpellParameters == null) {
            defaultSpellParameters = new ArrayList<>();
        }

        actions.clear();
        defaultSpellParameters.clear();

        try {
            URL metaUrl = this.getMetaURL();
            ObjectMapper mapper = new ObjectMapper();
            JsonFactory factory = new JsonFactory();
            JsonParser parser1 = factory.createParser(metaUrl);

            parser1.setCodec(mapper);
            parser1.nextToken();

            JsonNode actionsNode = parser1.readValueAsTree();

            for (Iterator<JsonNode> i = actionsNode.path("classed").path("actions").elements(); i.hasNext();) {
                JsonNode n = i.next();
                SpellAction action = mapper.treeToValue(n, SpellAction.class);

                actions.add(action);
            }

            JsonParser parser2 = factory.createParser(metaUrl);

            parser2.setCodec(mapper);
            parser2.nextToken();

            JsonNode spellParametersNode = parser2.readValueAsTree();
            JsonNode n = spellParametersNode.path("types").path("spell_parameters").path("parameters");

            Iterator<Map.Entry<String, JsonNode>> fields = n.fields();

            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String key = entry.getKey();

                defaultSpellParameters.add(key);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (getConfig().isVerbose()) {
            PluginLogger.atInfo()
                    .with("[MagicScan] Loaded %d actions from Magic's meta.json", this.actions.size())
                    .print();
            PluginLogger.atInfo()
                    .with("[MagicScan] Recognized %d default spell parameters", this.defaultSpellParameters.size())
                    .print();
        }
    }

    protected void refreshSpells() {
        if (spellCategories == null) {
            spellCategories = new ArrayList<>();
        }

        spellCategories.clear();

        for (com.elmakers.mine.bukkit.api.spell.SpellCategory cat : getMageController().getCategories()) {
            spellCategories.add(new SpellCategory(this, cat));
        }

        Collections.sort(spellCategories);

        if (spells == null) {
            spells = new ArrayList<>();
        }

        spells.clear();

        for (SpellTemplate spell : getIncludedSpellTemplates()) {
            spells.add(new MagicSpell(this, spell));
        }

        Collections.sort(spells);

        if (getConfig().isVerbose()) {
            PluginLogger.atInfo()
                    .with("[MagicScan] Refreshed %d SpellSnapshots", this.spells.size())
                    .print();
        }
    }

    protected void refreshPaths() {
        if (paths == null) {
            paths = new ArrayList<>();
        }

        paths.clear();

        int ignoredCount = 0;

        for (String key : getMageController().getWandPathKeys()) {
            MagicPath snapshot = new MagicPath(this, key);

            if (snapshot.getTemplate() == null || snapshot.getAllSpells().size() == 0) {
                ignoredCount++;

                continue;
            }

            paths.add(snapshot);
        }

        Collections.sort(paths);

        if (getConfig().isVerbose()) {
            PluginLogger.atInfo()
                    .with("[MagicScan] Refreshed %d PathSnapshots while ignoring %d paths", this.paths.size(), ignoredCount)
                    .print();
        }
    }

    protected void refreshWands() {
        if (wands == null) {
            wands = new ArrayList<>();
        }

        wands.clear();

        for (WandTemplate wand : getMageController().getWandTemplates()) {
            wands.add(new MagicWand(this, wand.getKey()));
        }

        if (getConfig().isVerbose()) {
            PluginLogger.atInfo()
                    .with("[MagicScan] Refreshed %d WandSnapshots", this.wands.size())
                    .print();
        }
    }

    protected void refreshMobs() {
        if (mobs == null) {
            mobs = new ArrayList<>();
        }

        mobs.clear();

        MageController mc = getMageController();

        for (String key : mc.getMobKeys()) {
            mobs.add(new MagicMob(this, key));
        }

        if (getConfig().isVerbose()) {
            PluginLogger.atInfo()
                    .with("[MagicScan] Refreshed %d MobSnapshots", this.mobs.size())
                    .print();
        }
    }

    private Collection<SpellTemplate> getIncludedSpellTemplates() {
        boolean loadHidden = getConfig().shouldLoadHidden();
        List<String> excludeCategories = getRulesFile().getFileConfiguration().getStringList("spells.exclude_categories");
        List<SpellTemplate> spellTemplates = new ArrayList<>();

        for (SpellTemplate template : getMagicAPI().getSpellTemplates(loadHidden)) {
            com.elmakers.mine.bukkit.api.spell.SpellCategory category = template.getCategory();

            if (category != null && !excludeCategories.contains(category.getKey())) {
                spellTemplates.add(template);
            }
        }

        return spellTemplates;
    }

    public String getMessage(String key) {
        return getMessages().getMessage(key);
    }

    public String getMessage(String key, Map<String, String> replace) {
        return StringUtilities.replace(getMessage(key), replace);
    }

    public List<String> getLore(String key) {
        return StringUtilities.color(getMessages().getFileConfiguration().getStringList(key));
    }

    public List<String> getLore(String key, Map<String, String> replace) {
        return StringUtilities.replace(getLore(key), replace);
    }

    public void sendMessage(CommandSender sender, String key) {
        this.sendMessage(sender, key, null);
    }

    public void sendMessage(CommandSender sender, String key, Map<String, String> replacements) {
        sender.sendMessage(getMessage(key, replacements));
    }

    public void sendMessage(CommandContext context, String key) {
        sendMessage(context.getSender(), key);
    }

    public void sendMessage(CommandContext context, String key, Map<String, String> replacements) {
        sendMessage(context.getSender(), key, replacements);
    }

    public List<String> getActionNames() {
        return getActions().stream().map(SpellAction::getName).collect(Collectors.toList());
    }

    public List<String> getMobKeys() {
        return getMobs().stream().map(MagicType::getKey).collect(Collectors.toList());
    }

    public List<String> getPathKeys() {
        return getPaths().stream().map(MagicType::getKey).collect(Collectors.toList());
    }

    public List<String> getSpellCategoryKeys() {
        return getSpellCategories().stream().map(MagicType::getKey).collect(Collectors.toList());
    }

    public List<String> getSpellKeys() {
        return getSpells().stream().map(MagicType::getKey).collect(Collectors.toList());
    }

    public List<String> getWandKeys() {
        return getWands().stream().map(MagicType::getKey).collect(Collectors.toList());
    }

    public List<MagicSpell> getAllSpells() {
        List<MagicSpell> spells = new ArrayList<>();

        for (SpellTemplate template : getMagicAPI().getSpellTemplates(true)) {
            spells.add(new MagicSpell(this, template));
        }

        return spells;
    }

    public List<MagicSpell> getSpells(String categoryKey) {
        List<MagicSpell> all = getAllSpells();

        if (categoryKey.equals("all")) {
            return all;
        }

        List<MagicSpell> spells = new ArrayList<>();

        for (MagicSpell spell : all) {
            spell.getCategory().ifPresent(cat -> {
                if (cat.getKey().equals(categoryKey)) {
                    spells.add(spell);
                }
            });
        }

        return spells;
    }

    public Optional<MagicPath> resolvePath(String key) {
        return getPaths().stream().filter((path) -> path.getKey().equalsIgnoreCase(key)).findFirst();
    }

    public Optional<SpellCategory> resolveSpellCategory(String key) {
        return getSpellCategories().stream().filter((cat) -> cat.getKey().equalsIgnoreCase(key)).findFirst();
    }

    public Optional<MagicSpell> resolveSpell(String key) {
        return getAllSpells().stream().filter((spell) -> spell.getKey().equalsIgnoreCase(key)).findFirst();
    }

    public Optional<SpellRule> resolveSpellRule(String key) {
        return getSpellRules().stream().filter((rule) -> rule.getKey().equalsIgnoreCase(key)).findFirst();
    }

    public Optional<MagicWand> resolveWand(String key) {
        return getWands().stream().filter((wand) -> wand.getKey().equalsIgnoreCase(key)).findFirst();
    }

    public Optional<MagicMob> resolveMob(String key) {
        return getMobs().stream().filter((mob) -> mob.getKey().equalsIgnoreCase(key)).findFirst();
    }

    public Optional<SpellAction> resolveAction(String key) {
        for (SpellAction action : getActions()) {
            if (StringUtilities.equalsAny(key, action.getName(), action.getClassName(), action.getShortClass())) {
                return Optional.of(action);
            }
        }

        return Optional.empty();
    }
}
