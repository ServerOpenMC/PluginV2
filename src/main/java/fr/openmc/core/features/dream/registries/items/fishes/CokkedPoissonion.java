package fr.openmc.core.features.dream.registries.items.fishes;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CokkedPoissonion extends DreamItem {
    public CokkedPoissonion() {
        super(new DreamItemMeta(
                "omc_dream:cooked_poissonion",
                "Poissonion Cuit",
                DreamRarity.RARE,
                Material.COOKED_COD,
                true
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return this.getBest();
    }
}
