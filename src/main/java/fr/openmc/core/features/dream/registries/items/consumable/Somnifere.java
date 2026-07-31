package fr.openmc.core.features.dream.registries.items.consumable;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Somnifere extends DreamItem {
    public Somnifere() {
        super(new DreamItemMeta(
                "omc_dream:somnifere",
                TranslationManager.translation("feature.dream.item.somnifere.name"),
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
