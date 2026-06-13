package fr.openmc.core.features.dream.registries.items.loots;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BurnCoal extends DreamItem {
    public BurnCoal() {
        super(new DreamItemMeta(
                "omc_dream:coal_burn",
                "Charbon brulé",
                DreamRarity.EPIC,
                Material.CHARCOAL,
                false
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return null;
    }
}
