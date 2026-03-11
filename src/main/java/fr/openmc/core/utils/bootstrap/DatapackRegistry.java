package fr.openmc.core.utils.bootstrap;

import io.papermc.paper.datapack.DatapackRegistrar;
import io.papermc.paper.plugin.lifecycle.event.registrar.RegistrarEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.Map;
import java.util.stream.Stream;

@SuppressWarnings("UnstableApiUsage")
public class DatapackRegistry {
    /**
     * Load datapacks from a given path and register them to the datapack registrar.
     * ONLY USE IN BOOTSTRAP
     * @param event the datapack registrar event
     * @param path the path to the datapacks directory
     */
    public static void load(RegistrarEvent<@NotNull DatapackRegistrar> event, Path path) {
        try (Stream<Path> paths = Files.list(path)){
            paths.forEach(pathDir -> {
                try {
                    event.registrar().discoverPack(pathDir.toUri(), pathDir.getFileName().toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Extract datapacks from the plugin jar to a temporary directory and return the path to that directory.
     * @param pluginSource the path to the plugin jar
     * @return the path to the temporary directory containing the extracted datapacks
     */
    public static Path extractDatapacks(Path pluginSource) {
        try {
            Path tempDir = Files.createTempDirectory("omc-datapacks");
            URI jarUri = URI.create("jar:" + pluginSource.toUri());

            // on lit le .jar
            try (FileSystem jarFs = FileSystems.newFileSystem(jarUri, Map.of())) {
                // les dossiers resources sont a la racine du .jar
                Path datapacksInJar = jarFs.getPath("/datapacks");

                try (Stream<Path> paths = Files.walk(datapacksInJar)) {
                    for (Path source : (Iterable<Path>) paths::iterator) {
                        // on fait les copies des dossiers dans les datapacks
                        Path dest = tempDir.resolve(datapacksInJar.relativize(source).toString());
                        if (Files.isDirectory(source)) Files.createDirectories(dest);
                        else {
                            Files.createDirectories(dest.getParent());
                            Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                }
            }

            return tempDir;
        } catch (IOException e) {
            throw new RuntimeException("Failed to extract datapacks", e);
        }
    }
}
