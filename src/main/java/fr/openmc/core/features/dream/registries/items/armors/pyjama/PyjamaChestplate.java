package fr.openmc.core.features.dream.registries.items.armors.pyjama;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class PyjamaChestplate extends DreamItem {
    public PyjamaChestplate() {
        super(new DreamItemMeta(
                "omc_dream:pyjama_chestplate",
                "Plastron de Pyjama",
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
