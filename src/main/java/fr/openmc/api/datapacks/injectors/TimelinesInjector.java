package fr.openmc.api.datapacks.injectors;

import fr.openmc.api.datapacks.DatapackInjector;
import fr.openmc.api.datapacks.builders.ContentBuilder;
import fr.openmc.api.datapacks.builders.TimelineBuilder;
import lombok.Getter;

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
    public String[] getPath() {
        return new String[]{"timeline"};
    }

    @Override
    public ContentBuilder getBuilder() {
        return builder;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getExtension() {
        return "json";
    }
}
