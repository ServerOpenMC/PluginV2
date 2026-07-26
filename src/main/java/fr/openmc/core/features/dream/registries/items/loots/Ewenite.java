package fr.openmc.core.features.dream.registries.items.loots;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Ewenite extends DreamItem {
    public Ewenite() {
        super(new DreamItemMeta(
                "omc_dream:ewenite",
                TranslationManager.translation("feature.dream.item.ewenite.name"),
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
