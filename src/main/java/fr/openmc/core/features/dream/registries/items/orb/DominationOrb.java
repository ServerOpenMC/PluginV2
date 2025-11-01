package fr.openmc.core.features.dream.registries.items.orb;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class DominationOrb extends DreamItem {
    /**
     * Creates a new DreamItem with the specified name.
     *
     * @param name The namespaced ID of the item, e.g., "omc_dream:orb".
     */
    public DominationOrb(String name) {
        super(name);
    }

    @Override
    public DreamRarity getRarity() {
        return DreamRarity.ONIRISIME;
    }

    @Override
    public boolean isTransferable() {
        return true;
    }

    @Override
    public ItemStack getTransferableItem() {
        return this.getBest();
    }

    @Override
    public ItemStack getVanilla() {
        return ItemStack.of(Material.HEART_OF_THE_SEA);
    }
}
