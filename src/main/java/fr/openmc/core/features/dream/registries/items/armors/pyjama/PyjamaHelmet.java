package fr.openmc.core.features.dream.registries.items.armors.pyjama;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PyjamaHelmet extends DreamItem {
    public PyjamaHelmet(String name) {
        super(name);
    }

    @Override
    public DreamRarity getRarity() {
        return DreamRarity.RARE;
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
        ItemStack item = new ItemStack(Material.LEATHER_HELMET);

        item.getItemMeta().itemName(Component.text("Casque de Pyjama"));
        return item;
    }
}
