package fr.openmc.api.datapacks.injectors;

import fr.openmc.api.datapacks.DatapackInjector;
import fr.openmc.api.datapacks.builders.dimensions.DimensionBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DimensionInjector implements DatapackInjector {

    private final String namespace;
    private final String id;
    private final DimensionBuilder builder;

    public DimensionInjector(String namespace, String id, DimensionBuilder builder) {
        this.namespace = namespace;
        this.id = id;
        this.builder = builder;
    }

    @Override
    public void inject(File rootFile) {
        if (builder == null) return;

        Path root = rootFile.toPath().resolve("data").resolve(namespace)
                .resolve("dimension");
        try {
            Files.createDirectories(root);
            Path dimensionFile = root.resolve(id + ".json");
            Files.createDirectories(dimensionFile.getParent());
            Files.writeString(dimensionFile, GSON.toJson(builder.toJson()));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write dimension files", e);
        }
    }

    public String getKey() {
        return namespace + ":" + id;
    }
}
