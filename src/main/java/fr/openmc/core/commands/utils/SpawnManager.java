package fr.openmc.core.commands.utils;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.lifecycle.integration.OMCLogger;
import fr.openmc.core.lifecycle.interfaces.HasCommands;
import fr.openmc.core.lifecycle.interfaces.HasListeners;
import fr.openmc.core.lifecycle.listeners.ListenerFactory;
import fr.openmc.core.listeners.RespawnListener;
import fr.openmc.core.registry.features.Feature;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class SpawnManager extends Feature implements HasCommands, HasListeners {

    private File spawnFile;
    private FileConfiguration spawnConfig;
    @Getter
    private Location spawnLocation;

    @Override
    public void onEnable() {
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
    public Set<ListenerFactory> getListeners() {
        return Set.of(RespawnListener::new);
    }

    private void loadSpawnConfig() {
        if (!spawnFile.exists()) {
            spawnFile.getParentFile().mkdirs();
            OMCPlugin.getInstance().saveResource("data/spawn.yml", false);
        }

        spawnConfig = YamlConfiguration.loadConfiguration(spawnFile);
        loadSpawnLocation();
    }

    private void loadSpawnLocation() {
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

    public void setSpawn(Location location) {
        spawnLocation = location;
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
            OMCLogger.warn("Failed to save spawn configuration file: {}", e.getMessage(), e);
        }
    }
}
