package fr.openmc.api.datapacks.injectors;

import fr.openmc.api.datapacks.DatapackInjector;
import fr.openmc.api.datapacks.builders.TimelineBuilder;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

@Getter
/**
 * Classe qui représente les données trouvable dans une timeline
 * Qui injecte directement cela sous forme .json dans le datapack
 * https://minecraft.wiki/w/Timeline
 */
public class TimelinesInjector implements DatapackInjector {

    private final String namespace;
    private final String id;
    private final TimelineBuilder builder;

    public TimelinesInjector(String namespace, String id, TimelineBuilder builder) {
        this.namespace = namespace;
        this.id = id;
        this.builder = builder;
    }

    public TimelinesInjector(String namespace, String id, Consumer<TimelineBuilder> builder) {
        this.namespace = namespace;
        this.id = id;
        this.builder = new TimelineBuilder();
        builder.accept(this.builder);
    }

    @Override
    public void inject(File rootFile) {
        if (builder == null) return;

        Path root = rootFile.toPath().resolve("data").resolve(namespace).resolve("timeline");
        try {
            Files.createDirectories(root);

            Path file = root.resolve(id + ".json");
            Files.createDirectories(file.getParent());
            Files.writeString(file, GSON.toJson(builder.toJson()));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write timeline files", e);
        }
    }

    public String getKey() {
        return namespace + ":" + id;
    }
}
