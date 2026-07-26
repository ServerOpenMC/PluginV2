package fr.openmc.core.features.dream.registries.items.blocks;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CraftingTable extends DreamItem {
    public CraftingTable() {
        super(new DreamItemMeta(
                "omc_dream:crafting_table",
                TranslationManager.translation("feature.dream.item.crafting_table.name"),
                DreamRarity.COMMON,
                Material.CRAFTING_TABLE,
                false
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return null;
    }
}
