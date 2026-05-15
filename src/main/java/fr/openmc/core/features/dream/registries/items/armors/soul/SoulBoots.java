package fr.openmc.core.features.dream.registries.items.armors.soul;

import fr.openmc.core.features.dream.models.registry.items.DreamEquipableItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class SoulBoots extends DreamItem implements DreamEquipableItem {
    public SoulBoots() {
        super(new DreamItemMeta(
                "omc_dream:soul_boots",
                "Bottes des Âmes",
                DreamRarity.RARE,
                Material.LEATHER_BOOTS,
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
