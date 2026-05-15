package fr.openmc.core.features.dream.registries.items.blocks;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class EternalCampFire extends DreamItem {
    public EternalCampFire() {
        super(new DreamItemMeta(
                "omc_dream:eternal_campfire",
                "Feu de Camp Eternel",
                DreamRarity.EPIC,
                Material.CAMPFIRE,
                false
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return null;
    }
}
