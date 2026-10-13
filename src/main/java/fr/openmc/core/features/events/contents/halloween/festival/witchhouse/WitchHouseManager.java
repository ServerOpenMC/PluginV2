package fr.openmc.core.features.events.contents.halloween.festival.witchhouse;

import com.sk89q.worldedit.math.BlockVector3;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.Floors;
import fr.openmc.core.registry.ambient.CustomAmbient;
import fr.openmc.core.registry.regions.CustomRegion;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.List;

public class WitchHouseManager {

    public Floors currentFloor;
    public static List<CustomRegion> enabledRegions; //TODO add values to it
    public int floorAdvancement;
    public boolean finalRoomReach;

    BukkitTask spawnTimeTask;

    @Getter
    public static CustomAmbient witchHouseAmbient = OMCRegistry.CUSTOM_AMBIENTS.WITCH_HOUSE;

    public WitchHouseManager(Floors advancements, int countAdvancement) {
        currentFloor = advancements;
        this.floorAdvancement = countAdvancement;
        finalRoomReach = currentFloor.equals(Floors.FLOOR_2); //TODO last floor FLOOR_15
    }

    public void updateAdvancement(int increase) {
        floorAdvancement += increase;

        if (currentFloor.getRequire() > floorAdvancement) {
            return;
        }
        int currentRoom = Integer.parseInt(currentFloor.name().replaceFirst(".*\\D", ""));

        currentRoom++;

        currentFloor = Floors.valueOf("FLOOR_" + currentRoom);

        //TODO broadcast
    }

    public void prepareSpawn() {
        // entre 1m et 1m30 -> dire que l'on peut spawn vague de mob, avec int min et int max
        // selon l'étage plus de mob + ou - dure

        spawnTimeTask = Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(),
                () -> {
                    // dire au zone de check pour spawn des mobs, sinon mettre en attente
                });


    }

    public void delayedSpawnMob() {

    }

    public void broadcast(Component text) {
        for (Player player : OMCPlugin.getInstance().getServer().getOnlinePlayers()) {
            MessagesManager.sendMessage(
                    player,
                    TranslationManager.translation("", text),
                    Prefix.HALLOWEEN,
                    MessageType.INFO,
                    true
            );
        }
    }

    public static void repulsePlayer(CustomRegion region, Entity entity, double force) {
        BlockVector3 coords = region.getCenter();
        Location center = new Location(region.getWorld(), coords.x(), coords.y(), coords.z());
        repulsePlayer(center, entity, force);
    }

    public static void repulsePlayer(Entity entity, double force) {
        repulsePlayer(entity.getLocation(), entity, force);
    }

    public static void repulsePlayer(Location source, Entity entity, double force) {
        Location playerLocation = entity.getLocation();

        if (source.getWorld() == null
                || !source.getWorld().equals(playerLocation.getWorld())) {
            return;
        }

        Vector knockback = playerLocation.toVector()
                .subtract(source.toVector());

        if (knockback.lengthSquared() < 1.0E-6) {
            knockback = playerLocation.getDirection();
        }

        knockback.normalize().multiply(force);

        entity.setVelocity(knockback);
    }
}
