package fr.openmc.core.features.corporation.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopMenu extends Menu {

    private int amountToBuy = 1;
    
    public ShopMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        return "Menu du shop ";
    }

    @Override
    public String getTexture() {
		return "";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGER;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> content = new HashMap<>();
		
        return content;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
