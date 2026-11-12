package fr.openmc.core.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
    public static void unzip(Path zipPath, Path targetDir) throws IOException {
        Files.createDirectories(targetDir);
        Path normalizedTarget = targetDir.toAbsolutePath().normalize();

        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipPath))) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName().replace('\\', '/');

                if (name.replace("/", "").isBlank()) {
                    zis.closeEntry();
                    continue;
                }

                name = name.replaceAll("/{2,}", "/").replaceAll("^/+", "");

                Path newPath = normalizedTarget.resolve(name).normalize();

                if (!newPath.startsWith(normalizedTarget))
                    throw new IOException("Entrée zip invalide : " + entry.getName());

                if (entry.isDirectory() || name.endsWith("/")) {
                    Files.createDirectories(newPath);
                } else {
                    Path parent = newPath.getParent();
                    if (parent != null) Files.createDirectories(parent);
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                }

                zis.closeEntry();
            }
        }
    }

    public static void zip(Path sourceDir, Path zipPath) throws IOException {
        Path parent = zipPath.toAbsolutePath().getParent();
        if (parent != null) Files.createDirectories(parent);

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath));
             Stream<Path> walk = Files.walk(sourceDir)) {

            walk.filter(Files::isRegularFile).forEach(path -> {
                try {
                    String entryName = sourceDir
                            .relativize(path)
                            .toString()
                            .replace('\\', '/');

                    zos.putNextEntry(new ZipEntry(entryName));
                    Files.copy(path, zos);
                    zos.closeEntry();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        }
    }
}