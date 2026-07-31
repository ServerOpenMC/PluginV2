package fr.openmc.core.features.dream.registries.items.consumable;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ChipsJimmy extends DreamItem {
    public ChipsJimmy() {
        super(new DreamItemMeta(
                "omc_dream:chips_jimmy",
                TranslationManager.translation("feature.dream.item.chips_jimmy.name"),
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
