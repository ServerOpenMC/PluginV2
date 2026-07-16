package fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight;

import fr.openmc.core.utils.world.LocationUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BloodyNightRaidManager {

    private static final int MIN_MOBS_PER_RAID = 7;
    private static final int MAX_MOBS_PER_RAID = 14;

    private static final int MAX_SPAWN_RADIUS = 15;

    private static final List<EntityType> BLOODY_MONSTERS_AVAILABLE = List.of(
            EntityType.ZOMBIE,
            EntityType.SKELETON,
            EntityType.SPIDER,
            EntityType.CREEPER,
            EntityType.HUSK,
            EntityType.STRAY
    );

    public static void startRaid(World world) {
        for (Player player : world.getPlayers()) {
            if (player.getGameMode() == GameMode.SPECTATOR ||
                    player.getGameMode() == GameMode.CREATIVE) continue;
            if (player.isDead()) continue;

            spawnRaidAroundPlayer(player);
        }
    }

    private static void spawnRaidAroundPlayer(Player player) {
        World world = player.getWorld();

        int mobAmount = ThreadLocalRandom.current().nextInt(
                MIN_MOBS_PER_RAID,
                MAX_MOBS_PER_RAID + 1
        );

        for (int i = 0; i < mobAmount; i++) {
            Location spawnLocation = LocationUtils.getSafeNearbySurface(
                    LocationUtils.randomLocation(
                            player.getLocation(),
                            MAX_SPAWN_RADIUS
                    ),
                    20);

            world.strikeLightningEffect(spawnLocation);
            spawnRandomMonster(spawnLocation);
        }
    }

    private static void spawnRandomMonster(Location location) {
        EntityType type = getRandomRaidMonsterType();
        location.getWorld().spawnEntity(location, type, CreatureSpawnEvent.SpawnReason.CUSTOM,s ->
                s.getPersistentDataContainer().set(
                        BloodyNightManager.RAID_MONSTER_KEY, PersistentDataType.BOOLEAN, true));

    }

    private static EntityType getRandomRaidMonsterType() {
        return BLOODY_MONSTERS_AVAILABLE.get(ThreadLocalRandom.current().nextInt(BLOODY_MONSTERS_AVAILABLE.size()));
    }
}
