package fr.openmc.core.features.dream.generation;

import lombok.Getter;
import org.bukkit.block.Biome;

@Getter
public enum DreamBiome {
    SCULK_PLAINS("§3Plaine de Sculk", Biome.PLAINS),
    SOUL_FOREST("§5Forêt des Ames", Biome.FOREST),
    MUD_BEACH("§8Plage de boue", Biome.BEACH),
    CLOUD_LAND("§fVallée des Nuages", Biome.THE_VOID),
    GLACITE_GROTTO("§bGrotte glacée", Biome.DEEP_DARK);

    private final String name;
    private final Biome biome;

    DreamBiome(String name, Biome biome) {
        this.name = name;
        this.biome = biome;
    }
}
