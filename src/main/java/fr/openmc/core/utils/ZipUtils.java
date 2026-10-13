package fr.openmc.core.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
    public static void unzip(Path zipPath, Path targetDir) throws IOException {
        Files.createDirectories(targetDir);

        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipPath))) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                Path newPath = targetDir.resolve(entry.getName());

                if (entry.isDirectory()) {
                    Files.createDirectories(newPath);
                } else {
                    Files.createDirectories(newPath.getParent());
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                }

                zis.closeEntry();
            }
        }
    }

    public static void zip(Path sourceDir, Path zipPath) throws IOException {
        Files.createDirectories(zipPath.getParent());

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            Files.walk(sourceDir)
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            String entryName = sourceDir
                                    .relativize(path)
                                    .toString()
                                    .replace('\\', '/');

                            ZipEntry entry = new ZipEntry(entryName);
                            zos.putNextEntry(entry);
                            Files.copy(path, zos);
                            zos.closeEntry();

                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }
    }

}
