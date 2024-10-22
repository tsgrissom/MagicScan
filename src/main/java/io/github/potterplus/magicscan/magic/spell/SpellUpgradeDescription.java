package io.github.potterplus.magicscan.magic.spell;

import io.github.potterplus.api.string.StringUtilities;
import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.magic.MagicSpell;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a spell's upgrade description and the formatting options available for it.
 */
public class SpellUpgradeDescription {

    @NonNull
    private MagicScanController controller;
    private String message;

    public SpellUpgradeDescription(MagicScanController controller, MagicSpell spell) {
        this.controller = controller;
        this.message = controller.getMagicAPI().getMessages().get("spells." + spell.getKey() + ".upgrade_description");
    }

    public String getPrefix() {
        return controller.getMagicAPI().getMessages().get("spell.upgrade_description_prefix");
    }

    public List<String> getFormatted() {
        List<String> list = new ArrayList<>();

        for (String s : message.split("\n")) {
            list.add(getPrefix() + s);
        }

        return StringUtilities.color(list);
    }
}
