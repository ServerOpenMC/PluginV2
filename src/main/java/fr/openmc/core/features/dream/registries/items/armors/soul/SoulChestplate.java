package fr.openmc.core.features.dream.registries.items.armors.soul;

import fr.openmc.core.features.dream.models.registry.items.DreamEquipableItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class SoulChestplate extends DreamItem implements DreamEquipableItem {
    public SoulChestplate() {
        super(new DreamItemMeta(
                "omc_dream:soul_chestplate",
                "Plastron des Âmes",
                DreamRarity.RARE,
                Material.LEATHER_CHESTPLATE,
                true
        ));
    }

    @Override
    public long getAdditionalMaxTime() {
        return 15;
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
