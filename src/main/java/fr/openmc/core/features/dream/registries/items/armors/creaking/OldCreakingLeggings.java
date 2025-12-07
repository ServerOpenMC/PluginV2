package fr.openmc.core.features.dream.registries.items.armors.creaking;

import fr.openmc.core.features.dream.models.registry.items.DreamEquipableItem;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class OldCreakingLeggings extends DreamEquipableItem {
    public OldCreakingLeggings(String name) {
        super(name);
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
    public DreamRarity getRarity() {
        return DreamRarity.COMMON;
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
        ItemStack item = new ItemStack(Material.IRON_LEGGINGS);

        item.getItemMeta().itemName(Component.text("Jambières du Vieux Creaking"));
        return item;
    }
}
