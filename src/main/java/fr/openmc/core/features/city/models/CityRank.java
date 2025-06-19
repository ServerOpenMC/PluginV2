package fr.openmc.core.features.city.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.openmc.core.features.city.CPermission;
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
public class CityRank {
	
	@DatabaseField(useGetSet = true)
	public String permissions;
	@DatabaseField(useGetSet = true)
	public String members;
	
	@DatabaseField(canBeNull = false)
	private int priority;
	@DatabaseField(id = true, uniqueCombo = true, columnName = "city_uuid")
	private String cityUUID;
	@DatabaseField(canBeNull = false)
	private Material icon;
	@DatabaseField(uniqueCombo = true)
	private String name;
	private Set<CPermission> permissionsSet;
	private Set<UUID> membersSet;
	
	public CityRank() {
		// Default constructor for ORMLite
	}
	
	public CityRank(String cityUUID, String name, int priority, Set<CPermission> permissionsSet, Material icon) {
		this(cityUUID, name, priority, permissionsSet, icon, null);
	}
	
	public CityRank(String cityUUID, String name, int priority, Set<CPermission> permissionsSet, Material icon, Set<UUID> membersSet) {
		this.cityUUID = cityUUID;
		this.name = name;
		this.priority = priority;
		this.permissionsSet = permissionsSet;
		this.icon = icon;
		this.membersSet = membersSet != null ? membersSet : new HashSet<>();
	}
	
	public CityRank validate(Player player) throws IllegalArgumentException {
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
	
	public CityRank withName(String name) {
		this.name = name;
		return this;
	}
	
	public CityRank withPriority(int priority) {
		this.priority = priority;
		return this;
	}
	
	public CityRank withPermissions(Set<CPermission> permissionsSet) {
		this.permissionsSet = permissionsSet;
		return this;
	}
	
	public CityRank withIcon(Material icon) {
		this.icon = icon;
		return this;
	}
	
	public void swapPermission(CPermission permission) {
		if (permissionsSet.contains(permission)) {
			permissionsSet.remove(permission);
		} else {
			permissionsSet.add(permission);
		}
	}
	
	public void addMember(UUID player) {
		membersSet.add(player);
	}
	
	public void removeMember(UUID player) {
		membersSet.remove(player);
	}
	
	/* METHODS FOR ORM - DON'T TOUCH IT */
	
	public String getPermissions() {
		return permissionsSet.stream()
				.map(CPermission::name)
				.reduce((a, b) -> a + "," + b)
				.orElse("");
	}
	
	public void setPermissions(String permissions) {
		if (permissionsSet == null) permissionsSet = new HashSet<>();
		
		if (permissions != null && ! permissions.isEmpty()) {
			String[] perms = permissions.split(",");
			for (String perm : perms) {
				try {
					permissionsSet.add(CPermission.valueOf(perm.trim()));
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
		
		if (members != null && ! members.isEmpty()) {
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
}
