package fr.openmc.core.features.city;

import fr.openmc.core.features.city.listeners.protections.*;
import fr.openmc.core.features.city.sub.war.War;
import fr.openmc.core.features.shops.managers.ShopManager;
import fr.openmc.core.lifecycle.interfaces.HasListeners;
import fr.openmc.core.lifecycle.listeners.ListenerFactory;
import fr.openmc.core.registry.features.Feature;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Location;
import org.bukkit.block.Barrel;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ProtectionsManager extends Feature implements HasListeners {
    public final Set<UUID> canBypassPlayer = new HashSet<>();

    private final Map<UUID, Long> lastErrorMessageTime = new HashMap<>();
    private final long ERROR_MESSAGE_COOLDOWN = 3000; // 3 secondes

	private final CityManager cityManager;

	public ProtectionsManager(CityManager cityManager) {
		this.cityManager = cityManager;
	}

	@Override
	public Set<ListenerFactory> getListeners() {
		return Set.of(
				() -> new BlockProtection(cityManager),
				() -> new BowProtection(cityManager),
				() -> new DamageProtection(cityManager),
				() -> new EntityProtection(cityManager),
				() -> new ExplodeProtection(cityManager),
				() -> new FireProtection(cityManager),
				() -> new FishProtection(cityManager)	,
				() -> new HangingProtection(cityManager),
				() -> new InteractProtection(cityManager),
				() -> new LeashProtection(cityManager),
				() -> new MountProtection(cityManager),
				() -> new PistonProtection(cityManager),
				() -> new PotionProtection(cityManager),
				() -> new TeleportProtection(cityManager),
				() -> new TramplingProtection(cityManager),
				() -> new VehicleProtection(cityManager)
		);
	}

    /**
     * Vérifie si le joueur est dans une ville et s'il en est membre.<br>
     * Si le joueur n'en est pas membre, l'événement est annulé.
     *
     * @param player Le joueur à vérifier
     * @param loc La localisation pour vérifier la ville
     */
    public boolean canInteract(Player player, Location loc) {
        if (!player.getWorld().getName().equals("world")) return true;
		
        if (canBypassPlayer.contains(player.getUniqueId())) return true; // Le joueur peut bypass les protections

        City cityAtLoc = cityManager.getCityFromChunk(loc.getChunk().getX(), loc.getChunk().getZ());

		if (cityAtLoc == null) return true;

        if (cityAtLoc.isMember(player)) return true;

        War war = cityAtLoc.getWar();
        if (cityAtLoc.isInWar() && war != null && war.getPhase() == War.WarPhase.COMBAT) {
            City playerCity = cityManager.getPlayerCity(player.getUniqueId());
            if (playerCity != null && war.equals(playerCity.getWar())) {
                return war.getAttackers().contains(player.getUniqueId())
                        || war.getDefenders().contains(player.getUniqueId());
            }
        }

        return false;
    }

    public boolean canExplodeNaturally(Location loc) {
        City city = cityManager.getCityFromChunk(loc.getChunk().getX(), loc.getChunk().getZ());
        return city == null;
    }
    
    public void checkCity(Player player, Cancellable event, City city, boolean allowByPassInWar) {
        if (!player.getWorld().getName().equals("world")) return;
        
		if (city == null) return; // Pas de ville, pas de protection
	    
	    if (canBypassPlayer.contains(player.getUniqueId())) return; // Le joueur peut bypass les protections
	    
	    if (allowByPassInWar && city.isInWar()) return; // En guerre, pas de protection

        if (!city.isMember(player)) {
            event.setCancelled(true);
			cancelMessage(player);
        }
    }
	
	public void verify(Entity entity, Cancellable event, Location loc) {
		if (!entity.getWorld().getName().equals("world")) return;
		
		if (ShopManager.getShopAt(loc) != null) {
			if (loc.getBlock().getState() instanceof Barrel) return;
			event.setCancelled(true);
			return;
		}
		
		City city = cityManager.getCityFromChunk(loc.getChunk().getX(), loc.getChunk().getZ()); // on regarde le claim ou l'action a été fait
		if (city == null || city.isInWar()) return;

		if (entity instanceof Player player) {
			if (canInteract(player, loc)) return;

			event.setCancelled(true);
			
			cancelMessage(player);
		} else {
			event.setCancelled(true);
		}

	}
	
	/**
	 * Envoie un message d'erreur au joueur si celui-ci n'a pas l'autorisation d'effectuer une action.
	 *
	 * @param player Le joueur à qui envoyer le message
	 */
	public void cancelMessage(Player player) {
		long now = System.currentTimeMillis();
		long last = lastErrorMessageTime.getOrDefault(player.getUniqueId(), 0L);
		if (now - last >= ERROR_MESSAGE_COOLDOWN) {
			lastErrorMessageTime.put(player.getUniqueId(), now);
			MessagesManager.sendMessage(
					player,
					TranslationManager.translation("feature.city.cant_do_this"),
					Prefix.CITY,
					MessageType.ERROR,
					0.6F,
					true
			);
		}
	}
	
	public void checkPermissions(@NotNull Player player, Cancellable event, City city, CityPermission permission) {
		if (!player.getWorld().getName().equals("world")) return;

		if (canBypassPlayer.contains(player.getUniqueId())) return; // Le joueur peut bypass les protections
		if (city == null) return; // Pas de ville, pas de protection
		if (city.isInWar()) return; // En guerre, pas de protection

		if (city.isMember(player)) {
			if (!city.hasPermission(player.getUniqueId(), permission)) {
				event.setCancelled(true);
				cancelMessage(player);
			}
		} else {
			checkCity(player, event, city, false);
		}
	}
}
