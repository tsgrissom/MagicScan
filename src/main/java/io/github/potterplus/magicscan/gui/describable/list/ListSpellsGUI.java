package io.github.potterplus.magicscan.gui.describable.list;

import com.google.common.collect.ImmutableMap;
import io.github.potterplus.api.item.Icon;
import io.github.potterplus.api.misc.PluginLogger;
import io.github.potterplus.api.ui.button.AutoUIButton;
import io.github.potterplus.api.ui.button.UIButton;
import io.github.potterplus.magicscan.MagicScanController;
import io.github.potterplus.magicscan.file.ConfigFile;
import io.github.potterplus.magicscan.gui.describable.ListDescribablesGUI;
import io.github.potterplus.magicscan.gui.spell.SpellBreakdownGUI;
import io.github.potterplus.magicscan.magic.MagicSpell;
import io.github.potterplus.magicscan.magic.spell.SpellCategory;
import io.github.potterplus.magicscan.misc.Describable;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.regex.Pattern;

/**
 * A GUI listing all Magic spells.
 */
public class ListSpellsGUI extends ListDescribablesGUI {

    private boolean showHidden, showLeveledVariants, showDisabledIcons;
    private String currentCategory;

    public ListSpellsGUI(HumanEntity target, MagicScanController controller) {
        super("Spells", target, controller);

        this.currentCategory = "all";
    }

    public ListSpellsGUI(HumanEntity target, MagicScanController controller, String currentCategory) {
        this(target, controller);

        this.currentCategory = currentCategory == null ? "all" : currentCategory;
    }

    List<String> getAvailableCategories() {
        List<String> list = new ArrayList<>();

        list.add("all");

        for (SpellCategory cat : getController().getSpellCategories()) {
            list.add(cat.getKey());
        }

        Collections.sort(list);

        return list;
    }

    public String getCurrentCategory() {
        return currentCategory == null ? "all" : currentCategory;
    }

    public void update(HumanEntity human) {
        this.refreshToolbar();
        this.resetPage();
        this.refreshEntries();
        this.refreshInventory(human);
    }

    public void update(InventoryClickEvent event) {
        this.update(event.getWhoClicked());
    }

