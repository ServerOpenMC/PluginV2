package fr.openmc.core.features.dream.registries.items.loots;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class Soul extends DreamItem {
    public Soul() {
        super(new DreamItemMeta(
                "omc_dream:soul",
                "Âme",
                DreamRarity.RARE,
                Material.PAPER,
                false
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return null;
    }
}
