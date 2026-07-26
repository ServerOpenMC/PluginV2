package fr.openmc.core.features.dream.registries.items.armors.pyjama;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PyjamaChestplate extends DreamItem {
    public PyjamaChestplate() {
        super(new DreamItemMeta(
                "omc_dream:pyjama_chestplate",
                TranslationManager.translation("feature.dream.item.pyjama_chestplate.name"),
                DreamRarity.RARE,
                Material.LEATHER_CHESTPLATE,
                false
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return null;
    }
}
