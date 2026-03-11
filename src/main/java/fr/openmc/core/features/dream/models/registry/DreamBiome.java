package fr.openmc.core.features.dream.models.registry;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static fr.openmc.core.utils.messages.MessagesManager.textToSmall;

@Getter
public enum DreamBiome {

    SCULK_PLAINS(
            Component.text(textToSmall("§3Plaine de Sculk")),
            NamespacedKey.fromString("omc_dream:sculk_plains")
    ),
    SOUL_FOREST(
            Component.text(textToSmall("§5Forêt des Âmes")),
            NamespacedKey.fromString("omc_dream:soul_forest")
    ),
    MUD_BEACH(
            Component.text(textToSmall("§8Plage de boue")),
            NamespacedKey.fromString("omc_dream:mud_beach")
    ),
    CLOUD_LAND(
            Component.text(textToSmall("§fVallée des Nuages")),
            NamespacedKey.fromString("omc_dream:cloud_land")
    ),
    GLACITE_GROTTO(
            Component.text(textToSmall("§bGrotte glacée")),
            NamespacedKey.fromString("omc_dream:glacite_grotto")
    );

    private final Registry<@NotNull Biome> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME);
    private final Component name;
    private final NamespacedKey biomeKey;
    private final Biome biome;

    DreamBiome(Component name, NamespacedKey biomeKey) {
        this.name = name;
        this.biomeKey = biomeKey;
        this.biome = registry.get(biomeKey);
    }
}
