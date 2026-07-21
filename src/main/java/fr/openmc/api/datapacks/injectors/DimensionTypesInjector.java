package fr.openmc.api.datapacks.injectors;

import fr.openmc.api.datapacks.DatapackInjector;
import fr.openmc.api.datapacks.builders.DimensionTypeBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Classe qui représente les données trouvable dans un dimension type
 * Qui injecte directement cela sous forme .json dans le datapack
 */
public class DimensionTypesInjector implements DatapackInjector {

    private final String namespace;
    private final Map<String, DimensionTypeBuilder> entries = new LinkedHashMap<>();

    public DimensionTypesInjector(String namespace) {
        this.namespace = namespace;
    }

    public DimensionTypesInjector add(String id, Consumer<DimensionTypeBuilder> builder) {
        DimensionTypeBuilder instance = new DimensionTypeBuilder();
        builder.accept(instance);
        entries.put(id, instance);
        return this;
    }

    public DimensionTypesInjector add(String id, DimensionTypeBuilder builder) {
        entries.put(id, builder);
        return this;
    }

    @Override
    public void inject(File rootFile) {
        if (entries.isEmpty()) return;

        Path root = rootFile.toPath().resolve("data").resolve(namespace).resolve("dimension_type");
        try {
            Files.createDirectories(root);
            for (var entry : entries.entrySet()) {
                Path dimensionTypeFile = root.resolve(entry.getKey() + ".json");
                Files.writeString(dimensionTypeFile, GSON.toJson(entry.getValue().toJson()));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write dimension_type files", e);
        }
    }
}
