package fr.openmc.core.features.dream.registries;

import fr.openmc.core.utils.text.messages.TranslationManager;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Getter
public enum DreamBiome {

    SCULK_PLAINS(
            "feature.dream.biome.sculk_plains",
            NamespacedKey.fromString("omc_dream:sculk_plains")
    ),
    SOUL_FOREST(
            "feature.dream.biome.soul_forest",
            NamespacedKey.fromString("omc_dream:soul_forest")
    ),
    MUD_BEACH(
            "feature.dream.biome.mud_beach",
            NamespacedKey.fromString("omc_dream:mud_beach")
    ),
    CLOUD_LAND(
            "feature.dream.biome.cloud_land",
            NamespacedKey.fromString("omc_dream:cloud_land")
    ),
    GLACITE_GROTTO(
            "feature.dream.biome.glacite_grotto",
            NamespacedKey.fromString("omc_dream:glacite_grotto")
    );

    private final Registry<@NotNull Biome> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME);
    private final String nameKey;
    private final NamespacedKey biomeKey;
    private final Biome biome;

    DreamBiome(String nameKey, NamespacedKey biomeKey) {
        this.nameKey = nameKey;
        this.biomeKey = biomeKey;
        this.biome = registry.get(biomeKey);
    }

    public Component getName() {
        return TranslationManager.translation(nameKey);
    }

    public Component getSmallName() {
        return TranslationManager.translation(nameKey + ".to_small");
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
