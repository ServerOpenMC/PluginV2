package fr.openmc.api.datapacks.injectors;

import fr.openmc.api.datapacks.DatapackInjector;
import fr.openmc.api.datapacks.builders.ContentBuilder;
import fr.openmc.api.datapacks.builders.DimensionTypeBuilder;

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
    public String[] getPath() {
        return new String[]{"worldgen", "dimension_type"};
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
