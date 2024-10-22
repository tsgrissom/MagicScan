package io.github.potterplus.magicscan.file;

import io.github.potterplus.api.storage.flatfile.YamlFile;
import io.github.potterplus.magicscan.MagicScanController;

import java.io.File;

/**
 * Represents a YAML file from /plugins/Magic/defaults/
 */
public class MagicDefFile extends YamlFile {

    public static final String PATH;

    static {
        PATH = "Magic" + File.separator + "defaults" + File.separator;
    }

    private MagicScanController controller;

    public MagicDefFile(MagicScanController controller, String defaultsFile) {
        super(controller.getPlugin().getDataFolder().getParentFile(), defaultsFile.startsWith(PATH) ? defaultsFile : PATH + defaultsFile);

        this.controller = controller;
    }
}
