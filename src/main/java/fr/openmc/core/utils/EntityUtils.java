package fr.openmc.core.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.EntityType;

import java.util.Objects;

public class EntityUtils {
    /**
     * Return a {@link TranslatableComponent} from an {@link EntityType}
     *
     * @param entityType EntityType to translate
     * @return a {@link TranslatableComponent} that can be translated by client
     */
    public static TranslatableComponent getEntityTranslation(EntityType entityType) {
        return Component.translatable(Objects.requireNonNullElse(
                entityType.translationKey(),
                "entity.minecraft.pig"
        ));
    }

    /**
     * Return a plain String name from an {@link EntityType}
     *
     * @param entityType EntityType to translate
     * @return the plain translated name
     */
    public static String getEntityName(EntityType entityType) {
        return PlainTextComponentSerializer.plainText().serialize(getEntityTranslation(entityType));
    }
}
