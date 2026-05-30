package fr.openmc.core.utils.bukkit;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;

public class EntityUtils {
    /**
     * Ajoute un attribut si l'AttributeInstance n'est pas nulle
     * @param entity l'entity
     * @param attribute l'attribut qui veut etre mis
     * @param value la valeur appliquée a l'attribut
     */
    public static void setAttributeIfPresent(LivingEntity entity, Attribute attribute, double value) {
        AttributeInstance attr = entity.getAttribute(attribute);
        if (attr != null) {
            attr.setBaseValue(value);
        }
    }
}
