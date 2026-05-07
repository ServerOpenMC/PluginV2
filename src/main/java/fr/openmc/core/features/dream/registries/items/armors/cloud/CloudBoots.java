package fr.openmc.core.features.dream.registries.items.armors.cloud;

import fr.openmc.core.features.dream.models.registry.items.DreamEquipableItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CloudBoots extends DreamItem implements DreamEquipableItem {
    public CloudBoots() {
        super(new DreamItemMeta(
                "omc_dream:cloud_boots",
                "Bottes des Nuages",
                DreamRarity.EPIC,
                Material.LEATHER_BOOTS,
                true
        ));
    }

    @Override
    public long getAdditionalMaxTime() {
        return 30;
    }

    @Override
    public Integer getColdResistance() {
        return null;
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
        return this.getBestTransferable();
    }

    @Override
    public @NonNull ItemStack getVanilla() {
        ItemStack item = new ItemStack(getMeta().getDefaultMaterial());
        item.getItemMeta().itemName(Component.text(getMeta().getName()));
        return item;
    }
}
