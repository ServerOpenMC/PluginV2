package fr.openmc.core.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class FilesUtils {

    /**
     * Delete a directory and all its content.
     * @param dir the directory to delete
     * @throws IOException if an error occurs while deleting the directory or its content
     */
    public static void deleteDirectory(Path dir) throws IOException {
        if (!Files.exists(dir)) return;

        try (var walk = Files.walk(dir)) {
            walk.sorted(Comparator.reverseOrder())
                    .filter(path -> !path.equals(dir))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }
    }
}