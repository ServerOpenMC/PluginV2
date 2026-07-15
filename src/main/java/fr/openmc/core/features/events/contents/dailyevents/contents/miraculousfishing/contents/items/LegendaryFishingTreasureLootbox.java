package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.items;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.items.options.LootboxBlock;
import fr.openmc.core.registry.lootboxes.CustomLootbox;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class LegendaryFishingTreasureLootbox extends CustomItem implements LootboxBlock {
    public LegendaryFishingTreasureLootbox(String id) {
        super(id);
    }

    @Override
    public ItemStack getVanilla() {
        return new ItemStack(Material.GLASS);
    }

    @Override
    public CustomLootbox getLootbox() {
        return OMCRegistry.CUSTOM_LOOTBOXES.LEGENDARY_FISHING_TREASURE;
    }
}
