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

public class AncientMonster extends CustomMob<Monster> {
    private static final AttributeModifier HEALTH_MODIFIER = new AttributeModifier(
            new NamespacedKey("omc_daily_events", "ancient_health"),
            3.00,
            AttributeModifier.Operation.ADD_SCALAR
    );

    private static final AttributeModifier ATTACK_MODIFIER = new AttributeModifier(
            new NamespacedKey("omc_daily_events", "ancient_attack"),
            0.60,
            AttributeModifier.Operation.ADD_SCALAR
    );

    private static final AttributeModifier SPEED_MODIFIER = new AttributeModifier(
            new NamespacedKey("omc_daily_events", "ancient_speed"),
            0.20,
            AttributeModifier.Operation.ADD_SCALAR
    );

    private static final AttributeModifier FOLLOW_MODIFIER = new AttributeModifier(
            new NamespacedKey("omc_daily_events", "ancient_follow"),
            0.60,
            AttributeModifier.Operation.ADD_SCALAR
    );

    public AncientMonster(String id) {
        super(
                id,
                "Ancient",
                Monster.class,
                3,
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

        EntityGlowNMS.setGlowingColor(entity, ChatFormatting.YELLOW);
    }

    public void resetToDefault(Monster entity) {
        unregisterAsCustomMob(entity);

        entity.setCustomNameVisible(true);

        EntityUtils.removeModifierIfPresent(entity, Attribute.MAX_HEALTH, HEALTH_MODIFIER);
        EntityUtils.removeModifierIfPresent(entity, Attribute.ATTACK_DAMAGE, ATTACK_MODIFIER);
        EntityUtils.removeModifierIfPresent(entity, Attribute.MOVEMENT_SPEED, SPEED_MODIFIER);
        EntityUtils.removeModifierIfPresent(entity, Attribute.FOLLOW_RANGE, FOLLOW_MODIFIER);

        EntityGlowNMS.removeGlowing(entity);
    }
}
