package io.github.potterplus.magicscan.file;

import io.github.potterplus.api.storage.flatfile.PluginYamlFile;
import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.MagicScanPlugin;

/**
 * A handler for the rules.yml file.
 */
public class RulesFile extends PluginYamlFile<MagicScanPlugin> {

    public RulesFile(MagicScanController controller) {
        super(controller.getPlugin(), "rules.yml");
    }
}
