package fr.openmc.api.datapacks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.openmc.api.datapacks.builders.ContentBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public interface DatapackInjector {
    Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    String[] getPath();
    ContentBuilder getBuilder();
    String getNamespace();
    String getId();
    String getExtension();

    default void inject(File rootFile) {
        if (getBuilder() == null) return;

        Path root = rootFile.toPath().resolve("data").resolve(getNamespace());

        for (String folder : getPath()) {
            root = root.resolve(folder);
        }

        try {
            Files.createDirectories(root);
            Path biomeFile = root.resolve(getId() + "." + getExtension());
            Files.createDirectories(biomeFile.getParent());
            Files.writeString(biomeFile, GSON.toJson(getBuilder().toJson()));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write Content files", e);
        }
    }

    default String getKey() {
        return getNamespace() + ":" + getId();
    }
}
