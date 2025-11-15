package fr.openmc.core.features.dream.generation.structures;

import com.sk89q.worldedit.math.BlockVector3;
import fr.openmc.core.features.dream.DreamUtils;
import lombok.Getter;
import org.bukkit.Location;

import java.util.Objects;

public record DreamStructure(DreamType type, BlockVector3 min, BlockVector3 max) {

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

    public static DreamStructure fromString(String s) {
        try {
            String[] split = s.split(";");
            String typeId = split[0];

            double minX = Double.parseDouble(split[1]);
            double minY = Double.parseDouble(split[2]);
            double minZ = Double.parseDouble(split[3]);
            double maxX = Double.parseDouble(split[4]);
            double maxY = Double.parseDouble(split[5]);
            double maxZ = Double.parseDouble(split[6]);

            return new DreamStructure(
                    DreamType.fromId(typeId),
                    BlockVector3.at(minX, minY, minZ),
                    BlockVector3.at(maxX, maxY, maxZ)
            );
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return type.getId() + ";" +
                min.x() + ";" + min.y() + ";" + min.z() + ";" +
                max.x() + ";" + max.y() + ";" + max.z();
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