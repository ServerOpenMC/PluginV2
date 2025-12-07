package fr.openmc.core.features.dream.registries.items.armors.cold;

import fr.openmc.core.features.dream.models.registry.items.DreamEquipableItem;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ColdBoots extends DreamEquipableItem {
    public ColdBoots(String name) {
        super(name);
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
        return DreamRarity.LEGENDARY;
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
        ItemStack item = new ItemStack(Material.DIAMOND_BOOTS);

        item.getItemMeta().itemName(Component.text("Bottes Glacées"));
        return item;
    }
}
