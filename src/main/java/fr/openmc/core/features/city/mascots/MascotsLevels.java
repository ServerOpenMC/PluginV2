package fr.openmc.core.features.city.mascots;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

@Getter
public enum MascotsLevels {
    level1(300, 1, Map.of(Material.DIAMOND, 5, Material.IRON_INGOT, 10), new PotionEffect[]{
            new PotionEffect(PotionEffectType.SLOWNESS, PotionEffect.INFINITE_DURATION, 0)
    }),

    level2(600, 2, Map.of(Material.DIAMOND, 5, Material.IRON_INGOT, 10), new PotionEffect[]{
            new PotionEffect(PotionEffectType.SLOWNESS, PotionEffect.INFINITE_DURATION, 1),
    }),
    level3(900, 3, Map.of(Material.DIAMOND, 10, Material.IRON_INGOT, 20), new PotionEffect[]{
            new PotionEffect(PotionEffectType.SLOWNESS, PotionEffect.INFINITE_DURATION, 1),
            new PotionEffect(PotionEffectType.WEAKNESS, PotionEffect.INFINITE_DURATION, 0)
    }),

    level4(1200, 4, Map.of(Material.DIAMOND, 15, Material.GOLD_INGOT, 10), new PotionEffect[]{
            new PotionEffect(PotionEffectType.SLOWNESS, PotionEffect.INFINITE_DURATION, 1),
            new PotionEffect(PotionEffectType.WEAKNESS, PotionEffect.INFINITE_DURATION, 1)
    }),

    level5(1500, 5, Map.of(Material.DIAMOND, 20, Material.GOLD_INGOT, 15), new PotionEffect[]{
            new PotionEffect(PotionEffectType.SLOWNESS, PotionEffect.INFINITE_DURATION, 1),
            new PotionEffect(PotionEffectType.WEAKNESS, PotionEffect.INFINITE_DURATION, 2)
    }),

    level6(1800, 6, Map.of(Material.DIAMOND, 25, Material.NETHERITE_INGOT, 1), new PotionEffect[]{
            new PotionEffect(PotionEffectType.SLOWNESS, PotionEffect.INFINITE_DURATION, 1),
            new PotionEffect(PotionEffectType.WEAKNESS, PotionEffect.INFINITE_DURATION, 2),
            new PotionEffect(PotionEffectType.HUNGER, PotionEffect.INFINITE_DURATION, 0)
    }),

    level7(2100, 7, Map.of(Material.DIAMOND, 30, Material.NETHERITE_INGOT, 2), new PotionEffect[]{
            new PotionEffect(PotionEffectType.SLOWNESS, PotionEffect.INFINITE_DURATION, 1),
            new PotionEffect(PotionEffectType.WEAKNESS, PotionEffect.INFINITE_DURATION, 2),
            new PotionEffect(PotionEffectType.HUNGER, PotionEffect.INFINITE_DURATION, 1)
    }),

    level8(2400, 10, Map.of(Material.DIAMOND, 40, Material.NETHERITE_INGOT, 3), new PotionEffect[]{
            new PotionEffect(PotionEffectType.SLOWNESS, PotionEffect.INFINITE_DURATION, 1),
            new PotionEffect(PotionEffectType.WEAKNESS, PotionEffect.INFINITE_DURATION, 2),
            new PotionEffect(PotionEffectType.HUNGER, PotionEffect.INFINITE_DURATION, 1)
    }),
    level9(2700, 15, Map.of(Material.DIAMOND, 50, Material.NETHERITE_INGOT, 4), new PotionEffect[]{
            new PotionEffect(PotionEffectType.SLOWNESS, PotionEffect.INFINITE_DURATION, 1),
            new PotionEffect(PotionEffectType.WEAKNESS, PotionEffect.INFINITE_DURATION, 2),
            new PotionEffect(PotionEffectType.HUNGER, PotionEffect.INFINITE_DURATION, 1),
            new PotionEffect(PotionEffectType.MINING_FATIGUE, PotionEffect.INFINITE_DURATION, 0)
    }),

    level10(3000, 20, Map.of(Material.DIAMOND, 64, Material.NETHERITE_BLOCK, 1), new PotionEffect[]{
            new PotionEffect(PotionEffectType.SLOWNESS, PotionEffect.INFINITE_DURATION, 2),
            new PotionEffect(PotionEffectType.WEAKNESS, PotionEffect.INFINITE_DURATION, 2),
            new PotionEffect(PotionEffectType.HUNGER, PotionEffect.INFINITE_DURATION, 1),
            new PotionEffect(PotionEffectType.MINING_FATIGUE, PotionEffect.INFINITE_DURATION, 1),
            new PotionEffect(PotionEffectType.DARKNESS, PotionEffect.INFINITE_DURATION, 0)
    }),;

    private final int health;
    private final int upgradeCost;
    private final Map<Material, Integer> requiredItems;
    private final PotionEffect[] malus;

    MascotsLevels(int health, int upgradeCost, Map<Material, Integer> requiredItems, PotionEffect[] malus) {
        this.health = health;
        this.upgradeCost = upgradeCost;
        this.requiredItems = requiredItems;
        this.malus = malus;
    }
}
