package fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight;

import fr.openmc.core.utils.bukkit.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameRules;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;

public class BloodyNightManager {
    private static final AttributeModifier BLOODY_HEALTH_MODIFIER = new AttributeModifier(
            new NamespacedKey("omc_daily_events", "bloody_health"),
            0.75,
            AttributeModifier.Operation.ADD_SCALAR
    );
    private static final AttributeModifier BLOODY_ATTACK_MODIFIER = new AttributeModifier(
            new NamespacedKey("omc_daily_events", "bloody_attack"),
            0.10,
            AttributeModifier.Operation.ADD_SCALAR
    );
    private static final AttributeModifier BLOODY_SPEED_MODIFIER = new AttributeModifier(
            new NamespacedKey("omc_daily_events", "bloody_speed"),
            0.10,
            AttributeModifier.Operation.ADD_SCALAR
    );
    private static final AttributeModifier BLOODY_FOLLOW_MODIFIER = new AttributeModifier(
            new NamespacedKey("omc_daily_events", "bloody_follow"),
            0.10,
            AttributeModifier.Operation.ADD_SCALAR
    );

    public static void start(BloodyNightEvent event) {
        World world = Bukkit.getWorld(event.getWorldEvent());
        if (world == null) return;

        boostMonsters(world);
        world.setGameRule(GameRules.NATURAL_HEALTH_REGENERATION, false);
    }

    public static void stop(BloodyNightEvent event) {
        World world = Bukkit.getWorld(event.getWorldEvent());
        if (world == null) return;

        desactivateBloodyMonsters(world);
        world.setGameRule(GameRules.NATURAL_HEALTH_REGENERATION, true);
    }

    /**
     * Boost touts les monstres présent dans le monde
     * @param world le monde où se passe la blood moon
     */
    private static void boostMonsters(World world) {
        for (Entity entity : world.getEntities()) {
            if (!(entity instanceof Monster monster)) continue;

            boostMonster(monster);
        }
    }

    /**
     * Boost le monstre
     * - Vie x1.75
     * - Dégâts +10 %
     * - Vitesse +10 %
     * - Détection +30 %
     * @param entity l'entité à boost
     */
    public static void boostMonster(LivingEntity entity) {
        EntityUtils.addModifierIfPresent(entity, Attribute.MAX_HEALTH, BLOODY_HEALTH_MODIFIER);
        EntityUtils.addModifierIfPresent(entity, Attribute.ATTACK_DAMAGE, BLOODY_ATTACK_MODIFIER);
        EntityUtils.addModifierIfPresent(entity, Attribute.MOVEMENT_SPEED, BLOODY_SPEED_MODIFIER);
        EntityUtils.addModifierIfPresent(entity, Attribute.FOLLOW_RANGE, BLOODY_FOLLOW_MODIFIER);
    }

    private static void desactivateBloodyMonsters(World world) {
        for (Entity entity : world.getEntities()) {
            if (!(entity instanceof Monster monster)) continue;

            EntityUtils.removeModifierIfPresent(monster, Attribute.MAX_HEALTH, BLOODY_HEALTH_MODIFIER);
            EntityUtils.removeModifierIfPresent(monster, Attribute.ATTACK_DAMAGE, BLOODY_ATTACK_MODIFIER);
            EntityUtils.removeModifierIfPresent(monster, Attribute.MOVEMENT_SPEED, BLOODY_SPEED_MODIFIER);
            EntityUtils.removeModifierIfPresent(monster, Attribute.FOLLOW_RANGE, BLOODY_FOLLOW_MODIFIER);
        }
    }
}
