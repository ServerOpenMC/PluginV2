package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.items;

import dev.lone.itemsadder.api.Events.FurniturePrePlaceEvent;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.items.options.UsableBlock;
import fr.openmc.core.utils.bukkit.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LegendaryFishingTreasureLootbox extends CustomItem implements UsableBlock {
    public LegendaryFishingTreasureLootbox(String id) {
        super(id);
    }

    @Override
    public ItemStack getVanilla() {
        return new ItemStack(Material.GLASS);
    }

    @Override
    public void onFurniturePlace(Player player, FurniturePrePlaceEvent event) {
        event.setCancelled(true);
        ItemUtils.removeItemsFromInventory(player, this.getBest(), 1);
        OMCRegistry.CUSTOM_LOOTBOXES.LEGENDARY_FISHING_TREASURE.open(player);
    }
}
