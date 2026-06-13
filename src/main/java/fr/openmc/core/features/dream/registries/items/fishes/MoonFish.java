package fr.openmc.core.features.dream.registries.items.fishes;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MoonFish extends DreamItem {
    public MoonFish() {
        super(new DreamItemMeta(
                "omc_dream:moon_fish",
                "Poisson-lune",
                DreamRarity.RARE,
                Material.SALMON,
                true
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return this.getBest();
    }
}
