package fr.openmc.core.features.dream.registries.items.armors.dream;

import fr.openmc.core.features.dream.models.registry.items.DreamEquipableItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import fr.openmc.core.registry.items.options.EquipableItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public class DreamLeggings extends DreamItem implements DreamEquipableItem, EquipableItem {
    public DreamLeggings() {
        super(new DreamItemMeta(
                "omc_dream:dream_leggings",
                "Jambières Oniriques",
                DreamRarity.ONIRISIME,
                Material.LEATHER_LEGGINGS,
                true
        ));
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
    public ItemStack getTransferableItem() {
        return this.getBestTransferable();
    }

    @Override
    public HashMap<PotionEffectType, Integer> getEffects() {
        return new HashMap<>(){{
            put(PotionEffectType.SPEED, 0);
        }};
    }
}
