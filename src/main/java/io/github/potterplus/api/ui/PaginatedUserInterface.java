package io.github.potterplus.api.ui;

import com.google.common.collect.ImmutableMap;
import io.github.potterplus.api.item.Icon;
import io.github.potterplus.api.ui.button.AutoUIButton;
import io.github.potterplus.api.ui.button.UIButton;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static io.github.potterplus.api.string.StringUtilities.replace;

/**
 * An extension of a basic GUI which allows for items to be paginated across as many pages as necessary along with a persistent toolbar.
 */
public class PaginatedUserInterface extends UserInterface {

    @Getter @Setter
    private Map<Integer, UIButton> toolbarItems;

    @Getter
    private int currentPage;

    public PaginatedUserInterface(String name) {
        super(name, 54);

        this.toolbarItems = new HashMap<>();
        this.currentPage = 0;
    }

    @Override
    public UIButton getButton(int slot) {
        if (slot < 45) {
            return getItems().get(slot);
        } else {
            return getToolbarItems().get(slot - 45);
        }
    }

    public void setToolbarItem(int slot, UIButton button) {
        if (slot < 0 || slot > 8) {
            throw new IllegalArgumentException("Slot must be between 0-8.");
        }

        toolbarItems.put(slot, button);
    }

    public void removeToolbarItem(int slot) {
        if (slot < 0 || slot > 8) {
            throw new IllegalArgumentException("Slot must be between 0-8.");
        }

        toolbarItems.remove(slot);
    }

    public boolean nextPage() {
        if (currentPage < getMaxPage()) {
            currentPage++;

            return true;
        } else return false;
    }

    public boolean previousPage() {
        if (currentPage > 0) {
            currentPage--;

            return true;
        } else return false;
    }

    public int getMaxPage() {
        int slot = 0;

        for (int nextSlot : getItems().keySet()) {
            if (nextSlot > slot) {
                slot = nextSlot;
            }
        }

        double highestSlot = slot + 1;

        return (int) Math.ceil(highestSlot / (double) 45) - 1;
    }

    public void resetPage() {
        this.currentPage = 0;
        this.setItems(new HashMap<>());
    }

    public Icon createNavigation(int currentPage, int maxPage, boolean controls) {
        Map<String, String> replace = ImmutableMap.of("$currentPage", String.valueOf(currentPage), "$maxPage", String.valueOf(maxPage));
        Icon pageNav = Icon.start(Material.NAME_TAG)
                .name(replace("&7Page &e$currentPage&8/&e$maxPage", replace));

        if (controls) {
            pageNav.lore(replace(Arrays.asList("&8> &6Left-click &7to go to the previous page", "&8> &6Right-click &7to go to the next page"), replace));
        }

        return pageNav;
    }

    public void clearToolbar() {
        this.toolbarItems = new HashMap<>();
    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, getSize(), getTitle());

        UIButton nav = new UIButton(this.createNavigation(getCurrentPage() + 1, getMaxPage() + 1, true));

        nav.setListener(event -> {
            event.setCancelled(true);

            PaginatedUserInterface menu = (PaginatedUserInterface) event.getInventory().getHolder();

            switch (event.getClick()) {
                case LEFT:
                    if (!menu.previousPage()) {
                        this.currentPage = getMaxPage();
                    }

                    this.refreshInventory(event.getWhoClicked());
                    break;
                case RIGHT:
                    if (!menu.nextPage()) {
                        this.currentPage = 0;
                    }

                    this.refreshInventory(event.getWhoClicked());
                    break;
            }
        });

        if (getMaxPage() > 0) {
            toolbarItems.put(4, nav);
        }

        if (getItems() != null && !getItems().isEmpty()) {
            int counter = 0;

            for (int key = (currentPage * 45); key <= Collections.max(getItems().keySet()); key++) {
                if (counter >= 45) {
                    break;
                }

                if (getItems().containsKey(key)) {
                    inventory.setItem(counter, getItems().get(key).getItem());
                }

                counter++;
            }
        }

        for (int toolbarItem : toolbarItems.keySet()) {
            int rawSlot = toolbarItem + 45;

            inventory.setItem(rawSlot, toolbarItems.get(toolbarItem).getItem());
        }

        if (inventory.getItem(44) == null && getCurrentPage() == 0) {
            Icon pageNav = this.createNavigation(1, 1, false);

            inventory.setItem(49, pageNav.build());

            setToolbarItem(4, new AutoUIButton(pageNav));
        }

        return inventory;
    }
}
