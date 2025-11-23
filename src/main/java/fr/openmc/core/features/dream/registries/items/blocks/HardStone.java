package fr.openmc.core.features.dream.registries.items.blocks;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class HardStone extends DreamItem {
    public HardStone(String name) {
        super(name);
    }

    @Override
    public DreamRarity getRarity() {
        return DreamRarity.COMMON;
    }

    @Override
    public boolean isTransferable() {
        return true;
    }

    @Override
    public ItemStack getTransferableItem() {
        return new ItemStack(Material.DEEPSLATE);
    }

    @Override
    public ItemStack getVanilla() {
        ItemStack item = new ItemStack(Material.DEEPSLATE);

        item.getItemMeta().itemName(Component.text("Pierre Dure"));
        return item;
    }
}
