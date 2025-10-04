package fr.openmc.core.features.city.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@DatabaseTable(tableName = "city_ranks")
@Getter
public class DBCityRank {
	
	@DatabaseField(id = true, canBeNull = false, unique = true, columnName = "rank_uuid")
	private UUID rankUUID;
	@DatabaseField(uniqueCombo = true, columnName = "city_uuid")
	private UUID cityUUID;
	@DatabaseField(useGetSet = true)
	public String permissions;
	@DatabaseField(canBeNull = false)
	private int priority;
	@DatabaseField(useGetSet = true)
	public String members;
	@DatabaseField(uniqueCombo = true)
	private String name;
	@DatabaseField(canBeNull = false)
	private Material icon;

	private Set<CityPermission> permissionsSet;
	private Set<UUID> membersSet;
	
	public DBCityRank() {
		// Default constructor for ORMLite
	}
	
	/**
	 * Constructor for creating a new CityRank.
	 *
	 * @param rankUUID       Unique identifier for the rank.
	 * @param cityUUID       Unique identifier for the city this rank belongs to.
	 * @param name           Name of the rank.
	 * @param priority       Priority of the rank (0-17).
	 * @param permissionsSet Set of permissions associated with this rank.
	 * @param icon           Icon representing the rank.
	 */
	public DBCityRank(UUID rankUUID, UUID cityUUID, String name, int priority, Set<CityPermission> permissionsSet, Material icon) {
		this.rankUUID = rankUUID;
		this.cityUUID = cityUUID;
		this.name = name;
		this.priority = priority;
		this.permissionsSet = permissionsSet;
		this.icon = icon;
		this.membersSet = new HashSet<>();
	}
	
	/**
	 * Validates the CityRank properties.
	 *
	 * @param player Player to send error messages to.
	 * @return The validated CityRank instance.
	 * @throws IllegalArgumentException if any validation fails.
	 */
	public DBCityRank validate(Player player) throws IllegalArgumentException {
		if (name == null || name.isEmpty()) {
			MessagesManager.sendMessage(player, Component.text("Le nom du grade ne peut pas être vide"), Prefix.CITY, MessageType.ERROR, false);
			throw new IllegalArgumentException("Rank name cannot be null or empty");
		}
		if (priority < 0) {
			MessagesManager.sendMessage(player, Component.text("La priorité doit être contenue entre 0 et 17"), Prefix.CITY, MessageType.ERROR, false);
			throw new IllegalArgumentException("Rank priority cannot be negative");
		}
		if (icon == null) {
			MessagesManager.sendMessage(player, Component.text("L'icône du grade ne peut pas être nulle (prévenir le staff)"), Prefix.CITY, MessageType.ERROR, false);
			throw new IllegalArgumentException("Rank icon cannot be null");
		}
		return this;
	}
	
	/**
	 * Creates a copy of the current CityRank with the specified name.
	 *
	 * @param name The new name for the rank.
	 * @return A new CityRank instance with the new name.
	 */
	public DBCityRank withName(String name) {
		this.name = name;
		return this;
	}
	
	/**
	 * Creates a copy of the current CityRank with the specified priority.
	 *
	 * @param priority The new priority for the rank.
	 * @return A new CityRank instance with the new priority.
	 */
	public DBCityRank withPriority(int priority) {
		this.priority = priority;
		return this;
	}
	
	/**
	 * Creates a copy of the current CityRank with the specified permissions.
	 *
	 * @param permissionsSet The new set of permissions for the rank.
	 * @return A new CityRank instance with the new permissions.
	 */
	public DBCityRank withPermissions(Set<CityPermission> permissionsSet) {
		this.permissionsSet = permissionsSet;
		return this;
	}
	
	/**
	 * Creates a copy of the current CityRank with the specified icon.
	 *
	 * @param icon The new icon for the rank.
	 * @return A new CityRank instance with the new icon.
	 */
	public DBCityRank withIcon(Material icon) {
		this.icon = icon;
		return this;
	}
	
	/**
	 * Toggles the specified permission for this rank.
	 *
	 * @param permission The permission to toggle.
	 */
	public void swapPermission(CityPermission permission) {
		if (permissionsSet.contains(permission)) {
			permissionsSet.remove(permission);
		} else {
			permissionsSet.add(permission);
		}
	}
	
	/**
	 * Adds a member to this rank.
	 *
	 * @param playerUUID The UUID of the player to add as a member.
	 */
	public void addMember(UUID playerUUID) {
		membersSet.add(playerUUID);
	}
	
	/**
	 * Removes a member from this rank.
	 *
	 * @param playerUUID The UUID of the player to remove from members.
	 */
	public void removeMember(UUID playerUUID) {
		membersSet.remove(playerUUID);
	}
	
	/* METHODS FOR ORM - DON'T TOUCH IT */
	
	public String getPermissions() {
		return permissionsSet.stream()
				.map(CityPermission::name)
				.reduce((a, b) -> a + "," + b)
				.orElse("");
	}
	
	public void setPermissions(String permissions) {
		if (permissionsSet == null) permissionsSet = new HashSet<>();
		
		if (permissions != null && !permissions.isEmpty()) {
			String[] perms = permissions.split(",");
			for (String perm : perms) {
				try {
					permissionsSet.add(CityPermission.valueOf(perm.trim()));
				} catch (IllegalArgumentException e) {
					// Ignore invalid permissions
				}
			}
		}
	}
	
	public String getMembers() {
		return membersSet.stream()
				.map(UUID::toString)
				.reduce((a, b) -> a + "," + b)
				.orElse("");
	}
	
	public void setMembers(String members) {
		if (membersSet == null) membersSet = new HashSet<>();
		
		if (members != null && !members.isEmpty()) {
			String[] membersUUIDs = members.split(",");
			for (String uuid : membersUUIDs) {
				try {
					membersSet.add(UUID.fromString(uuid.trim()));
				} catch (IllegalArgumentException e) {
					// Ignore invalid UUIDs
				}
			}
		}
	}
	
	public DBCityRank clone() {
		return new DBCityRank(this.rankUUID, this.cityUUID, this.name, this.priority, new HashSet<>(this.permissionsSet), this.icon);
	}
}
