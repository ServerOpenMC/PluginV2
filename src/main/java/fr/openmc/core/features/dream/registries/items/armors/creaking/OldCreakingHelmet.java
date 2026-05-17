package fr.openmc.core.features.dream.registries.items.armors.creaking;

import fr.openmc.core.features.dream.models.registry.items.DreamEquipableItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class OldCreakingHelmet extends DreamItem implements DreamEquipableItem {
    public OldCreakingHelmet() {
        super(new DreamItemMeta(
                "omc_dream:old_creaking_helmet",
                "Vieux Casque de Creaking",
                DreamRarity.COMMON,
                Material.LEATHER_HELMET,
                true
        ));
    }

    @Override
    public long getAdditionalMaxTime() {
        return 5;
    }

    @Override
    public Integer getColdResistance() {
        return null;
    }

    @Override
    public ItemStack getTransferableItem() {
        return this.getBestTransferable();
    }
}
