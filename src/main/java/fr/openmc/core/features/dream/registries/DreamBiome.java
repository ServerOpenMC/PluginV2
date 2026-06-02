package fr.openmc.core.features.dream.registries;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Getter
public enum DreamBiome {

    SCULK_PLAINS(
            "§3Plaine de Sculk",
            NamespacedKey.fromString("omc_dream:sculk_plains")
    ),
    SOUL_FOREST(
            "§5Forêt des Âmes",
            NamespacedKey.fromString("omc_dream:soul_forest")
    ),
    MUD_BEACH(
            "§8Plage de boue",
            NamespacedKey.fromString("omc_dream:mud_beach")
    ),
    CLOUD_LAND(
            "§fVallée des Nuages",
            NamespacedKey.fromString("omc_dream:cloud_land")
    ),
    GLACITE_GROTTO(
            "§bGrotte glacée",
            NamespacedKey.fromString("omc_dream:glacite_grotto")
    );

    private final Registry<@NotNull Biome> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME);
    private final String name;
    private final NamespacedKey biomeKey;
    private final Biome biome;

    DreamBiome(String name, NamespacedKey biomeKey) {
        this.name = name;
        this.biomeKey = biomeKey;
        this.biome = registry.get(biomeKey);
    }

    public static boolean isDreamBiome(Location loc, DreamBiome dreamBiome) {
        return loc.getBlock().getBiome().equals(dreamBiome.getBiome());
    }

    public static boolean isInDreamBiome(Player player, DreamBiome dreamBiome) {
        return player.getLocation().getBlock().getBiome() == dreamBiome.getBiome();
    }

    public static DreamBiome getDreamBiome(Player player) {
        for (DreamBiome dreamBiome : DreamBiome.values()) {
            if (!dreamBiome.getBiome().equals(player.getLocation().getBlock().getBiome())) continue;

            return dreamBiome;
        }

        return DreamBiome.SCULK_PLAINS;
    }
}
