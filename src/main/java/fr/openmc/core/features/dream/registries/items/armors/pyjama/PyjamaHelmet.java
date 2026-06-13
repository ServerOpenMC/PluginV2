package fr.openmc.core.features.dream.registries.items.armors.pyjama;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PyjamaHelmet extends DreamItem {
    public PyjamaHelmet() {
        super(new DreamItemMeta(
                "omc_dream:pyjama_helmet",
                "Casque de Pyjama",
                DreamRarity.RARE,
                Material.LEATHER_HELMET,
                false
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return null;
    }
}
