package fr.openmc.core.features.dream.registries.items.loots;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Glacite extends DreamItem {
    public Glacite() {
        super(new DreamItemMeta(
                "omc_dream:glacite",
                TranslationManager.translation("feature.dream.item.glacite.name"),
                DreamRarity.EPIC,
                Material.BLUE_ICE,
                false
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return null;
    }
}
