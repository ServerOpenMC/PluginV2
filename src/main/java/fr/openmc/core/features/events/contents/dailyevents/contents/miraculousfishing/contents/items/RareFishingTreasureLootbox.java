package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.items;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.items.options.LootboxBlock;
import fr.openmc.core.registry.lootboxes.CustomLootbox;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RareFishingTreasureLootbox extends CustomItem implements LootboxBlock {
    public RareFishingTreasureLootbox(String id) {
        super(id);
    }

    @Override
    public ItemStack getVanilla() {
        return new ItemStack(Material.GLASS);
    }

    @Override
    public CustomLootbox getLootbox() {
        return OMCRegistry.CUSTOM_LOOTBOXES.RARE_FISHING_TREASURE;
    }
}
