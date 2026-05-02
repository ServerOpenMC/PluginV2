package fr.openmc.core.commands.utils;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.listeners.RespawnListener;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class SpawnManager extends Feature implements HasCommands, HasListeners {

    private static File spawnFile;
    private static FileConfiguration spawnConfig;
    @Getter private static Location spawnLocation;

    @Override
    public void init() {
        spawnFile = new File(OMCPlugin.getInstance().getDataFolder() + "/data", "spawn.yml");
        loadSpawnConfig();
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new Spawn(),
                new SetSpawn()
        );
    }

    @Override
    public Set<Listener> getListeners() {
        return Set.of(
                new RespawnListener()
        );
    }

    private static void loadSpawnConfig() {
        if(!spawnFile.exists()) {
            spawnFile.getParentFile().mkdirs();
            OMCPlugin.getInstance().saveResource("data/spawn.yml", false);
        }

        spawnConfig = YamlConfiguration.loadConfiguration(spawnFile);
        loadSpawnLocation();
    }

    private static void loadSpawnLocation() {
        if (spawnConfig.contains("spawn")) {
            World world = OMCPlugin.getInstance().getServer().getWorld(spawnConfig.getString("spawn.world", "world"));
            double x = spawnConfig.getDouble("spawn.x", 0.0);
            double z = spawnConfig.getDouble("spawn.z", 0.0);

            spawnLocation = new Location(
                    world,
                    x,
                    spawnConfig.getDouble("spawn.y", world.getHighestBlockYAt((int) x, (int) z) + 1),
                    z,
                    (float) spawnConfig.getDouble("spawn.yaw", 0.0),
                    (float) spawnConfig.getDouble("spawn.pitch", 0.0)
            );
        }
    }

    public static void setSpawn(Location location) {
        spawnLocation = location;
        spawnConfig.set("spawn.world", location.getWorld().getName());
        spawnConfig.set("spawn.x", location.getX());
        spawnConfig.set("spawn.y", location.getY());
        spawnConfig.set("spawn.z", location.getZ());
        spawnConfig.set("spawn.yaw", location.getYaw());
        spawnConfig.set("spawn.pitch", location.getPitch());
        saveSpawnConfig();
    }

    private static void saveSpawnConfig() {
        try {
            spawnConfig.save(spawnFile);
        } catch (IOException e) {
            OMCPlugin.getInstance().getSLF4JLogger().warn("Failed to save spawn configuration file: {}", e.getMessage(), e);
        }
    }
}
