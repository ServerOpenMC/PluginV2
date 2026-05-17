package fr.openmc.core.features.dream.registries.items.armors.pyjama;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class PyjamaBoots extends DreamItem {
    public PyjamaBoots() {
        super(new DreamItemMeta(
                "omc_dream:pyjama_boots",
                "Bottes de Pyjama",
                DreamRarity.RARE,
                Material.LEATHER_BOOTS,
                false
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return null;
    }
}
