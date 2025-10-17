package fr.openmc.core.features.dream.items.armors.soul;

import fr.openmc.core.features.dream.items.DreamItem;
import fr.openmc.core.features.dream.items.DreamRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SoulChestplate extends DreamItem {
    public SoulChestplate(String name) {
        super(name);
    }

    @Override
    public DreamRarity getRarity() {
        return DreamRarity.RARE;
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
        ItemStack item = new ItemStack(Material.IRON_CHESTPLATE);

        item.getItemMeta().displayName(Component.text("Plastron des Ames"));
        return item;
    }
}
