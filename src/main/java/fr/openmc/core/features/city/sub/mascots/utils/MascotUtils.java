package fr.openmc.core.features.city.sub.mascots.utils;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
import fr.openmc.core.features.city.sub.mascots.models.Mascot;
import fr.openmc.core.features.city.sub.mascots.models.MascotType;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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
	 * Adds a mascot for a given city.
	 *
	 * @param city       The city for which the mascot is being added.
	 * @param mascotUUID The UUID of the mascot entity.
	 * @param chunk      The chunk where the mascot is located.
	 *                   This method creates a new Mascot object and adds it to the mascotsByCityUUID and mascotsByEntityUUID maps.
	 */
	public static void addMascotForCity(City city, UUID mascotUUID, Chunk chunk) {
        Mascot newMascot = new Mascot(city.getUUID(), mascotUUID, 1, true, true, chunk.getX(), chunk.getZ());
		MascotsManager.mascotsByCityUUID.put(city.getUUID(), newMascot);
		MascotsManager.mascotsByEntityUUID.put(mascotUUID, newMascot);
	}

	/**
	 * Removes the mascot associated with a given city.
	 * @param mascot The mascot is to be removed.
	 * This method will also remove the mascot from the mascotsByCityUUID and mascotsByEntityUUID maps.
	 */
	public static void removeMascotOfCity(Mascot mascot) {
		MascotsManager.mascotsByCityUUID.remove(mascot.getCityUUID());
		MascotsManager.mascotsByEntityUUID.remove(mascot.getMascotUUID());
	}

	/**
	 * Checks if an entity is a mascot.
	 * @param entity The entity to check.
	 * @return true if the entity is a mascot, false otherwise.
	 */
	public static boolean isMascot(Entity entity) {
		// Check if the entity is of a type that can be a mascot
		// So it doesn't check the persistent data container that takes more time
		if (!POSSIBLE_MASCOT_TYPES.contains(entity.getType()))
			return false;

		return entity.getPersistentDataContainer().has(MascotsManager.mascotsKey, PersistentDataType.STRING);
	}

	public static City getCityFromEntity(UUID entityUUID) {
		City city = null;

		if (MascotsManager.mascotsByEntityUUID.containsKey(entityUUID)) {
			Mascot mascot = MascotsManager.mascotsByEntityUUID.get(entityUUID);
			city = mascot.getCity();
		}

		return city;
	}
}

