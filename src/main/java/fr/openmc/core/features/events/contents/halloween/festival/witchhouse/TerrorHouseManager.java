package fr.openmc.core.features.events.contents.halloween.festival.witchhouse;

import com.sk89q.worldedit.math.BlockVector3;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.PointRewards;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.floor.Floor;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.floor.Floors;
import fr.openmc.core.registry.ambient.CustomAmbient;
import fr.openmc.core.registry.regions.CustomRegion;
import fr.openmc.core.utils.EnumUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class TerrorHouseManager {

    public static Floors currentFloorToReach;
    public static PointRewards currentRewardToReach;

    public static List<Floors> enabledFloors;
    public static List<CustomRegion> useRegions = new ArrayList<>();

    public static int globalAdvancement;
    public static int floorAdvancement;
    public static boolean finalRoomReach;

    public static int entityCount = 0;
    public static final int MAX_ENTITY = 225; // 15 entities * 15 floors

    @Getter
    public static CustomAmbient witchHouseAmbient = OMCRegistry.CUSTOM_AMBIENTS.WITCH_HOUSE;

    public static void init(int gAdvancement, int fAdvancement) {
        globalAdvancement = gAdvancement;
        floorAdvancement = fAdvancement;

        currentFloorToReach = getFloorFromAdvancement(globalAdvancement);
        currentRewardToReach = getRewardFromAdvancement(globalAdvancement);

        enabledFloors = EnumUtils.getValuesBefore(currentFloorToReach, false);

        finalRoomReach = currentFloorToReach.equals(Floors.FLOOR_2); //TODO last floor FLOOR_15

        for (Floors floors : Floors.values()) {
            Floor floor = floors.getFloor();
            if (floor == null) continue;
            CustomRegion region = floor.getRegion();
            if (region != null)
                useRegions.add(region);
        }
    }

    // --- Advancement / Floor Part --- //

    public static void updateAdvancement(int increase) {
        globalAdvancement += increase;
        floorAdvancement += increase;

        CheckForPointRewards();

        if (currentFloorToReach.equals(Floors.FLOOR_2)) //TODO last floor FLOOR_15
            return;

        if (currentFloorToReach.getRequire() > floorAdvancement)
            return;

        updateFloor();

        //TODO broadcast
    }

    private static void CheckForPointRewards() {
        if (currentRewardToReach.equals(PointRewards.NULL))
            return;

        if (globalAdvancement >= currentRewardToReach.getGlobalPointsRequire()) {
            currentRewardToReach.getReward().giveReward();

            currentRewardToReach = currentRewardToReach.getNextReward();
        }
    }

    private static void updateFloor() {
        floorAdvancement -= currentFloorToReach.getRequire();

        enabledFloors.add(currentFloorToReach);

        currentFloorToReach = EnumUtils.getValueAfter(currentFloorToReach);

        if (currentFloorToReach == null)
            return;

        Floor floor = currentFloorToReach.getFloor();

        if (floor == null)
            return;

        floor.setCanSpawn(true);
    }

    public static Floors getFloorFromAdvancement(int advancement) {
        Floors currentFloors = Floors.FLOOR_1;

        for (Floors floors : Floors.values()) {
            if (floors.getRequire() < advancement)
                currentFloors = floors;
            else if (floors.getRequire() == advancement)
                return floors;
            else break;
        }

        return currentFloors;
    }

    public static PointRewards getRewardFromAdvancement(int points) {

        for (PointRewards rewards : PointRewards.values())
            if (rewards.getGlobalPointsRequire() >= points)
                return rewards;

        return PointRewards.NULL;
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

    // --- Entity part --- //
    public static boolean limitPassed() {
        return entityCount >= MAX_ENTITY;
    }
}
