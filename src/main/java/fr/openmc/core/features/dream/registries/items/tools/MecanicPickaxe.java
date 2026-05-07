package fr.openmc.core.features.dream.registries.items.tools;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MecanicPickaxe extends DreamItem {
    public MecanicPickaxe() {
        super(new DreamItemMeta(
                "omc_dream:mecanic_pickaxe",
                "Pioche Mécanisée",
                DreamRarity.LEGENDARY,
                Material.NETHERITE_PICKAXE,
                false
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return null;
    }
}
