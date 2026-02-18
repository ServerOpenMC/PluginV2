package fr.openmc.core.features.dream.registries.items.fishes;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class CokkedPoissonion extends DreamItem {
    public CokkedPoissonion(String name) {
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
    public @NonNull ItemStack getVanilla() {
        ItemStack item = new ItemStack(Material.PUFFERFISH);

        item.getItemMeta().itemName(Component.text("Poissonion Cuit"));
        return item;
    }
}
