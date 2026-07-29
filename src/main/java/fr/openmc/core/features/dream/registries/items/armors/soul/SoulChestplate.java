package fr.openmc.core.features.dream.registries.items.armors.soul;

import fr.openmc.core.features.dream.models.registry.items.DreamEquipableItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SoulChestplate extends DreamItem implements DreamEquipableItem {
    public SoulChestplate() {
        super(new DreamItemMeta(
                "omc_dream:soul_chestplate",
                TranslationManager.translation("feature.dream.item.soul_chestplate.name"),
                DreamRarity.RARE,
                Material.LEATHER_CHESTPLATE,
                true
        ));
    }

    @Override
    public long getAdditionalMaxTime() {
        return 15;
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
