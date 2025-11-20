package fr.openmc.core.features.dream.models.registry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("DreamBlock")
public record DreamBlock(String type, Location location) implements ConfigurationSerializable {
    public DreamBlock(Map<String, Object> map) {
        this(
                (String) map.get("type"),
                new Location(
                        Bukkit.getWorld((String) map.get("world_name")),
                        (int) map.get("x"),
                        (int) map.get("y"),
                        (int) map.get("z")
                )
        );
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("type", type);

        map.put("world_name", location.getWorld().getName());
        map.put("x", location.getBlockX());
        map.put("y", location.getBlockY());
        map.put("z", location.getBlockZ());

        return map;
    }
}