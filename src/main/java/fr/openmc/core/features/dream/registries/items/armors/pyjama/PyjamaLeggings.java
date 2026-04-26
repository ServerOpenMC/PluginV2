package fr.openmc.core.features.dream.registries.items.armors.pyjama;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class PyjamaLeggings extends DreamItem {
    public PyjamaLeggings(String name) {
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
    public @NonNull ItemStack getVanilla() {
        ItemStack item = new ItemStack(Material.LEATHER_LEGGINGS);

        item.getItemMeta().itemName(Component.text("Jambières de Pyjama"));
        return item;
    }
}
