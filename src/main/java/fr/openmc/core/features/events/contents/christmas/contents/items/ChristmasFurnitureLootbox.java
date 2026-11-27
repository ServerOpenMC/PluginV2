package fr.openmc.core.features.events.contents.christmas.contents.items;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.items.options.LootboxBlock;
import fr.openmc.core.registry.lootboxes.CustomLootbox;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ChristmasFurnitureLootbox extends CustomItem implements LootboxBlock {
    public ChristmasFurnitureLootbox(String id) {
        super(id);
    }

    @Override
    public ItemStack getVanilla() {
        return new ItemStack(Material.GLASS);
    }

    @Override
    public CustomLootbox getLootbox() {
        return OMCRegistry.CUSTOM_LOOTBOXES.CHRISTMAS_FURNITURE;
    }
}
