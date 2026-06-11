package fr.openmc.api.datapacks.injectors;

import fr.openmc.api.datapacks.DatapackInjector;
import fr.openmc.api.datapacks.builders.TimelineBuilder;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

@Getter
/**
 * Classe qui représente les données trouvable dans une timeline
 * Qui injecte directement cela sous forme .json dans le datapack
 * https://minecraft.wiki/w/Timeline
 */
public class TimelinesInjector implements DatapackInjector {

    private final String namespace;
    private String id;
    private final Map<String, TimelineBuilder> entries = new LinkedHashMap<>();

    public TimelinesInjector(String namespace) {
        this.namespace = namespace;
    }

    public TimelinesInjector add(String id, Consumer<TimelineBuilder> builder) {
        TimelineBuilder instance = new TimelineBuilder();
        builder.accept(instance);
        entries.put(id, instance);
        this.id = id;
        return this;
    }

    public TimelinesInjector add(String id, TimelineBuilder builder) {
        entries.put(id, builder);
        this.id = id;
        return this;
    }

    @Override
    public void inject(File rootFile) {
        if (entries.isEmpty()) return;

        Path root = rootFile.toPath().resolve("data").resolve(namespace).resolve("timeline");
        try {
            Files.createDirectories(root);
            for (var entry : entries.entrySet()) {
                Path file = root.resolve(entry.getKey() + ".json");
                Files.writeString(file, GSON.toJson(entry.getValue().toJson()));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write timeline files", e);
        }
    }
}
