package fr.openmc.core.features.dream.registries.items.blocks;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class OldPaleOakWood extends DreamItem {
    public OldPaleOakWood() {
        super(new DreamItemMeta(
                "omc_dream:old_pale_oak",
                "Vieux chêne pale",
                DreamRarity.COMMON,
                Material.PALE_OAK_WOOD,
                true
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return new ItemStack(Material.PALE_OAK_WOOD);
    }
}
