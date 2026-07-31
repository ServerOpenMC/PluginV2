package fr.openmc.core.features.dream.registries.items.consumable;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ChipsDihydrogene extends DreamItem {
    public ChipsDihydrogene() {
        super(new DreamItemMeta(
                "omc_dream:chips_dihydrogene",
                TranslationManager.translation("feature.dream.item.chips_dihydrogene.name"),
                DreamRarity.EPIC,
                Material.PAPER,
                true
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return this.getBest();
    }
}
