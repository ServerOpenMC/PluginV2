package fr.openmc.core.features.dream.registries.items.loots;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CloudKey extends DreamItem {
    public CloudKey() {
        super(new DreamItemMeta(
                "omc_dream:cloud_key",
                "Clé nuageuse",
                DreamRarity.RARE,
                Material.OMINOUS_TRIAL_KEY,
                false
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return null;
    }
}
