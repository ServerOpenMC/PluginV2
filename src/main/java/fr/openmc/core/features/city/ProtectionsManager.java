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
     * @return true si le joueur est membre de la ville, false autrement.
     */
    public static boolean checkClaimAndCheckIfIsMember(Player player, Cancellable event, Location loc) {
        if (! player.getWorld().getName().equals("world")) {
            return false; // Pas de protection dans les mondes autres que "world"
        }

        boolean canBypass = canBypassPlayer.contains(player.getUniqueId());
        if (canBypass) return false; // Le joueur peut bypass les protections

        City cityAtLoc = CityManager.getCityFromChunk(loc.getChunk().getX(), loc.getChunk().getZ());
        if (cityAtLoc == null) return false; // Pas de ville à cet endroit, pas de protection

        CityType cityType = cityAtLoc.getType();
        boolean isMember = cityAtLoc.isMember(player);

        if (cityType.equals(CityType.WAR)) {
            return false; // En guerre, pas de protection
        }
        
        if (! isMember) {
            cancelEvent(player, event);
            return false; // Le joueur n'est pas membre de la ville, action annulée
        } else {
            return true; // Le joueur est membre de la ville, verification des permissions à faire
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
     * @param loc La localisation pour vérifier la ville
     * @param permission La permission à vérifier
     * @return true si le joueur a la permission, false autrement.
     */
    public static boolean checkPermissions(Player player, Cancellable event, Location loc, CPermission permission) {
        City city = CityManager.getCityFromChunk(loc.getChunk().getX(), loc.getChunk().getZ());
        if (city == null) return false; // Pas de ville à cet endroit, pas de protection
        
        if (! city.hasPermission(player.getUniqueId(), permission)) {
            cancelEvent(player, event);
            return false; // Le joueur n'a pas la permission, action annulée
        }
        return true; // Le joueur a la permission, action autorisée
    }
    
    /**
     * Annule l'événement et envoie un message d'erreur au joueur.
     *
     * @param player Le joueur à qui envoyer le message
     * @param event L'événement à annuler
     */
    private static void cancelEvent(Player player, Cancellable event) {
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
        event.setCancelled(true);
    }
}
