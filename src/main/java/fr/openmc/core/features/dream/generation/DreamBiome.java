package fr.openmc.core.features.dream.generation;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;

import static fr.openmc.core.utils.messages.MessagesManager.textToSmall;

@Getter
public enum DreamBiome {

    SCULK_PLAINS(
            Component.text(textToSmall("Plaine de Sculk"), NamedTextColor.DARK_AQUA),
            NamespacedKey.fromString("openmc:sculk_plains")
    ),
    SOUL_FOREST(
            Component.text(textToSmall("Forêt des Âmes"), NamedTextColor.DARK_PURPLE),
            NamespacedKey.fromString("openmc:soul_forest")
    ),
    MUD_BEACH(
            Component.text(textToSmall("Plage de boue"), NamedTextColor.DARK_GRAY),
            NamespacedKey.fromString("openmc:mud_beach")
    ),
    CLOUD_LAND(
            Component.text(textToSmall("§fVallée des Nuages"), NamedTextColor.WHITE),
            NamespacedKey.fromString("openmc:cloud_land")
    ),
    GLACITE_GROTTO(
            Component.text(textToSmall("§bGrotte glacée"), NamedTextColor.AQUA),
            NamespacedKey.fromString("openmc:glacite_grotto")
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