    void refreshToolbar() {
        MagicScanController controller = getController();
        ConfigFile config = controller.getConfig();
        Icon enabled = Icon.of(config.getIcon("enabled", Material.GREEN_STAINED_GLASS));
        Icon disabled = Icon.of(config.getIcon("disabled", Material.RED_STAINED_GLASS));
        
        UIButton showHidden = new UIButton(
                this.showHidden
                        ? enabled.name(controller.getMessage("gui.list_spells.show_hidden_enabled.name")).lore(controller.getLore("gui.list_spells.show_hidden_enabled.lore"))
                        : disabled.name(controller.getMessage("gui.list_spells.show_hidden_disabled.name")).lore(controller.getLore("gui.list_spells.show_hidden_disabled.lore"))
        );

        showHidden.setListener(event -> {
            event.setCancelled(true);

            this.showHidden = !this.showHidden;
            this.update(event);
        });

        UIButton showLeveledVariants = new UIButton(
                this.showLeveledVariants
                        ? enabled.name(controller.getMessage("gui.list_spells.show_leveled_variants_enabled.name")).lore(controller.getLore("gui.list_spells.show_leveled_variants_enabled.lore"))
                        : disabled.name(controller.getMessage("gui.list_spells.show_leveled_variants_disabled.name")).lore(controller.getLore("gui.list_spells.show_leveled_variants_disabled.lore"))
        );

        showLeveledVariants.setListener(event -> {
            event.setCancelled(true);

            this.showLeveledVariants = !this.showLeveledVariants;
            this.update(event);
        });

        UIButton showDisabledIcons = new UIButton(
                this.showDisabledIcons
                        ? enabled.name(controller.getMessage("gui.list_spells.show_disabled_icons_enabled.name")).lore(controller.getLore("gui.list_spells.show_hidden_enabled.lore"))
                        : disabled.name(controller.getMessage("gui.list_spells.show_disabled_icons_disabled.name")).lore(controller.getLore("gui.list_spells.show_disabled_icons_disabled.lore"))
        );

        showDisabledIcons.setListener(event -> {
            event.setCancelled(true);

            this.showDisabledIcons = !this.showDisabledIcons;
            this.update(event);
        });

        String[] categories = new String[getAvailableCategories().size()];

        for (int i = 0; i < getAvailableCategories().size(); i++) {
            String current = getAvailableCategories().get(i);

            String lineFormat = controller.getMessage("gui.list_spells.cycle_categories.category_line_format");
            String otherColor = controller.getMessage("gui.list_spells.cycle_categories.other_color");
            String currentColor = controller.getMessage("gui.list_spells.cycle_categories.current_color");

            String allFormat = lineFormat.replaceAll(Pattern.quote("$cat"), currentColor + "all");

            if (getCurrentCategory().equals("all") && i == 0) {
                categories[i] = allFormat;
            }

            categories[i] = lineFormat.replaceAll(Pattern.quote("$cat"), (current.equalsIgnoreCase(getCurrentCategory()) ? currentColor : otherColor) + current);
        }

        UIButton cycleCategories = new UIButton(
                Icon
                        .of(config.getIcon("cycle", Material.MAP))
                        .name(controller.getMessage("gui.list_spells.cycle_categories.name", ImmutableMap.of("$currentCategory", getCurrentCategory())))
                        .lore(controller.getLore("gui.list_spells.cycle_categories.lore"))
                        .addLore(categories)
        );

        cycleCategories.setListener(event -> {
            event.setCancelled(true);

            ItemMeta meta = cycleCategories.getItem().getItemMeta();

            if (meta == null) {
                return;
            }

            int index = getAvailableCategories().indexOf(getCurrentCategory());

            if (index < 0) {
                return;
            }

            switch (event.getClick()) {
                case LEFT:
                    index--;

                    if (index <= 0) {
                        index = getAvailableCategories().size() - 1;
                    }

                    this.currentCategory = getAvailableCategories().get(index);

                    this.update(event);

                    break;
                case RIGHT:
                    index++;

                    if (index >= getAvailableCategories().size()) {
                        index = 0;
                    }

                    this.currentCategory = getAvailableCategories().get(index);

                    this.update(event);

                    break;
                case SHIFT_LEFT:
                    new ListSpellCategoriesGUI(getTarget(), getController()).activate();

                    break;
                case SHIFT_RIGHT:
                    this.currentCategory = getAvailableCategories().get(0);

                    this.update(event);

                    break;
            }
        });

        this.setToolbarItem(0, showHidden);
        this.setToolbarItem(1, showLeveledVariants);
        this.setToolbarItem(2, showDisabledIcons);
        this.setToolbarItem(8, cycleCategories);
    }

    void refreshEntries() {
        MagicScanController controller = getController();
        List<MagicSpell> spells = controller.getFilteredSpells();
        List<MagicSpell> newSpells = new ArrayList<>(spells);

        for (MagicSpell spell : spells) {
            if (spell.isLeveledVariant() && !showLeveledVariants) {
                newSpells.remove(spell);
            }

            if (spell.getTemplate().isHidden() && !showHidden) {
                newSpells.remove(spell);
            }
        }

        Collections.sort(newSpells);

        newSpells.forEach(this::populate);

        boolean empty = getInventory().getItem(0) == null;
        Map<String, String> countReplaceMap = ImmutableMap.of("$count", empty ? "&cNONE" : String.valueOf(getItems().size()));

        if (empty) {
            addButton(new AutoUIButton(
                    Icon
                            .of(controller.getConfig().getIcon("empty", Material.BARRIER))
                            .name(controller.getMessage("gui.list_spells.no_spells.name"))
                            .lore(controller.getLore("gui.list_spells.no_spells.lore"))
            ));
        }

        this.setTitle(controller.getMessage("gui.list_spells.title", countReplaceMap));
    }

    @Override
    public void populate(Describable describable) {
        if (!(describable instanceof MagicSpell)) {
            PluginLogger.atSevere()
                    .with("Cannot populate spell list GUI with incorrect describable type!")
                    .print();

            return;
        }

        MagicSpell spell = (MagicSpell) describable;
        ItemStack def = spell.describeAsItem(getTarget());
        ItemMeta meta = def.getItemMeta();
        Optional<ItemStack> opt = showDisabledIcons ? spell.getDisabledIcon() : spell.getIcon();
        ItemStack item = opt.orElseGet(() -> spell.describeAsItem(getTarget()));

        item.setItemMeta(meta);

        UIButton button = new UIButton(item);

        button.setListener((event -> {
            new SpellBreakdownGUI(getController(), spell).activate(event.getWhoClicked());
        }));

        this.addButton(button);
    }

    @Override
    public void initialize() {
        this.refreshToolbar();
        this.refreshEntries();
    }
}
