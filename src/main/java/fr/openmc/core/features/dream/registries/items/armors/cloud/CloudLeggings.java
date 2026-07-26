package fr.openmc.core.features.dream.registries.items.armors.cloud;

import fr.openmc.core.features.dream.models.registry.items.DreamEquipableItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CloudLeggings extends DreamItem implements DreamEquipableItem {
    public CloudLeggings() {
        super(new DreamItemMeta(
                "omc_dream:cloud_leggings",
                TranslationManager.translation("feature.dream.item.cloud_leggings.name"),
                DreamRarity.EPIC,
                Material.LEATHER_LEGGINGS,
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
