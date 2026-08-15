package fr.openmc.api.datapacks.injectors;

import fr.openmc.api.datapacks.DatapackInjector;
import fr.openmc.api.datapacks.builders.DimensionTypeBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Classe qui représente les données trouvable dans un dimension type
 * Qui injecte directement cela sous forme .json dans le datapack
 */
public class DimensionTypesInjector implements DatapackInjector {

    private final String namespace;
    private final String id;
    private final DimensionTypeBuilder builder;

    public DimensionTypesInjector(String namespace, String id, DimensionTypeBuilder builder) {
        this.namespace = namespace;
        this.id = id;
        this.builder = builder;
    }

    public DimensionTypesInjector(String namespace, String id, Consumer<DimensionTypeBuilder> builder) {
        this.namespace = namespace;
        this.id = id;
        this.builder = new DimensionTypeBuilder();
        builder.accept(this.builder);
    }

    @Override
    public void inject(File rootFile) {
        if (builder == null) return;

        Path root = rootFile.toPath().resolve("data").resolve(namespace).resolve("dimension_type");
        try {
            Files.createDirectories(root);

            Path dimensionTypeFile = root.resolve(id + ".json");
            Files.createDirectories(dimensionTypeFile.getParent());
            Files.writeString(dimensionTypeFile, GSON.toJson(builder.toJson()));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write dimension_type files", e);
        }
    }

    public String getKey() {
        return namespace + ":" + id;
    }
}
