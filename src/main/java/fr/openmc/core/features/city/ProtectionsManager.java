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
    
    public static boolean verify(Player player, Cancellable event, Location loc) {
        if (! player.getWorld().getName().equals("world")) {
            return true; // Pas de protection dans les mondes autres que "world"
        }

        boolean canBypass = canBypassPlayer.contains(player.getUniqueId());
        if (canBypass) return true; // Le joueur peut bypass les protections

        City cityAtLoc = CityManager.getCityFromChunk(loc.getChunk().getX(), loc.getChunk().getZ());
        if (cityAtLoc == null) return true; // Pas de ville à cet endroit, pas de protection

        CityType cityType = cityAtLoc.getType();
        boolean isMember = cityAtLoc.isMember(player);

        if (cityType.equals(CityType.WAR)) {
            return true; // En guerre, pas de protection
        }

        if (!isMember) {
            event.setCancelled(true);
            
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
            return false; // Le joueur n'est pas membre de la ville, action annulée
        }
        return true; // Le joueur est membre de la ville, action autorisée
    }

    public static void verify(Entity entity, Cancellable event, Location loc) {
        if (!entity.getWorld().getName().equals("world")) return;

        City city = CityManager.getCityFromChunk(loc.getChunk().getX(), loc.getChunk().getZ()); // on regarde le claim ou l'action a été fait
        if (city == null || !CityType.WAR.equals(city.getType()))
            return;

        event.setCancelled(true);
    }
    
    public static boolean verifyByPermission(Player player, Location loc, CPermission permission) {
        City city = CityManager.getCityFromChunk(loc.getChunk().getX(), loc.getChunk().getZ());
        if (city == null) return true; // Pas de ville à cet endroit, pas de protection
        
        if (! city.hasPermission(player.getUniqueId(), permission)) {
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
            return false; // Le joueur n'a pas la permission, action annulée
        }
        return true; // Le joueur a la permission, action autorisée
    }
}
