package fr.openmc.core.features.dream.registries.items.blocks;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CorruptedSculk extends DreamItem {
    public CorruptedSculk() {
        super(new DreamItemMeta(
                "omc_dream:corrupted_sculk",
                "Sculk Corrompu",
                DreamRarity.COMMON,
                Material.SCULK,
                true
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return new ItemStack(Material.SCULK);
    }
}
