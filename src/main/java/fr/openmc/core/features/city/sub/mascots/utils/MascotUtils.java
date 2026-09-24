package fr.openmc.core.features.city.sub.mascots.utils;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
import fr.openmc.core.features.city.sub.mascots.models.Mascot;
import fr.openmc.core.features.city.sub.mascots.models.MascotType;
import org.bukkit.Chunk;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class MascotUtils {
	private static final Set<EntityType> POSSIBLE_MASCOT_TYPES = Arrays.stream(MascotType.values())
			.map(MascotType::getEntityType)
			.collect(Collectors.toUnmodifiableSet());

	/**
	 * Checks if an entity can be a mascot based on their type.
	 * @param entity The entity to check.
	 * @return true if the entity can be a mascot, false otherwise.
	 */
	public static boolean canBeAMascot(Entity entity) {
		// Check if the entity is of a type that can be a mascot
		// So it doesn't check the persistent data container that takes more time
		if (!POSSIBLE_MASCOT_TYPES.contains(entity.getType()))
			return false;

		return entity.getPersistentDataContainer().has(OMCRegistry.CITY_FEATURES.MASCOTS.getMascotsKey(), PersistentDataType.STRING);
	}
}

