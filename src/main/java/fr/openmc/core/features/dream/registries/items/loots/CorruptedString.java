package fr.openmc.core.features.dream.registries.items.loots;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CorruptedString extends DreamItem {
    public CorruptedString() {
        super(new DreamItemMeta(
                "omc_dream:corrupted_string",
                "Fil corrompu",
                DreamRarity.COMMON,
                Material.STRING,
                false
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return null;
    }
}
