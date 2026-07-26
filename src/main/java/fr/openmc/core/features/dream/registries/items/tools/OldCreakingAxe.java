package fr.openmc.core.features.dream.registries.items.tools;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class OldCreakingAxe extends DreamItem {
    public OldCreakingAxe() {
        super(new DreamItemMeta(
                "omc_dream:old_creaking_axe",
                TranslationManager.translation("feature.dream.item.old_creaking_axe.name"),
                DreamRarity.COMMON,
                Material.STONE_AXE,
                true
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return this.getBest();
    }
}
