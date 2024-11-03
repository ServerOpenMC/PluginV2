package fr.openmc.core.commands.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.openmc.core.OMCPlugin;
import lombok.Getter;

public class SpawnManager {

    private final OMCPlugin plugin;
    private final File spawnFile;
    private FileConfiguration spawnConfig;
    @Getter private Location spawnLocation;
    @Getter static SpawnManager instance;

    public SpawnManager(OMCPlugin plugin) {
        instance = this;
        this.plugin = plugin;
        this.spawnFile = new File(plugin.getDataFolder() + "/data", "spawn.yml");
        loadSpawnConfig();
    }

    private void loadSpawnConfig() {
        if(!spawnFile.exists()) {
            spawnFile.getParentFile().mkdirs();
            plugin.saveResource("data/spawn.yml", false);
        }

        spawnConfig = YamlConfiguration.loadConfiguration(spawnFile);
        loadSpawnLocation();
    }

    private void loadSpawnLocation() {
        if (spawnConfig.contains("spawn")) {
            this.spawnLocation = new Location(
                plugin.getServer().getWorld(spawnConfig.getString("spawn.world", "world")),
                spawnConfig.getDouble("spawn.x", 0.0),
                spawnConfig.getDouble("spawn.y", 0.0),
                spawnConfig.getDouble("spawn.z", 0.0),
                (float) spawnConfig.getDouble("spawn.yaw", 0.0),
                (float) spawnConfig.getDouble("spawn.pitch", 0.0)
            );
        }
    }

    public void setSpawn(Location location) {
        this.spawnLocation = location;
        spawnConfig.set("spawn.world", location.getWorld().getName());
        spawnConfig.set("spawn.x", location.getX());
        spawnConfig.set("spawn.y", location.getY());
        spawnConfig.set("spawn.z", location.getZ());
        spawnConfig.set("spawn.yaw", location.getYaw());
        spawnConfig.set("spawn.pitch", location.getPitch());
        saveSpawnConfig();
    }

    private void saveSpawnConfig() {
        try {
            spawnConfig.save(spawnFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossible de sauvegarder le fichier de configuration de spawn");
            e.printStackTrace();
        }
    }
    
}
