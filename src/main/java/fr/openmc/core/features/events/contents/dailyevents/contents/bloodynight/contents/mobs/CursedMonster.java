package fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.contents.mobs;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.mobs.CustomMob;
import fr.openmc.core.utils.bukkit.EntityUtils;
import fr.openmc.core.utils.nms.entity.EntityGlowNMS;
import net.minecraft.ChatFormatting;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Monster;

public class CursedMonster extends CustomMob<Monster> {
    private static final AttributeModifier HEALTH_MODIFIER = new AttributeModifier(
            new NamespacedKey("omc_daily_events", "cursed_health"),
            1.75,
            AttributeModifier.Operation.ADD_SCALAR
    );

    private static final AttributeModifier ATTACK_MODIFIER = new AttributeModifier(
            new NamespacedKey("omc_daily_events", "cursed_attack"),
            0.25,
            AttributeModifier.Operation.ADD_SCALAR
    );

    private static final AttributeModifier SPEED_MODIFIER = new AttributeModifier(
            new NamespacedKey("omc_daily_events", "cursed_speed"),
            -0.15,
            AttributeModifier.Operation.ADD_SCALAR
    );

    private static final AttributeModifier FOLLOW_MODIFIER = new AttributeModifier(
            new NamespacedKey("omc_daily_events", "cursed_follow"),
            0.20,
            AttributeModifier.Operation.ADD_SCALAR
    );

    private static final AttributeModifier SCALE_MODIFIER = new AttributeModifier(
            new NamespacedKey("omc_daily_events", "cursed_scale"),
            0.40,
            AttributeModifier.Operation.ADD_SCALAR
    );

    public CursedMonster(String id) {
        super(id,
                "Cursed",
                Monster.class,
                1,
                1,
                OMCRegistry.CUSTOM_LOOT_TABLES.BLOODY_MOB.rollLoots()
        );
    }

    @Override
    public void apply(Monster entity) {
        registerAsCustomMob(entity);

        entity.setCustomNameVisible(false);

        EntityUtils.addModifierIfPresent(entity, Attribute.MAX_HEALTH, HEALTH_MODIFIER);
        EntityUtils.addModifierIfPresent(entity, Attribute.ATTACK_DAMAGE, ATTACK_MODIFIER);
        EntityUtils.addModifierIfPresent(entity, Attribute.MOVEMENT_SPEED, SPEED_MODIFIER);
        EntityUtils.addModifierIfPresent(entity, Attribute.FOLLOW_RANGE, FOLLOW_MODIFIER);
        EntityUtils.addModifierIfPresent(entity, Attribute.SCALE, SCALE_MODIFIER);

        EntityGlowNMS.setGlowingColor(entity, ChatFormatting.DARK_PURPLE);
    }

    public void resetToDefault(Monster entity) {
        unregisterAsCustomMob(entity);

        entity.setCustomNameVisible(true);

        EntityUtils.removeModifierIfPresent(entity, Attribute.MAX_HEALTH, HEALTH_MODIFIER);
        EntityUtils.removeModifierIfPresent(entity, Attribute.ATTACK_DAMAGE, ATTACK_MODIFIER);
        EntityUtils.removeModifierIfPresent(entity, Attribute.MOVEMENT_SPEED, SPEED_MODIFIER);
        EntityUtils.removeModifierIfPresent(entity, Attribute.FOLLOW_RANGE, FOLLOW_MODIFIER);
        EntityUtils.removeModifierIfPresent(entity, Attribute.SCALE, SCALE_MODIFIER);

        EntityGlowNMS.removeGlowing(entity);
    }
}
