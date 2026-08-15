package fr.openmc.api.datapacks.injectors;

import fr.openmc.api.datapacks.DatapackInjector;
import fr.openmc.api.datapacks.builders.BiomeBuilder;
import fr.openmc.api.datapacks.builders.ContentBuilder;

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
    public String[] getPath() {
        return new String[]{"worldgen", "biome"};
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
