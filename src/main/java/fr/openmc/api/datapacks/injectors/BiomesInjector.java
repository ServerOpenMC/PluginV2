package fr.openmc.api.datapacks.injectors;

import fr.openmc.api.datapacks.DatapackInjector;
import fr.openmc.api.datapacks.builders.BiomeBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Classe qui représente les données trouvable dans un biome
 * Qui injecte directement cela sous forme .json dans le datapack
 * <a href="https://minecraft.wiki/w/Biome_definition">...</a>
 */
public class BiomesInjector implements DatapackInjector {

    private final String namespace;
    private final Map<String, BiomeBuilder> entries = new LinkedHashMap<>();

    public BiomesInjector(String namespace) {
        this.namespace = namespace;
    }

    public BiomesInjector add(String id, Consumer<BiomeBuilder> builder) {
        BiomeBuilder instance = new BiomeBuilder();
        builder.accept(instance);
        entries.put(id, instance);
        return this;
    }

    public BiomesInjector add(String id, BiomeBuilder builder) {
        entries.put(id, builder);
        return this;
    }

    @Override
    public void inject(File rootFile) {
        if (entries.isEmpty()) return;

        Path root = rootFile.toPath().resolve("data").resolve(namespace)
                .resolve("worldgen").resolve("biome");
        try {
            Files.createDirectories(root);
            for (var entry : entries.entrySet()) {
                Path biomeFile = root.resolve(entry.getKey() + ".json");
                Files.createDirectories(biomeFile.getParent());
                Files.writeString(biomeFile, GSON.toJson(entry.getValue().toJson()));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write biome files", e);
        }
    }
}
