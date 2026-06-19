package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.items;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.items.options.UsableItem;
import fr.openmc.core.utils.bukkit.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class RareFishingTreasureLootbox extends CustomItem implements UsableItem {
    public RareFishingTreasureLootbox(String id) {
        super(id);
    }

    @Override
    public ItemStack getVanilla() {
        return new ItemStack(Material.GLASS);
    }

    @Override
    public void onRightClick(Player player, PlayerInteractEvent event) {
        ItemUtils.removeItemsFromInventory(player, this.getBest(), 1);
        OMCRegistry.CUSTOM_LOOTBOXES.RARE_FISHING_TREASURE.open(player);
    }
}
