package fr.openmc.core.features.dream.registries.items.armors.cold;

import fr.openmc.core.features.dream.models.registry.items.DreamEquipableItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ColdBoots extends DreamItem implements DreamEquipableItem {
    public ColdBoots() {
        super(new DreamItemMeta(
                "omc_dream:cold_boots",
                "Jambières des Nuages",
                DreamRarity.EPIC,
                Material.LEATHER_LEGGINGS,
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
