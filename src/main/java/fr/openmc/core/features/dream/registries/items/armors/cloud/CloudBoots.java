package fr.openmc.core.features.dream.registries.items.armors.cloud;

import fr.openmc.core.features.dream.models.registry.items.DreamEquipableItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CloudBoots extends DreamItem implements DreamEquipableItem {
    public CloudBoots(String name) {
        super(name);
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
        return DreamRarity.EPIC;
    }

    @Override
    public boolean isTransferable() {
        return true;
    }

    @Override
    public ItemStack getTransferableItem() {
        return this.getBestTransferable();
    }

    @Override
    public ItemStack getVanilla() {
        ItemStack item = new ItemStack(Material.IRON_BOOTS);

        item.getItemMeta().itemName(Component.text("Bottes des Nuages"));
        return item;
    }
}
