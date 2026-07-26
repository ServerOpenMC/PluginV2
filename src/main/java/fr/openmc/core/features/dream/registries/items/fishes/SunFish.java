package fr.openmc.core.features.dream.registries.items.fishes;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SunFish extends DreamItem {
    public SunFish() {
        super(new DreamItemMeta(
                "omc_dream:sun_fish",
                TranslationManager.translation("feature.dream.item.sun_fish.name"),
                DreamRarity.RARE,
                Material.COOKED_SALMON,
                true
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return this.getBest();
    }
}
