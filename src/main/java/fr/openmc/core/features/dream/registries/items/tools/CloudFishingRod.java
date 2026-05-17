package fr.openmc.core.features.dream.registries.items.tools;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CloudFishingRod extends DreamItem {
    public CloudFishingRod() {
        super(new DreamItemMeta(
                "omc_dream:cloud_fishing_rod",
                "Cloud Fishing Rod",
                DreamRarity.EPIC,
                Material.FISHING_ROD,
                false
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return null;
    }
}
