package io.github.potterplus.magicscan.file;

import com.elmakers.mine.bukkit.api.block.MaterialAndData;
import com.elmakers.mine.bukkit.api.item.ItemData;
import com.elmakers.mine.bukkit.api.magic.MageController;
import io.github.potterplus.api.misc.PluginLogger;
import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.rule.RuleType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Write docs
 */
@RequiredArgsConstructor
public class ConfigFile {

    @NonNull
    private MagicScanController controller;

    public FileConfiguration getFileConfiguration() {
        return controller.getPlugin().getConfig();
    }

    public boolean isVerbose() {
        return getFileConfiguration().getBoolean("verbose", true);
    }

    public boolean shouldLoadHidden() {
        return getFileConfiguration().getBoolean("load_hidden", true);
    }

    public boolean shouldCreateScanOnStart() {
        return getFileConfiguration().getBoolean("create_scan_on_start", true);
    }

    public boolean shouldPerformScanOnStart() {
        return shouldCreateScanOnStart() && getFileConfiguration().getBoolean("perform_scan_on_start", false);
    }

    public boolean isUsingPastebinIntegration() {
        return getFileConfiguration().getBoolean("pastebin_integration", false) && controller.getPastebinFactory() != null && controller.getPastebin() != null;
    }

    public String getPastebinKey() {
        return getFileConfiguration().getString("pastebin_key");
    }

    public int getInactiveScanTimeout() {
        return getFileConfiguration().getInt("inactive_scan_timeout", 1200);
    }

    public int getInterval() {
        return getFileConfiguration().getInt("interval", 5);
    }

    public SimpleDateFormat getDateFormat() {
        String def = "yyyy/MM/dd HH:mm:ss";
        String format = getFileConfiguration().getString("date_format", def);

        if (format == null) {
            format = def;
        }

        return new SimpleDateFormat(format);
    }

    public String getIconString(String key, Material def) {
        ConfigurationSection cs = getFileConfiguration();

        cs = cs.getConfigurationSection("materials");

        if (cs == null) {
            PluginLogger
                    .atWarn()
                    .with("Materials section cannot be null!")
                    .print();

            return def.name();
        }

        if (!cs.isSet(key)) {
            PluginLogger
                    .atWarn()
                    .with("Cannot resolve material '%s'!", key)
                    .print();

            return def.name();
        }

        return cs.getString(key, def.name());
    }

    public ItemStack getIcon(String key, Material def) {
        MageController mc = controller.getMageController();
        ItemStack defItem = new ItemStack(def);

        String s = getIconString(key, def);
        ItemData data = mc.getOrCreateItem(s);

        if (data == null) {
            PluginLogger
                    .atWarn()
                    .with("Data for '%s' cannot be null!", s)
                    .print();

            return defItem;
        }

        MaterialAndData mat = data.getMaterialAndData();

        if (mat == null) {
            PluginLogger
                    .atWarn()
                    .with("MaterialAndData for '%s' cannot be null!", s)
                    .print();

            return defItem;
        }

        ItemStack item = mat.getItemStack(1);

        if (item == null) {
            PluginLogger
                    .atInfo()
                    .with("Couldn't resolve Magic item for '%s'", s)
                    .print();

            return new ItemStack(getMaterial(s, def));
        } else {
            return item;
        }
    }

    public Material getMaterial(String key, Material def) {
        FileConfiguration fc = getFileConfiguration();

        if (fc == null || !fc.isSet(key)) {
            return def;
        }

        String s = fc.getString(key);
        
        if (s == null) {
            return def;
        }

        Material mat = Material.getMaterial(s);

        if (mat == null) {
            mat = def;
        }

        return mat;
    }

    public List<String> getListFilters(String key) {
        ConfigurationSection section = getFileConfiguration().getConfigurationSection("list_filters");

        if (section == null) {
            return null;
        }

        Object obj = section.get(key);

        if (obj != null) {
            return section.getStringList(key);
        } else {
            PluginLogger.atWarn()
                    .with("Could not resolve list filters '%s'", key)
                    .print();

            return null;
        }
    }

    public List<RuleType> getRuleTypeList(String node, List<RuleType> def) {
        List<RuleType> list = getRuleTypeList(node);

        if (list == null || list.isEmpty()) return list;
        else return def;
    }

    public List<RuleType> getRuleTypeList(String node) {
        return getFileConfiguration().getStringList(node).stream()
                .map(new RuleType.StringToRuleTypeFunction())
                .collect(Collectors.toList());
    }
}
