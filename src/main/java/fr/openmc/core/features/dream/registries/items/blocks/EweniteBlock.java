package fr.openmc.core.features.dream.registries.items.blocks;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class EweniteBlock extends DreamItem {
    public EweniteBlock() {
        super(new DreamItemMeta(
                "omc_dream:ewenite_block",
                "Bloc d'ewenite",
                DreamRarity.ONIRISIME,
                Material.NETHERITE_BLOCK,
                false
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return new ItemStack(Material.SCULK);
    }
}
