package fr.openmc.core.features.dream.registries;

import fr.openmc.core.features.dream.models.registry.DreamBiome;
import fr.openmc.core.features.dream.models.registry.DreamStructure;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DreamBiomesRegistry {
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
