package fr.openmc.core.features.events.contents.halloween.festival.witchhouse;

import com.sk89q.worldedit.math.BlockVector3;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.halloween.festival.HalloweenFestivalManager;
import fr.openmc.core.features.events.contents.halloween.festival.shop.WitchShopManager;
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
    public static boolean finalFloorReach;

    public static final Floors FINAL_FLOOR = Floors.FLOOR_15;

    public static int entityCount = 0;
    public static final int MAX_ENTITY = 225; // 15 entities * 15 floors

    @Getter
    public static CustomAmbient witchHouseAmbient = OMCRegistry.CUSTOM_AMBIENTS.TERROR_HOUSE;

    public static void init(int gAdvancement, int fAdvancement) {
        // Advancement
        globalAdvancement = gAdvancement;
        floorAdvancement = fAdvancement;

        // Current things that are reachable
        currentFloorToReach = getFloorFromAdvancement(globalAdvancement);
        currentRewardToReach = getRewardFromAdvancement(globalAdvancement);

        // Floors
        finalFloorReach = currentFloorToReach.equals(FINAL_FLOOR);
        enabledFloors = EnumUtils.getValuesBefore(currentFloorToReach, finalFloorReach);

        for (Floors floors : Floors.values()) {
            Floor floor = floors.getFloor();
            if (floor == null)
                continue;

            if (enabledFloors.contains(floors))
                floor.enableSpawn();

            CustomRegion region = floor.getRegion();
            if (region != null)
                useRegions.add(region);
        }
    }

    public static void updateAdvancement(int increase) {
        globalAdvancement += increase;
        floorAdvancement += increase;

        CheckForPointRewards();

        if (finalFloorReach)
            return;

        if (currentFloorToReach.getRequire() > floorAdvancement)
            return;

        updateFloor();
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
        if (finalFloorReach)
            return;

        floorAdvancement -= currentFloorToReach.getRequire();

        enabledFloors.add(currentFloorToReach);

        Floor floor = currentFloorToReach.getFloor();

        if (floor == null)
            return;

        floor.enableSpawn();

        if (currentFloorToReach.equals(Floors.FLOOR_15)) {

            finalFloorReach = true;

            HalloweenFestivalManager.broadcast(
                    TranslationManager.translation(
                            "feature.events.halloween.festival.last_floor_unlock",
                            Component.text(currentRewardToReach.getGlobalPointsRequire()),
                            Component.text(globalAdvancement),
                            Component.text(WitchShopManager.getHalloweenPointIcon())
                    )
            );

            return;
        }

        currentFloorToReach = EnumUtils.getValueAfter(currentFloorToReach);

        if (currentFloorToReach == null)
            return;

        if (floorAdvancement >= currentFloorToReach.getRequire())
            updateFloor();

        else
            HalloweenFestivalManager.broadcast(
                    TranslationManager.translation(
                            "feature.events.halloween.festival.new_floor_unlock",
                            Component.text(currentFloorToReach.getRequire()),
                            Component.text(floorAdvancement),
                            Component.text(WitchShopManager.getHalloweenPointIcon())
                    )
            );

    }

    public static Floors getFloorFromAdvancement(int advancement) {
        Floors currentFloors = Floors.FLOOR_2;

        for (Floors floors : Floors.values()) {
            if (floors.getRequire() < advancement)
                currentFloors = floors;
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

    public static void repulsePlayer(Location source, Entity entity, double force) {
        Location playerLocation = entity.getLocation();

        if (source.getWorld() == null
                || !source.getWorld().equals(playerLocation.getWorld())) {
            return;
        }

        Vector knockback = playerLocation.toVector()
                .subtract(source.toVector());

        knockback.setY(0);

        if (knockback.lengthSquared() < 1.0E-6) {
            knockback = playerLocation.getDirection().setY(0);
        }

        if (knockback.lengthSquared() < 1.0E-6) {
            knockback = new Vector(1, 0, 0);
        }

        knockback.normalize().multiply(force);

        knockback.setY(-0.15);

        entity.setVelocity(knockback);
    }

    public static boolean limitPassed() {
        return entityCount >= MAX_ENTITY;
    }
}
