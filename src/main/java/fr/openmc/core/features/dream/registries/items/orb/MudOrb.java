package fr.openmc.core.features.dream.registries.items.orb;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class MudOrb extends DreamItem {
    public MudOrb() {
        super(new DreamItemMeta(
                "omc_dream:mud_orb",
                "Orbe de Boue",
                DreamRarity.ONIRISIME,
                Material.HEART_OF_THE_SEA,
                true
        ));
    }

    @Override
    public DreamRarity getRarity() {
        return getMeta().getRarity();
    }

    @Override
    public boolean isTransferable() {
        return getMeta().getTransferable();
    }

    @Override
    public ItemStack getTransferableItem() {
        return this.getBest();
    }

    @Override
    public @NonNull ItemStack getVanilla() {
        ItemStack item = new ItemStack(getMeta().getDefaultMaterial());

        item.getItemMeta().itemName(Component.text(getMeta().getName()));
        return item;
    }
}
