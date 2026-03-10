package fr.openmc.core.features.dream.models.registry;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.generator.structure.Structure;
import org.jetbrains.annotations.NotNull;

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
}
