package fr.openmc.core.features.dream.registries.items.armors.cloud;

import fr.openmc.core.features.dream.models.registry.DreamItem;
import fr.openmc.core.features.dream.models.registry.DreamRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CloudLeggings extends DreamItem {
    public CloudLeggings(String name) {
        super(name);
    }

    @Override
    public DreamRarity getRarity() {
        return DreamRarity.EPIC;
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
        ItemStack item = new ItemStack(Material.IRON_LEGGINGS);

        item.getItemMeta().displayName(Component.text("Jambière des Nuages"));
        return item;
    }
}
