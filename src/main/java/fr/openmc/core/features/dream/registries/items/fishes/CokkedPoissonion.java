package fr.openmc.core.features.dream.registries.items.fishes;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CokkedPoissonion extends DreamItem {
    public CokkedPoissonion() {
        super(new DreamItemMeta(
                "omc_dream:cooked_poissonion",
                TranslationManager.translation("feature.dream.item.cooked_poissonion.name"),
                DreamRarity.RARE,
                Material.COOKED_COD,
                true
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return this.getBest();
    }
}
