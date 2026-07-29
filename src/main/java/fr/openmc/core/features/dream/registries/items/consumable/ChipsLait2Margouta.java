package fr.openmc.core.features.dream.registries.items.consumable;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ChipsLait2Margouta extends DreamItem {
    public ChipsLait2Margouta() {
        super(new DreamItemMeta(
                "omc_dream:chips_lait_2_margouta",
                TranslationManager.translation("feature.dream.item.chips_lait_2_margouta.name"),
                DreamRarity.ONIRISIME,
                Material.PAPER,
                true
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return this.getBest();
    }
}
