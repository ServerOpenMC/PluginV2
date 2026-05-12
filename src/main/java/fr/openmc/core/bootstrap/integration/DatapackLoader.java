package fr.openmc.core.bootstrap.integration;

import fr.openmc.core.utils.FilesUtils;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@SuppressWarnings("UnstableApiUsage")
public class DatapackLoader {
    /**
     * Charge tout les datapacks qui sont dans la ressource resources/datapacks
     */
    public static void loadAllInResource(BootstrapContext context) {
        Path extractedDatapacks = extractDatapacks();

        try (Stream<Path> paths = Files.list(extractedDatapacks)){
            paths.forEach(pathDir ->
                    context.getLifecycleManager().registerEventHandler(LifecycleEvents.DATAPACK_DISCOVERY.newHandler(
                    event -> {
                        try {
                            event.registrar().discoverPack(pathDir.toUri(), pathDir.getFileName().toString());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Extrait tout les datapacks dans un dossier temporaire "omc-datapacks" et retourne le path de ce dossier.
     */
    public static Path extractDatapacks() {
        try {
            Path tempDir = Files.createTempDirectory("omc-datapacks");

            FilesUtils.copyResourceFolder("datapacks", tempDir.toFile());

            return tempDir;
        } catch (IOException e) {
            throw new RuntimeException("Failed to extract datapacks", e);
        }
    }
}
