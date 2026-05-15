package fr.openmc.core.features.dream.registries.items.consumable;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class Somnifere extends DreamItem {
    public Somnifere() {
        super(new DreamItemMeta(
                "omc_dream:somnifere",
                "Somnifère",
                DreamRarity.RARE,
                Material.POTION,
                true
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return this.getBest();
    }
}
