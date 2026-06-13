package fr.openmc.core.features.dream.registries.items.consumable;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ChipsTerre extends DreamItem {
    public ChipsTerre() {
        super(new DreamItemMeta(
                "omc_dream:chips_terre",
                "Chips goût Terre",
                DreamRarity.EPIC,
                Material.PAPER,
                true
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return this.getBest();
    }
}
