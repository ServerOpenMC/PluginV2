package fr.openmc.core.utils.bukkit;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;

public class EntityUtils {
    /**
     * Ajoute un attribut si l'AttributeInstance n'est pas nulle
     * @param entity l'entité
     * @param attribute l'attribut qui veut etre mis
     * @param value la valeur appliquée a l'attribut
     */
    public static void setAttributeIfPresent(LivingEntity entity, Attribute attribute, double value) {
        AttributeInstance attr = entity.getAttribute(attribute);
        if (attr != null) {
            attr.setBaseValue(value);
        }
    }

    /**
     * Ajoute un attribut si l'AttributeInstance n'est pas nulle
     * @param entity l'entité
     * @param attribute l'attribut qui veut etre mis
     * @param modifier le modifier appliqué sur la stats de l'entité
     */
    public static void addModifierIfPresent(LivingEntity entity, Attribute attribute, AttributeModifier modifier) {
        AttributeInstance attr = entity.getAttribute(attribute);
        if (attr == null) return;

        NamespacedKey modifierKey = modifier.getKey();
        if (attr.getModifier(modifierKey) != null) {
            attr.removeModifier(modifierKey);
        }

        attr.addModifier(modifier);
    }

    /**
     * Supprime un modifier si l'AttributeInstance n'est pas nulle et que le modifier est présent
     * @param entity l'entité
     * @param attribute l'attribut qui est ciblé
     * @param modifier le modifier
     */
    public static void removeModifierIfPresent(LivingEntity entity, Attribute attribute, AttributeModifier modifier) {
        AttributeInstance attr = entity.getAttribute(attribute);
        if (attr == null) return;
        NamespacedKey modifierKey = modifier.getKey();
        if (attr.getModifier(modifierKey) == null) return;

        attr.removeModifier(modifierKey);
    }

    /**
     * Supprime un modifier si l'AttributeInstance n'est pas nulle et que le modifier est présent
     * @param entity l'entité
     * @param attribute l'attribut qui est ciblé
     * @param modifierKey la cle du modifier
     */
    public static void removeModifierIfPresent(LivingEntity entity, Attribute attribute, NamespacedKey modifierKey) {
        AttributeInstance attr = entity.getAttribute(attribute);
        if (attr == null) return;
        if (attr.getModifier(modifierKey) == null) return;

        attr.removeModifier(modifierKey);
    }
}
