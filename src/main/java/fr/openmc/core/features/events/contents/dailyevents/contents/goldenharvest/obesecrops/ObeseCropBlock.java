package fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.obesecrops;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.LinkedHashMap;
import java.util.Map;

public record ObeseCropBlock(Location location) implements ConfigurationSerializable {

    public ObeseCropBlock(Map<String, Object> map) {
        this((Location) map.get("location"));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("location", location);
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObeseCropBlock other)) return false;
        return location.equals(other.location);
    }

    @Override
    public int hashCode() {
        return location.hashCode();
    }
}