package fr.openmc.core.features.dream.registries.items.orb;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class SoulOrb extends DreamItem {
    public SoulOrb() {
        super(new DreamItemMeta(
                "omc_dream:ame_orb",
                "Orbe des Ames",
                DreamRarity.ONIRISIME,
                Material.HEART_OF_THE_SEA,
                true
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return this.getBest();
    }
}
