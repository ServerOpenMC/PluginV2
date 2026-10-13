package fr.openmc.riftengine.api.scanner.general;

import fr.openmc.riftengine.api.registry.scanner.AbstractScanner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class YamlScanner extends AbstractScanner<List<Path>, Path> {
    @Override
    public List<Path> scan(Path path) throws Exception {
        try (Stream<Path> walk = Files.walk(path)) {
            return walk
                    .filter(Files::isRegularFile)
                    .filter(p -> {
                        String name = p.getFileName().toString().toLowerCase(Locale.ROOT);
                        return name.endsWith(".yml") || name.endsWith(".yaml");
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du scan des fichiers yml dans " + path, e);}
    }
}
