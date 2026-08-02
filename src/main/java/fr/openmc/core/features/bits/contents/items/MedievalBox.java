package fr.openmc.core.features.bits.contents.items;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.items.options.LootboxBlock;
import fr.openmc.core.registry.lootboxes.CustomLootbox;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class MedievalBox extends CustomItem implements LootboxBlock {
    public MedievalBox(String id) {
        super(id);
    }

    @Override
    public @NonNull ItemStack getVanilla() {
        return new ItemStack(Material.GLASS);
    }

    @Override
    public CustomLootbox getLootbox() {
        return OMCRegistry.CUSTOM_LOOTBOXES.MEDIEVAL_BOX;
    }
}