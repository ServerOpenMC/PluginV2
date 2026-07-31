package fr.openmc.core.features.dream.registries.items.loots;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CorruptedString extends DreamItem {
    public CorruptedString() {
        super(new DreamItemMeta(
                "omc_dream:corrupted_string",
                TranslationManager.translation("feature.dream.item.corrupted_string.name"),
                DreamRarity.COMMON,
                Material.STRING,
                false
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return null;
    }
}
