package fr.openmc.api.datapacks.injectors;

import fr.openmc.api.datapacks.DatapackInjector;
import fr.openmc.api.datapacks.builders.ContentBuilder;

public class DimensionInjector implements DatapackInjector {

    private final String namespace;
    private final String id;
    private final ContentBuilder builder;

    public DimensionInjector(String namespace, String id, ContentBuilder builder) {
        this.namespace = namespace;
        this.id = id;
        this.builder = builder;
    }

    @Override
    public String[] getPath() {
        return new String[]{"dimension"};
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
