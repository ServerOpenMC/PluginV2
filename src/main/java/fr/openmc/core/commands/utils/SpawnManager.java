package fr.openmc.core.commands.utils;

import fr.openmc.core.OMCPlugin;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class SpawnManager {

    private static File spawnFile;
    private static FileConfiguration spawnConfig;
    @Getter private static Location spawnLocation;

    public SpawnManager() {
        spawnFile = new File(OMCPlugin.getInstance().getDataFolder() + "/data", "spawn.yml");
        loadSpawnConfig();
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
            spawnLocation = new Location(
                OMCPlugin.getInstance().getServer().getWorld(spawnConfig.getString("spawn.world", "world")),
                spawnConfig.getDouble("spawn.x", 0.0),
                spawnConfig.getDouble("spawn.y", 0.0),
                spawnConfig.getDouble("spawn.z", 0.0),
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
