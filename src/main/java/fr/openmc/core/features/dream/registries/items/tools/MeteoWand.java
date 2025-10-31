package fr.openmc.core.features.dream.registries.items.tools;

import fr.openmc.core.features.dream.models.registry.DreamItem;
import fr.openmc.core.features.dream.models.registry.DreamRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MeteoWand extends DreamItem {
    public MeteoWand(String name) {
        super(name);
    }

    @Override
    public DreamRarity getRarity() {
        return DreamRarity.LEGENDARY;
    }

    @Override
    public boolean isTransferable() {
        return true;
    }

    @Override
    public ItemStack getTransferableItem() {
        return this.getItemsAdder();
    }

    @Override
    public ItemStack getVanilla() {
        ItemStack item = new ItemStack(Material.STICK);

        item.getItemMeta().displayName(Component.text("Meteo Wand"));
        return item;
    }
}
