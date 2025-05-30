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

    public static void verify(Player player, Cancellable event, Location loc) {
        if (!player.getWorld().getName().equals("world")) return;

        boolean canBypass = canBypassPlayer.contains(player.getUniqueId());
        if (canBypass) return;

        City cityAtLoc = CityManager.getCityFromChunk(loc.getChunk().getX(), loc.getChunk().getZ());
        if (cityAtLoc == null) return;

        CityType cityType = cityAtLoc.getType();
        boolean isMember = cityAtLoc.isMember(player);

        if (cityType.equals(CityType.WAR)) {
            return;
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
        }
    }

    public static void verify(Entity entity, Cancellable event, Location loc) {
        if (!entity.getWorld().getName().equals("world")) return;

        City city = CityManager.getCityFromChunk(loc.getChunk().getX(), loc.getChunk().getZ()); // on regarde le claim ou l'action a été fait
        if (city == null || !CityType.WAR.equals(city.getType()))
            return;

        event.setCancelled(true);
    }
}
