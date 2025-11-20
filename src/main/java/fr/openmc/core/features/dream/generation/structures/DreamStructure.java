package fr.openmc.core.features.dream.generation.structures;

import com.sk89q.worldedit.math.BlockVector3;
import fr.openmc.core.features.dream.DreamUtils;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SerializableAs("DreamStructure")
public record DreamStructure(DreamType type, BlockVector3 min, BlockVector3 max) implements ConfigurationSerializable {

    public DreamStructure(Map<String, Object> map) {
        this(
                DreamType.fromId((String) map.get("type")),
                BlockVector3.at(
                        (int) map.get("min_x"),
                        (int) map.get("min_y"),
                        (int) map.get("min_z")
                ),
                BlockVector3.at(
                        (int) map.get("max_x"),
                        (int) map.get("max_y"),
                        (int) map.get("max_z")
                )
        );
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("type", type.getId());

        map.put("min_x", min.x());
        map.put("min_y", min.y());
        map.put("min_z", min.z());

        map.put("max_x", max.x());
        map.put("max_y", max.y());
        map.put("max_z", max.z());

        return map;
    }

    public boolean isInside(Location loc) {
        if (!DreamUtils.isDreamWorld(loc)) return false;

        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();

        double minX = Math.min(min.x(), max.x());
        double maxX = Math.max(min.x(), max.x());
        double minY = Math.min(min.y(), max.y());
        double maxY = Math.max(min.y(), max.y());
        double minZ = Math.min(min.z(), max.z());
        double maxZ = Math.max(min.z(), max.z());

        return x >= minX && x <= maxX
                && y >= minY && y <= maxY
                && z >= minZ && z <= maxZ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DreamStructure that)) return false;
        return min.equals(that.min) && max.equals(that.max);
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max);
    }

    @Getter
    public enum DreamType {
        BASE_CAMP("base_camp", "§bCamp de Grotte"),
        SOUL_ALTAR("soul_altar", "§5Temple du Cube"),
        CLOUD_CASTLE("cloud_castle", "§7Château des Nuages");

        private final String id;
        private final String name;

        DreamType(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public static DreamType fromId(String id) {
            for (DreamType type : values()) {
                if (type.id.equalsIgnoreCase(id)) return type;
            }
            return null;
        }
    }
}