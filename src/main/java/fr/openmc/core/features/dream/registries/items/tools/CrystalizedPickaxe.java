package fr.openmc.core.features.dream.registries.items.tools;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CrystalizedPickaxe extends DreamItem {
    public CrystalizedPickaxe() {
        super(new DreamItemMeta(
                "omc_dream:crystallized_pickaxe",
                "Pioche Crysalisée",
                DreamRarity.LEGENDARY,
                Material.DIAMOND_PICKAXE,
                false
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return null;
    }
}
