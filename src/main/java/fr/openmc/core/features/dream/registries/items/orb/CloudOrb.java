package fr.openmc.core.features.dream.registries.items.orb;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CloudOrb extends DreamItem {
    public CloudOrb() {
        super(new DreamItemMeta(
                "omc_dream:cloud_orb",
                TranslationManager.translation("feature.dream.item.cloud_orb.name"),
                DreamRarity.ONIRISIME,
                Material.HEART_OF_THE_SEA,
                true
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return this.getBest();
    }
}
