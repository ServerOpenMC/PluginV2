package fr.openmc.core.registry.mobs;

import fr.openmc.core.utils.bukkit.EntityUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;

public record CustomMobAttribute(Attribute attribute, double value) {

    public void setAttributeIfPresent(LivingEntity entity) {
        EntityUtils.setAttributeIfPresent(entity, attribute, value);
    }
}
