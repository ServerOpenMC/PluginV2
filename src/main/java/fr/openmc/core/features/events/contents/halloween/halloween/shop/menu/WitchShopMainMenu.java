package fr.openmc.core.features.events.contents.halloween.halloween.shop.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.menu.main.buttons.*;
import fr.openmc.core.features.events.contents.halloween.halloween.shop.menu.buttons.WitchHouseButton;
import fr.openmc.core.features.events.contents.halloween.halloween.shop.menu.buttons.WitchStoreButton;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WitchShopMainMenu extends Menu {

    private static final int[] WITCH_HOUSE_SLOTS = {8, 9, 10, 11, 12, 13, 18, 19, 20, 21, 22, 27, 28, 29, 30, 31, 36, 37, 38, 39, 40, 45, 46, 47, 48, 49};
    private static final int[] WITCH_SHOP_SLOTS = {23, 24, 25, 26, 32, 33, 34, 35, 41, 42, 43, 44, 50, 51, 52, 53};

    public WitchShopMainMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.city.menus.main.name"); //TODO
    }

    @Override
    public String getTexture() {
        return "§r§f:offset_-48::halloween_event_main_menu:";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> inventory = new HashMap<>();

        // ** Witch House Button
        WitchHouseButton.init(this, inventory, WITCH_HOUSE_SLOTS);

        // ** Witch Store Button
        WitchStoreButton.init(this, inventory, WITCH_SHOP_SLOTS);

        return inventory;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        //TODO bruit de sorcière
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
    }
}
