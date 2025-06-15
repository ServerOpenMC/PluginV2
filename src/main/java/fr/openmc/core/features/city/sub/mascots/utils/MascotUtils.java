package fr.openmc.core.features.city.sub.mascots.utils;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.mascots.Mascot;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class MascotUtils {

	/**
	 * Adds a mascot for a given city.
	 *
	 * @param city       The city for which the mascot is being added.
	 * @param mascotUUID The UUID of the mascot entity.
	 * @param chunk      The chunk where the mascot is located.
	 *                   This method creates a new Mascot object and adds it to the mascotsByCityUUID and mascotsByEntityUUID maps.
	 */
	public static void addMascotForCity(City city, UUID mascotUUID, Chunk chunk) {
		Mascot newMascot = new Mascot(city, mascotUUID, 1, true, true, chunk);
		MascotsManager.mascotsByCityUUID.put(city.getUUID(), newMascot);
		MascotsManager.mascotsByEntityUUID.put(mascotUUID, newMascot);
	}

	/**
	 * Removes the mascot associated with a given city.
	 * @param city The city whose mascot is to be removed.
	 * This method will also remove the mascot from the mascotsByCityUUID and mascotsByEntityUUID maps.
	 */
	public static void removeMascotOfCity(City city) {
		Mascot mascot = city.getMascot();
		MascotsManager.mascotsByCityUUID.remove(city.getUUID());
		MascotsManager.mascotsByEntityUUID.remove(mascot.getMascotUUID());
	}

	/**
	 * Checks if an entity is a mascot.
	 * @param entity The entity to check.
	 * @return true if the entity is a mascot, false otherwise.
	 */
	public static boolean isMascot(Entity entity) {
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

