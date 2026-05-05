package fr.openmc.core.features.dream.registries.items.consumable;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class ChipsLait2Margouta extends DreamItem {
    public ChipsLait2Margouta(String name) {
        super(name);
    }
    
    @Override
    public DreamRarity getRarity() {
        return DreamRarity.ONIRISIME;
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
    public @NonNull ItemStack getVanilla() {
        ItemStack item = new ItemStack(Material.DRIED_KELP);

        item.getItemMeta().itemName(Component.text("Chips goût Lait de Margouta"));
        return item;
    }
}
