package fr.openmc.core.features.dream.registries.items.armors.cold;

import fr.openmc.core.features.dream.models.registry.items.DreamEquipableItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ColdHelmet extends DreamItem implements DreamEquipableItem {
    public ColdHelmet() {
        super(new DreamItemMeta(
                "omc_dream:cold_helmet",
                TranslationManager.translation("feature.dream.item.cold_helmet.name"),
                DreamRarity.LEGENDARY,
                Material.LEATHER_HELMET,
                true
        ));
    }

    @Override
    public long getAdditionalMaxTime() {
        return 60;
    }

    @Override
    public Integer getColdResistance() {
        return 1;
    }

    @Override
    public ItemStack getTransferableItem() {
        return this.getBestTransferable();
    }
}
