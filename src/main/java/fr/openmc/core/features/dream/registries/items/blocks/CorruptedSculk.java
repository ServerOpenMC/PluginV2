package fr.openmc.core.features.dream.registries.items.blocks;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CorruptedSculk extends DreamItem {
    public CorruptedSculk(String name) {
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
        return new ItemStack(Material.SCULK);
    }

    @Override
    public ItemStack getVanilla() {
        ItemStack item = new ItemStack(Material.SCULK);

        item.getItemMeta().itemName(Component.text("Sculk Corrompu"));
        return item;
    }
}
