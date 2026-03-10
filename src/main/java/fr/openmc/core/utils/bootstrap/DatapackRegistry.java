package fr.openmc.core.utils.bootstrap;

import io.papermc.paper.datapack.DatapackRegistrar;
import io.papermc.paper.plugin.lifecycle.event.registrar.RegistrarEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DatapackRegistry {
    /**
     * Load datapacks from a given path and register them to the datapack registrar.
     * ONLY USE IN BOOTSTRAP
     * @param event the datapack registrar event
     * @param path the path to the datapacks directory
     * @throws IOException if an I/O error occurs when reading the datapacks directory
     */
    public static void load(RegistrarEvent<DatapackRegistrar> event, Path path) throws IOException {
        Files.list(path).forEach(pathDir -> {
                    try {
                        event.registrar().discoverPack(pathDir.toUri(), pathDir.getFileName().toString());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
