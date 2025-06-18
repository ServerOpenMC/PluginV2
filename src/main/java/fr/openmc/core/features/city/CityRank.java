package fr.openmc.core.features.city;

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

@Getter
public class CityRank {
	
	private String name;
	private int priority;
	private Set<CPermission> permissions;
	private Material icon;
	private final Set<UUID> members; // Optional, if you want to track members with this rank
	
	public CityRank(String name, int priority, Set<CPermission> permissions, Material icon) {
		this(name, priority, permissions, icon, null);
	}
	
	public CityRank(String name, int priority, Set<CPermission> permissions, Material icon, Set<UUID> members) {
		this.name = name;
		this.priority = priority;
		this.permissions = permissions;
		this.icon = icon;
		this.members = members != null ? members : new HashSet<>();
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
		if (permissions == null) {
			MessagesManager.sendMessage(player, Component.text("Les permissions du grade ne peuvent pas être nulles (prévenir le staff)"), Prefix.CITY, MessageType.ERROR, false);
			throw new IllegalArgumentException("Rank must have at least one permission");
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
	
	public CityRank withPermissions(Set<CPermission> permissions) {
		this.permissions = permissions;
		return this;
	}
	
	public CityRank withIcon(Material icon) {
		this.icon = icon;
		return this;
	}
	
	public void swapPermission(CPermission permission) {
		if (permissions.contains(permission)) {
			permissions.remove(permission);
		} else {
			permissions.add(permission);
		}
	}
	
	public void addMember(UUID player) {
		members.add(player);
	}
	
	public void removeMember(UUID player) {
		members.remove(player);
	}
}
