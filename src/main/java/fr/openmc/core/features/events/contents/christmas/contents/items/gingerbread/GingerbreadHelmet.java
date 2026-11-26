package fr.openmc.core.features.events.contents.christmas.contents.items.gingerbread;

import fr.openmc.core.features.events.contents.christmas.mecanism.dimension.ChristmasDimensionManager;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.items.options.ActivableInWorld;
import fr.openmc.core.registry.items.options.EquipableItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public class GingerbreadHelmet extends CustomItem implements EquipableItem, ActivableInWorld {
    public GingerbreadHelmet(String name) {
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

    @Override
    public String inWorld() {
        return ChristmasDimensionManager.DIMENSION_NAME;
    }
}
