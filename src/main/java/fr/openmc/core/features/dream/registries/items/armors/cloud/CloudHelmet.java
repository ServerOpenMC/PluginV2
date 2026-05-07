package fr.openmc.core.features.dream.registries.items.armors.cloud;

import fr.openmc.core.features.dream.models.registry.items.DreamEquipableItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CloudHelmet extends DreamItem implements DreamEquipableItem {
    public CloudHelmet() {
        super(new DreamItemMeta(
                "omc_dream:cloud_helmet",
                "Casques des Nuages",
                DreamRarity.EPIC,
                Material.LEATHER_HELMET,
                true
        ));
    }

    @Override
    public long getAdditionalMaxTime() {
        return 30;
    }

    @Override
    public Integer getColdResistance() {
        return null;
    }

    @Override
    public ItemStack getTransferableItem() {
        return this.getBestTransferable();
    }
}
