package fr.openmc.core.features.dream.registries.items.tools;

import fr.openmc.core.features.dream.models.registry.DreamItem;
import fr.openmc.core.features.dream.models.registry.DreamRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CloudFishingRod extends DreamItem {
    public CloudFishingRod(String name) {
        super(name);
    }

    @Override
    public DreamRarity getRarity() {
        return DreamRarity.EPIC;
    }

    @Override
    public boolean isTransferable() {
        return false;
    }

    @Override
    public ItemStack getTransferableItem() {
        return null;
    }

    @Override
    public ItemStack getVanilla() {
        ItemStack item = new ItemStack(Material.FISHING_ROD);

        item.getItemMeta().displayName(Component.text("Canne à pêche nuageuse"));
        return item;
    }
}
