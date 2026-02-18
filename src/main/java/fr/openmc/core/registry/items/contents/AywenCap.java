package fr.openmc.core.registry.items.contents;

import fr.openmc.core.registry.items.options.EquipableItem;
import fr.openmc.core.registry.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Set;

public class AywenCap extends CustomItem implements EquipableItem {
    public AywenCap(String name) {
        super(name);
    }

    @Override
    public HashMap<PotionEffectType, Integer> getEffects() {
        return new HashMap<>(){{
            put(PotionEffectType.NIGHT_VISION, 0);
        }};
    }

    @Override
    public ItemStack getVanilla() {
        return new ItemStack(Material.IRON_HELMET);
    }
}
