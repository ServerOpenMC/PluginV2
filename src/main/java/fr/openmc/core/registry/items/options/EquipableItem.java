package fr.openmc.core.registry.items.options;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.Set;

public interface EquipableItem {
    Map<PotionEffectType, Integer> getEffects();

    default void removeEffects(Player player) {
        for (PotionEffectType type : getEffects().keySet()) {
            player.removePotionEffect(type);
        }
    }
}
