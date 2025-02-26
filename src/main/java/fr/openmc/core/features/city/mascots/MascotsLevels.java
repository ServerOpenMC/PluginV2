package fr.openmc.core.features.city.mascots;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

@Getter
public enum MascotsLevels {
    level1(300, 1, Map.of(Material.DIAMOND, 5, Material.IRON_INGOT, 10), new PotionEffect[]{
            invisibleEffect(PotionEffectType.SLOWNESS, 0)
    }, new PotionEffect[0]),

    level2(600, 2, Map.of(Material.DIAMOND, 5, Material.IRON_INGOT, 10), new PotionEffect[]{
            invisibleEffect(PotionEffectType.SLOWNESS, 1),
    }, new PotionEffect[0]),

    level3(900, 3, Map.of(Material.DIAMOND, 10, Material.IRON_INGOT, 20), new PotionEffect[]{
            invisibleEffect(PotionEffectType.SLOWNESS, 1),
            invisibleEffect(PotionEffectType.WEAKNESS, 0)
    }, new PotionEffect[0]),

    level4(1200, 4, Map.of(Material.DIAMOND, 15, Material.GOLD_INGOT, 10), new PotionEffect[]{
            invisibleEffect(PotionEffectType.SLOWNESS, 1),
            invisibleEffect(PotionEffectType.WEAKNESS, 1)
    }, new PotionEffect[]{invisibleEffect(PotionEffectType.REGENERATION, 0)}),

    level5(1500, 5, Map.of(Material.DIAMOND, 20, Material.GOLD_INGOT, 15), new PotionEffect[]{
            invisibleEffect(PotionEffectType.SLOWNESS, 1),
            invisibleEffect(PotionEffectType.WEAKNESS, 2)
    }, new PotionEffect[]{invisibleEffect(PotionEffectType.REGENERATION, 0)}),

    level6(1800, 7, Map.of(Material.DIAMOND, 25, Material.NETHERITE_INGOT, 1), new PotionEffect[]{
            invisibleEffect(PotionEffectType.SLOWNESS, 1),
            invisibleEffect(PotionEffectType.WEAKNESS, 2),
            invisibleEffect(PotionEffectType.HUNGER, 0)
    }, new PotionEffect[]{
            invisibleEffect(PotionEffectType.REGENERATION, 1),
    }),

    level7(2100, 10, Map.of(Material.DIAMOND, 30, Material.NETHERITE_INGOT, 2), new PotionEffect[]{
            invisibleEffect(PotionEffectType.SLOWNESS, 1),
            invisibleEffect(PotionEffectType.WEAKNESS, 2),
            invisibleEffect(PotionEffectType.HUNGER, 1)
    }, new PotionEffect[]{
            invisibleEffect(PotionEffectType.REGENERATION, 1),
            invisibleEffect(PotionEffectType.HASTE, 0),
    }),

    level8(2400, 15, Map.of(Material.DIAMOND, 40, Material.NETHERITE_INGOT, 3), new PotionEffect[]{
            invisibleEffect(PotionEffectType.SLOWNESS, 1),
            invisibleEffect(PotionEffectType.WEAKNESS, 2),
            invisibleEffect(PotionEffectType.HUNGER, 1)
    }, new PotionEffect[]{
            invisibleEffect(PotionEffectType.REGENERATION, 1),
            invisibleEffect(PotionEffectType.HASTE, 1),
    }),

    level9(2700, 20, Map.of(Material.DIAMOND, 50, Material.NETHERITE_INGOT, 4), new PotionEffect[]{
            invisibleEffect(PotionEffectType.SLOWNESS, 1),
            invisibleEffect(PotionEffectType.WEAKNESS, 2),
            invisibleEffect(PotionEffectType.HUNGER, 1),
            invisibleEffect(PotionEffectType.MINING_FATIGUE, 0)
    }, new PotionEffect[]{
            invisibleEffect(PotionEffectType.REGENERATION, 1),
            invisibleEffect(PotionEffectType.HASTE, 1),
            invisibleEffect(PotionEffectType.LUCK, 0),
    }),

    level10(3000, 0, Map.of(Material.DIAMOND, 64, Material.NETHERITE_BLOCK, 1), new PotionEffect[]{
            invisibleEffect(PotionEffectType.SLOWNESS, 2),
            invisibleEffect(PotionEffectType.WEAKNESS, 2),
            invisibleEffect(PotionEffectType.HUNGER, 1),
            invisibleEffect(PotionEffectType.MINING_FATIGUE, 1),
            invisibleEffect(PotionEffectType.DARKNESS, 0)
    }, new PotionEffect[]{
            invisibleEffect(PotionEffectType.REGENERATION, 1),
            invisibleEffect(PotionEffectType.HASTE, 1),
            invisibleEffect(PotionEffectType.LUCK, 0),
            invisibleEffect(PotionEffectType.STRENGTH, 0),
    });

    private final int health;
    private final int upgradeCost;
    private final Map<Material, Integer> requiredItems;
    private final PotionEffect[] malus;
    private final PotionEffect[] bonus;

    MascotsLevels(int health, int upgradeCost, Map<Material, Integer> requiredItems, PotionEffect[] malus, PotionEffect[] bonus) {
        this.health = health;
        this.upgradeCost = upgradeCost;
        this.requiredItems = requiredItems;
        this.malus = malus;
        this.bonus = bonus;
    }

    private static PotionEffect invisibleEffect(PotionEffectType type, int level) {
        return new PotionEffect(type, PotionEffect.INFINITE_DURATION, level, false, false, false);
    }
}
