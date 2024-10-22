package io.github.potterplus.magicscan.gui.spell;

import io.github.potterplus.api.item.Icon;
import io.github.potterplus.api.ui.UserInterface;
import io.github.potterplus.api.ui.button.AutoUIButton;
import io.github.potterplus.api.ui.button.UIButton;
import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.magic.MagicSpell;
import lombok.NonNull;
import org.bukkit.Material;

public class SpellBreakdownGUI extends UserInterface {

    @NonNull private final MagicScanController controller;
    @NonNull private final MagicSpell spell;

    public SpellBreakdownGUI(MagicScanController controller, MagicSpell spell) {
        super("Creating spell breakdown...", 54);

        this.controller = controller;
        this.spell = spell;

        this.refreshButtons();

        setTitle("&7Breakdown of spell &e" + spell.getKey());
    }

    public void refreshButtons() {
        this.clearButtons();

        Icon textElements = Icon
                .start(Material.PAPER)
                .name("&7Text Components")
                .lore(
                        "&7Display Name&8: &r" + spell.getNameString(),
                        "&7Description&8: &r" + spell.getDescriptionString()
                );

        setButton(0, new AutoUIButton(textElements));

        boolean hasPrev = spell.getPreviousLevel().isPresent();
        boolean hasNext = spell.getNextLevel().isPresent();

        if (!hasPrev && !hasNext) {
            Icon icon = Icon
                    .start(Material.BARRIER)
                    .name("&cThis spell only has one level");
            setButton(53, new AutoUIButton(icon));
        } else {
            if (hasPrev) {
                MagicSpell prev = spell.getPreviousLevel().get();
                Icon icon = Icon
                        .start(Material.ARROW)
                        .name("&b<&d&m--&r &7Previous level")
                        .lore(
                                "",
                                "&8> &eClick &7to break down the previous level",
                                "",
                                "  &7Current level&8: &e" + spell.getCurrentLevel()
                        );
                UIButton button = new UIButton(icon);

                button.setListener((event -> {
                    new SpellBreakdownGUI(controller, prev).activate(event.getWhoClicked());
                }));

                setButton(52, button);
            } else {
                Icon icon = Icon
                        .start(Material.BARRIER)
                        .name("&cNo previous level");
                setButton(52, new AutoUIButton(icon));
            }

            if (hasNext) {
                MagicSpell next = spell.getNextLevel().get();
                Icon icon = Icon
                        .start(Material.ARROW)
                        .name("&d&m--&r&d>&r &7Next level")
                        .lore(
                                "",
                                "&8> &eClick &7to break down the next level",
                                "",
                                "  &7Current level&8: &e" + spell.getCurrentLevel()
                        );
                UIButton button = new UIButton(icon);

                button.setListener((event -> {
                    new SpellBreakdownGUI(controller, next).activate(event.getWhoClicked());
                }));

                setButton(53, button);
            } else {
                Icon icon = Icon
                        .start(Material.BARRIER)
                        .name("&cNo next level");
                setButton(53, new AutoUIButton(icon));
            }
        }
    }
}
