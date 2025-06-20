package fr.openmc.core.features.city;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.listeners.protections.*;
import fr.openmc.core.features.city.sub.war.War;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ProtectionsManager {
    public static final Set<UUID> canBypassPlayer = new HashSet<>();

    private static final Map<UUID, Long> lastErrorMessageTime = new HashMap<>();
    private static final long ERROR_MESSAGE_COOLDOWN = 3000; // 3 secondes

    public ProtectionsManager() {
        OMCPlugin.registerEvents(
                new BlockProtection(),
                new BowProtection(),
                new DamageProtection(),
                new EntityProtection(),
                new ExplodeProtection(),
                new FireProtection(),
                new FishProtection(),
                new FoodProtection(),
                new HangingProtection(),
                new InteractProtection(),
                new LeashProtection(),
                new MountProtection(),
                new PistonProtection(),
                new PotionProtection(),
                new TramplingProtection(),
                new VehicleProtection()
        );
    }
    
    /**
     * Vérifie si le joueur est dans une ville et s'il en est membre.<br>
     * Si le joueur n'en est pas membre, l'événement est annulé.
     *
     * @param player Le joueur à vérifier
     * @param loc La localisation pour vérifier la ville
     */
    public static boolean canInteract(Player player, Location loc) {
        if (! player.getWorld().getName().equals("world")) return true;
		
        if (canBypassPlayer.contains(player.getUniqueId())) return true; // Le joueur peut bypass les protections

        City cityAtLoc = CityManager.getCityFromChunk(loc.getChunk().getX(), loc.getChunk().getZ());

        if (cityAtLoc.isMember(player)) return true;

        War war = cityAtLoc.getWar();
        if (cityAtLoc.isInWar() && war != null && war.getPhase() == War.WarPhase.COMBAT) {
            City playerCity = CityManager.getPlayerCity(player.getUniqueId());
            if (playerCity != null && war.equals(playerCity.getWar())) {
                return war.getAttackers().contains(player.getUniqueId())
                        || war.getDefenders().contains(player.getUniqueId());
            }
        }

        return false;
    }

    public static boolean canExplodeNaturally(Location loc) {
        City city = CityManager.getCityFromChunk(loc.getChunk().getX(), loc.getChunk().getZ());
        if (city == null) return true;

        return city.isInWar() && city.getWar().getPhase() == War.WarPhase.COMBAT;
    }
    
    public static void checkCity(Player player, Cancellable event, City city) {
        if (! player.getWorld().getName().equals("world")) return;
        
        if (city == null) return; // Pas de ville, pas de protection
	    
	    if (canBypassPlayer.contains(player.getUniqueId())) return; // Le joueur peut bypass les protections
	    
	    if (city.isInWar()) return; // En guerre, pas de protection
        
        
        if (! city.isMember(player)) {
            event.setCancelled(true);
            cancelMessage(player);
        }
    }
	
	public static boolean canExplodeNaturally(Location loc) {
		City city = CityManager.getCityFromChunk(loc.getChunk().getX(), loc.getChunk().getZ());
		if (city == null) return true;
		
		return city.isInWar() && city.getWar().getPhase() == War.WarPhase.COMBAT;
	}
	
	public static void verify(Entity entity, Cancellable event, Location loc) {
		if (!entity.getWorld().getName().equals("world")) return;
		
		City city = CityManager.getCityFromChunk(loc.getChunk().getX(), loc.getChunk().getZ()); // on regarde le claim ou l'action a été fait
		if (city == null || !CityType.WAR.equals(city.getType()))
			return;
		
		event.setCancelled(true);
	}
	
	/**
	 * Envoie un message d'erreur au joueur si celui-ci n'a pas l'autorisation d'effectuer une action.
	 *
	 * @param player Le joueur à qui envoyer le message
	 */
	public static void cancelMessage(Player player) {
		long now = System.currentTimeMillis();
		long last = lastErrorMessageTime.getOrDefault(player.getUniqueId(), 0L);
		if (now - last >= ERROR_MESSAGE_COOLDOWN) {
			lastErrorMessageTime.put(player.getUniqueId(), now);
			MessagesManager.sendMessage(
					player,
					Component.text("Vous n'avez pas l'autorisation de faire ceci !"),
					Prefix.CITY,
					MessageType.ERROR,
					0.6F,
					true
			);
		}
	}
	
	public static void checkPermissions(@NotNull Player player, Cancellable event, City city, CPermission permission) {
		if (! player.getWorld().getName().equals("world")) return;
		
		if (canBypassPlayer.contains(player.getUniqueId())) return; // Le joueur peut bypass les protections
		if (city == null) return; // Pas de ville, pas de protection
		if (city.isInWar()) return; // En guerre, pas de protection
		
		if (city.isMember(player)) {
			if (! city.hasPermission(player.getUniqueId(), permission)) {
				event.setCancelled(true);
				cancelMessage(player);
			}
		} else {
			checkCity(player, event, city);
		}
	}
}
