package fr.openmc.core.features.dream.registries;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.generator.structure.GeneratedStructure;
import org.bukkit.generator.structure.Structure;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

@Getter
public enum DreamStructure {

    BASE_CAMP(
            Component.text("§bCamp de Grotte"),
            NamespacedKey.fromString("omc_dream:glacite_grotto/base_camp")
    ),
    CUBE_TEMPLE(
            Component.text("§5Temple du Cube"),
            NamespacedKey.fromString("omc_dream:soul_forest/cube_temple")
    ),
    CLOUD_CASTLE(
            Component.text("§7Château des Nuages"),
            NamespacedKey.fromString("omc_dream:cloud_land/cloud_castle")
    )
    ;

    private final Registry<@NotNull Structure> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.STRUCTURE);
    private final Component name;
    private final NamespacedKey structureKey;
    private final Structure structure;

    DreamStructure(Component name, NamespacedKey structureKey) {
        this.name = name;
        this.structureKey = structureKey;
        this.structure = registry.get(structureKey);
    }

    public static boolean isInInsideDreamStructure(Location location, DreamStructure dreamStructure) {
        return !location.getChunk().getStructures(dreamStructure.getStructure()).isEmpty();
    }
    public static boolean isInInsideDreamStructure(Player player, DreamStructure dreamStructure) {
        return isInInsideDreamStructure(player.getLocation(), dreamStructure);
    }

    public static DreamStructure getDreamStructureAt(Location location) {
        for (DreamStructure structure : DreamStructure.values()) {
            Collection<GeneratedStructure> structures = location.getChunk().getStructures(structure.getStructure());

            if (structures.isEmpty()) continue;

            for (GeneratedStructure s : structures) {
                BoundingBox boundingBox = s.getBoundingBox();
                if (boundingBox != null && boundingBox.contains(location.toVector())) {
                    return structure;
                }
            }
        }

        return null;
    }

    public static DreamStructure getDreamStructure(Player player) {
        return getDreamStructureAt(player.getLocation());
    }
}
