package fr.openmc.core.features.dream.registries.items.consumable;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ChipsAywen extends DreamItem {
    public ChipsAywen() {
        super(new DreamItemMeta(
                "omc_dream:chips_aywen",
                "Chips goût Aywen",
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
