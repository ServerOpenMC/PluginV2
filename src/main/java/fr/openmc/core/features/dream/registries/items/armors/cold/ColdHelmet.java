package fr.openmc.core.features.dream.registries.items.armors.cold;

import fr.openmc.core.features.dream.models.registry.items.DreamEquipableItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class ColdHelmet extends DreamItem implements DreamEquipableItem {
    public ColdHelmet() {
        super(new DreamItemMeta(
                "omc_dream:cold_helmet",
                "Casque Glacé",
                DreamRarity.LEGENDARY,
                Material.LEATHER_HELMET,
                true
        ));
    }

    @Override
    public long getAdditionalMaxTime() {
        return 60;
    }

    @Override
    public Integer getColdResistance() {
        return 1;
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
