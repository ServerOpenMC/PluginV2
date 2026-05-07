package fr.openmc.core.features.dream.registries.items.blocks;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class CraftingTable extends DreamItem {
    public CraftingTable() {
        super(new DreamItemMeta(
                "omc_dream:crafting_table",
                "Table de Craft",
                DreamRarity.COMMON,
                Material.CRAFTING_TABLE,
                false
        ));
    }

    @Override
    public DreamRarity getRarity() {
        return getMeta().getRarity();
    }

    @Override
    public boolean isTransferable() {
        return getMeta().getTransferable();
    }

    @Override
    public ItemStack getTransferableItem() {
        return null;
    }

    @Override
    public @NonNull ItemStack getVanilla() {
        ItemStack item = new ItemStack(getMeta().getDefaultMaterial());

        item.getItemMeta().itemName(Component.text(getMeta().getName()));
        return item;
    }
}
