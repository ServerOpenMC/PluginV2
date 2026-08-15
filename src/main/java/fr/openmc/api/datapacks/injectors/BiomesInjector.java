package fr.openmc.api.datapacks.injectors;

import fr.openmc.api.datapacks.DatapackInjector;
import fr.openmc.api.datapacks.builders.BiomeBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Classe qui représente les données trouvable dans un biome
 * Qui injecte directement cela sous forme .json dans le datapack
 * <a href="https://minecraft.wiki/w/Biome_definition">...</a>
 */
public class BiomesInjector implements DatapackInjector {

    private final String namespace;
    private final String id;
    private final BiomeBuilder builder;

    public BiomesInjector(String namespace, String id, BiomeBuilder builder) {
        this.namespace = namespace;
        this.id = id;
        this.builder = builder;
    }

    public BiomesInjector(String namespace, String id, Consumer<BiomeBuilder> builder) {
        this.namespace = namespace;
        this.id = id;
        this.builder = new BiomeBuilder();
        builder.accept(this.builder);
    }

    @Override
    public void inject(File rootFile) {
        if (builder == null) return;

        Path root = rootFile.toPath().resolve("data").resolve(namespace)
                .resolve("worldgen").resolve("biome");
        try {
            Files.createDirectories(root);
            Path biomeFile = root.resolve(id + ".json");
            Files.createDirectories(biomeFile.getParent());
            Files.writeString(biomeFile, GSON.toJson(builder.toJson()));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write biome files", e);
        }
    }

    public String getKey() {
        return namespace + ":" + id;
    }
}
