package fr.openmc.core.features.dream.registries.items.armors.creaking;

import fr.openmc.core.features.dream.models.registry.items.DreamEquipableItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class OldCreakingBoots extends DreamItem implements DreamEquipableItem {
    public OldCreakingBoots() {
        super(new DreamItemMeta(
                "omc_dream:old_creaking_boots",
                TranslationManager.translation("feature.dream.item.old_creaking_boots.name"),
                DreamRarity.COMMON,
                Material.LEATHER_BOOTS,
                true
        ));
    }

    @Override
    public long getAdditionalMaxTime() {
        return 5;
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
