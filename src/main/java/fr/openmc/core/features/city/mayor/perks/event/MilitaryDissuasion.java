package fr.openmc.core.features.city.mayor.perks.event;

import com.sk89q.worldedit.math.BlockVector2;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.stream.Collectors;

public class MilitaryDissuasion implements Listener {

    /**
     * Spawns Iron Golems in the specified city.
     *
     * @param city           The city where the golems will be spawned.
     * @param golemsToSpawn  The number of golems to spawn.
     */
    public static void spawnIronMan(City city, int golemsToSpawn) {
        Set<BlockVector2> chunks = city.getChunks();
        if (chunks.isEmpty()) return;

        World world = Bukkit.getWorld("world");
        if (world == null) return;

        List<BlockVector2> chunkList = new ArrayList<>(chunks);
        Random random = new Random();

        int spawned = 0;
        int attempts = 0;

        while (spawned < golemsToSpawn && attempts < golemsToSpawn * 10) {
            attempts++;

            BlockVector2 chunk = chunkList.get(spawned % chunkList.size());

            int chunkX = chunk.getBlockX();
            int chunkZ = chunk.getBlockZ();

            int x = (chunkX << 4) + 8 + random.nextInt(8);
            int z = (chunkZ << 4) + 8 + random.nextInt(8);
            int y = world.getHighestBlockYAt(x, z);

            Material ground = world.getBlockAt(x, y - 1, z).getType();
            if (!ground.isSolid() || ground == Material.WATER || ground == Material.LAVA) {
                continue;
            }

            Location spawnLocation = new Location(world, x + 0.5, y, z + 0.5);

            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                IronGolem golem = (IronGolem) world.spawnEntity(spawnLocation, EntityType.IRON_GOLEM);
                golem.customName(Component.text("Défendeur de " + city.getName()));
                golem.setLootTable(null);
                golem.setGlowing(true);
                golem.setHealth(35);

                PersistentDataContainer pdc = golem.getPersistentDataContainer();
                pdc.set(new NamespacedKey(OMCPlugin.getInstance(), "city_golem"), PersistentDataType.STRING, city.getUUID());

                golem.setAI(false);
                golem.setTarget(null);

                Bukkit.getScheduler().runTaskTimer(OMCPlugin.getInstance(), () -> {
                    if (!golem.isValid() || golem.isDead()) return;

                    List<Player> nearbyEnemies = golem.getNearbyEntities(15, 15, 15).stream()
                            .filter(ent -> ent instanceof Player)
                            .map(ent -> (Player) ent)
                            .filter(nearbyPlayer -> {
                                System.out.println(nearbyPlayer);
                                if (nearbyPlayer.getGameMode() != GameMode.SURVIVAL && nearbyPlayer.getGameMode() != GameMode.ADVENTURE) {
                                    return false;
                                }
                                System.out.println("good " + nearbyPlayer);
                                City playerCity = CityManager.getPlayerCity(nearbyPlayer.getUniqueId());
                                return playerCity == null || !playerCity.getUUID().equals(city.getUUID());
                            })
                            .collect(Collectors.toList());
                    System.out.println(nearbyEnemies.isEmpty());
                    if (!nearbyEnemies.isEmpty()) {
                        System.out.println(nearbyEnemies);
                        Collections.shuffle(nearbyEnemies);
                        Player target = nearbyEnemies.get(0);
                        golem.setAI(true);
                        golem.setTarget(target);
                    } else {
                        golem.setAI(false);
                        golem.setTarget(null);
                    }
                }, 0L, 20L);

            });
            spawned++;
        }
    }


    /**
     * Clears all Iron Golems in the specified city.
     *
     * @param city The city whose golems will be cleared.
     */
    public static void clearCityGolems(City city) {
        NamespacedKey key = new NamespacedKey(OMCPlugin.getInstance(), "city_golem");
        String cityUUID = city.getUUID();

        for (Entity entity : Bukkit.getWorld("world").getEntitiesByClass(IronGolem.class)) {
            PersistentDataContainer pdc = entity.getPersistentDataContainer();
            if (!pdc.has(key, PersistentDataType.STRING)) continue;

            String storedUUID = pdc.get(key, PersistentDataType.STRING);
            if (storedUUID != null && storedUUID.equals(cityUUID)) {
                entity.remove();
            }
        }
    }

    /**
     * Clears all Iron Golems in the world.
     */
    private void clearAllGolems() {
        NamespacedKey key = new NamespacedKey(OMCPlugin.getInstance(), "city_golem");

        for (Entity entity : Bukkit.getWorld("world").getEntitiesByClass(IronGolem.class)) {
            PersistentDataContainer pdc = entity.getPersistentDataContainer();
            if (!pdc.has(key, PersistentDataType.STRING)) continue;

            entity.remove();
        }
    }

    private static boolean cleared = false;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (cleared) return;
        cleared = true;

        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), this::clearAllGolems, 20L * 5);
    }

}
