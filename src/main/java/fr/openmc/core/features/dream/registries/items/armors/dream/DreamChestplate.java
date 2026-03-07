package fr.openmc.core.features.dream.registries.items.armors.dream;

import fr.openmc.core.features.dream.models.registry.items.DreamEquipableItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import fr.openmc.core.registry.items.options.EquipableItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Set;

public class DreamChestplate extends DreamItem implements DreamEquipableItem, EquipableItem {
    public DreamChestplate(String name) {
        super(name);
    }

    @Override
    public long getAdditionalMaxTime() {
        return 120;
    }

    @Override
    public Integer getColdResistance() {
        return 2;
    }

    @Override
    public DreamRarity getRarity() {
        return DreamRarity.ONIRISIME;
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
        ItemStack item = new ItemStack(Material.NETHERITE_CHESTPLATE);

        item.getItemMeta().itemName(Component.text("Plastron Onirique"));
        return item;
    }

    @Override
    public HashMap<PotionEffectType, Integer> getEffects() {
        return new HashMap<>(){{
            put(PotionEffectType.HASTE, 0);
        }};
    }
}
