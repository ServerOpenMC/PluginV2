package fr.openmc.core.features.dream.generation;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;

@Getter
public enum DreamBiome {

    SCULK_PLAINS("§3Plaine de Sculk", NamespacedKey.fromString("openmc:sculk_plains")),
    SOUL_FOREST("§5Forêt des Ames", NamespacedKey.fromString("openmc:soul_forest")),
    MUD_BEACH("§8Plage de boue", NamespacedKey.fromString("openmc:mud_beach")),
    CLOUD_LAND("§fVallée des Nuages", NamespacedKey.fromString("openmc:cloud_land")),
    GLACITE_GROTTO("§bGrotte glacée", NamespacedKey.fromString("openmc:glacite_grotto"));

    private final Registry<@NotNull Biome> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME);
    private final String name;
    private final NamespacedKey biomeKey;
    private final Biome biome;

    DreamBiome(String name, NamespacedKey biomeKey) {
        this.name = name;
        this.biomeKey = biomeKey;
        this.biome = registry.get(biomeKey);
    }
}
