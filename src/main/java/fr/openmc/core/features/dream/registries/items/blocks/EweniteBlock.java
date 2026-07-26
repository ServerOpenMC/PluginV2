package fr.openmc.core.features.dream.registries.items.blocks;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class EweniteBlock extends DreamItem {
    public EweniteBlock() {
        super(new DreamItemMeta(
                "omc_dream:ewenite_block",
                TranslationManager.translation("feature.dream.item.ewenite_block.name"),
                DreamRarity.ONIRISIME,
                Material.NETHERITE_BLOCK,
                false
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return new ItemStack(Material.SCULK);
    }
}
