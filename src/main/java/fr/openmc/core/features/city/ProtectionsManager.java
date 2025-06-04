package fr.openmc.core.features.city;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.listeners.protections.*;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

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
                new PotionProtection(),
                new TramplingProtection()
        );
    }
    
    /**
     * Vérifie si le joueur est dans une ville et s'il en est membre.<br>
     * Si le joueur n'en est pas membre, l'événement est annulé.
     *
     * @param player Le joueur à vérifier
     * @param event L'événement à annuler si le joueur n'est pas membre
     * @param loc La localisation pour vérifier la ville
     */
    public static void checkClaim(Player player, Cancellable event, Location loc) {
        if (! player.getWorld().getName().equals("world")) return;
        
        boolean canBypass = canBypassPlayer.contains(player.getUniqueId());
        if (canBypass) return; // Le joueur peut bypass les protections

        City cityAtLoc = CityManager.getCityFromChunk(loc.getChunk().getX(), loc.getChunk().getZ());
        
        checkCity(player, event, cityAtLoc);
    }
    
    public static void checkCity(Player player, Cancellable event, City city) {
        if (! player.getWorld().getName().equals("world")) return;
        
        if (city == null) return; // Pas de ville, pas de protection
        
        boolean canBypass = canBypassPlayer.contains(player.getUniqueId());
        if (canBypass) return; // Le joueur peut bypass les protections
        
        if (city.getType().equals(CityType.WAR)) {
            return; // En guerre, pas de protection
        }
        
        if (! city.isMember(player)) {
            event.setCancelled(true);
            cancelMessage(player);
        }
    }
    
    /**
     * Vérifie si l'entité est dans une ville en guerre et annule l'événement si c'est le cas.
     *
     * @param entity L'entité à vérifier
     * @param event L'événement à annuler si l'entité est dans une ville en guerre
     * @param loc La localisation pour vérifier la ville
     */
    public static void verify(Entity entity, Cancellable event, Location loc) {
        if (!entity.getWorld().getName().equals("world")) return;

        City city = CityManager.getCityFromChunk(loc.getChunk().getX(), loc.getChunk().getZ()); // on regarde le claim ou l'action a été fait
        if (city == null || !CityType.WAR.equals(city.getType()))
            return;

        event.setCancelled(true);
    }
    
    /**
     * Vérifie si le joueur a la permission d'effectuer une action dans la ville à l'emplacement donné.
     *
     * @param player Le joueur à vérifier
     * @param event L'événement à annuler si le joueur n'a pas la permission
     * @param permission La permission à vérifier
     */
    public static void checkPermissions(Player player, Cancellable event, City city, CPermission permission) {
        if (city == null) return; // Pas de ville à cet endroit, pas de protection
        
        if (! city.hasPermission(player.getUniqueId(), permission)) {
            event.setCancelled(true);
            cancelMessage(player);
        }
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
}
