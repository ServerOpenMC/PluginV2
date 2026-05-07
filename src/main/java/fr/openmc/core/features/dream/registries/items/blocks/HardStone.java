package fr.openmc.core.features.dream.registries.items.blocks;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class HardStone extends DreamItem {
    public HardStone() {
        super(new DreamItemMeta(
                "omc_dream:hard_stone",
                "Pierre dure",
                DreamRarity.COMMON,
                Material.DEEPSLATE,
                true
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return new ItemStack(Material.DEEPSLATE);
    }
}
