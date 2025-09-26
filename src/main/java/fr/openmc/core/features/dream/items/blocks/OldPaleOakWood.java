package fr.openmc.core.features.dream.items.blocks;

import fr.openmc.core.features.dream.items.DreamItem;
import fr.openmc.core.features.dream.items.DreamRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class OldPaleOakWood extends DreamItem {
    public OldPaleOakWood(String name) {
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
        return new ItemStack(Material.PALE_OAK_WOOD);
    }

    @Override
    public ItemStack getVanilla() {
        ItemStack item = new ItemStack(Material.PALE_OAK_WOOD);

        item.getItemMeta().displayName(Component.text("Vieux Chêne Pale"));
        return item;
    }
}
