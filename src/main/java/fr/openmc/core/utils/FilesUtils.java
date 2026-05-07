package fr.openmc.core.utils;

import fr.openmc.core.OMCBootstrap;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Utilitaires pour la gestion des fichiers et dossiers
 */
public class FilesUtils {
    //todo: enelver les Logger et faire une classe OMCLogger en static qui est initialisé dans le bootstrap et dans le runtime (tout en changeant de logger)
    private static final Set<String> TEXT_EXTENSIONS = Set.of(
            "yml",
            "yaml",
            "json",
            "txt",
            "mcmeta",
            "properties"
    );
    private static final ClassLoader CLASS_LOADER = OMCBootstrap.class.getClassLoader();

    /**
     * Supprime récursivement un répertoire
     *
     * @param dir Répertoire à supprimer
     * @throws IOException Si une erreur survient lors de la suppression
     */
    public static void deleteDirectory(Logger logger, File dir) throws IOException {
        if (!dir.exists()) return;

        Files.walk(Paths.get(dir.getAbsolutePath()))
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        if (logger != null)
                            logger.warn("Impossible de supprimer: {}", path, e);
                    }
                });
    }

    /**
     * Supprime récursivement un répertoire
     *
     * @param dir Répertoire à supprimer
     * @throws IOException Si une erreur survient lors de la suppression
     */
    public static void deleteDirectory(File dir) throws IOException {
        deleteDirectory(null, dir);
    }

    /**
     * Retourne la liste de tous les dossiers présents dans une ressource du JAR
     *
     * @param resourcePath Chemin dans les ressources (ex: "contents")
     * @return Liste des noms des dossiers dans la ressource
     */
    public static List<String> listFolderNamesInResource(Logger logger, String resourcePath) {
        Set<String> folderNames = new HashSet<>();

        try {
            Enumeration<URL> resources = CLASS_LOADER.getResources(resourcePath);

            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                String protocol = url.getProtocol();

                if (protocol.equals("file")) {
                    // * méthode de parcours d'un fichier
                    try {
                        var path = Paths.get(url.toURI());
                        if (Files.exists(path)) {
                            try (var stream = Files.list(path)) {
                                stream.filter(Files::isDirectory)
                                        .map(p -> p.getFileName().toString())
                                        .forEach(folderNames::add);
                            }
                        }
                    } catch (Exception e) {
                        logger.warn("Erreur lors de la lecture des ressources en mode fichier: {}", e.getMessage());
                    }
                } else if (protocol.equals("jar")) {
                    // * méthode de parcours d'un jar
                    try {
                        JarURLConnection connection = (JarURLConnection) url.openConnection();
                        try (JarFile jar = connection.getJarFile()) {
                            String prefix = connection.getEntryName();
                            if (prefix == null) {
                                prefix = resourcePath;
                            }
                            if (!prefix.endsWith("/")) {
                                prefix += "/";
                            }

                            Enumeration<JarEntry> entries = jar.entries();
                            while (entries.hasMoreElements()) {
                                JarEntry entry = entries.nextElement();
                                String name = entry.getName();
                                if (name.startsWith(prefix) && entry.isDirectory()) {
                                    // * recupere le nom du folder
                                    String relativePath = name.substring(prefix.length());
                                    if (!relativePath.isEmpty() && !relativePath.equals("/")) {
                                        String folderName = relativePath.split("/")[0];
                                        if (!folderName.isEmpty()) {
                                            folderNames.add(folderName);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        logger.warn("Erreur lors de la lecture des ressources en mode JAR: {}", e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            logger.warn("Erreur lors de l'accès aux ressources: {}", e.getMessage());
        }

        return folderNames.stream().sorted().toList();
    }

    public static List<String> listFileNamesInResource(Logger logger, String resourcePath) {
        Set<String> result = new HashSet<>();

        try {
            Enumeration<URL> resources = CLASS_LOADER.getResources(resourcePath);

            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                String protocol = url.getProtocol();

                if (protocol.equals("file")) {
                    try {
                        Path rootPath = Paths.get(url.toURI());
                        if (!Files.exists(rootPath)) {
                            continue;
                        }
                        try (var stream = Files.walk(rootPath)) {
                            stream.filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".properties"))
                                    .forEach(path -> result.add(toFileName(rootPath, path)));
                        }
                    } catch (Exception e) {
                        logger.warn("Erreur lors de la lecture des ressources en mode fichier: {}", e.getMessage());
                    }
                }

                if (protocol.equals("jar")) {
                    try {
                        JarURLConnection connection = (JarURLConnection) url.openConnection();
                        try (JarFile jar = connection.getJarFile()) {
                            String prefix = connection.getEntryName();
                            if (prefix == null) {
                                prefix = resourcePath;
                            }
                            if (!prefix.endsWith("/")) {
                                prefix += "/";
                            }

                            Enumeration<JarEntry> entries = jar.entries();
                            while (entries.hasMoreElements()) {
                                JarEntry entry = entries.nextElement();
                                String name = entry.getName();
                                if (!entry.isDirectory() && name.startsWith(prefix)) {
                                    result.add(toFileName(prefix, name));
                                }
                            }
                        }
                    } catch (IOException e) {
                        logger.warn("Erreur lors de la lecture des ressources en mode JAR: {}", e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            logger.warn("Erreur lors de l'accès aux ressources: {}", e.getMessage());
        }
        return result.stream().sorted().toList();
    }
    /**
     * Copie un dossier depuis les ressources vers le système de fichiers
     *
     * @param sourcePath Chemin dans les ressources (ex: "contents/omc_items")
     * @param destDir Dossier destination
     * @throws IOException Si une erreur survient lors de la copie
     */
    public static void copyResourceFolder(Logger logger, String sourcePath, File destDir) throws IOException {
        copyResourceFolder(logger, sourcePath, destDir, null);
    }

    /**
     * Copie un dossier depuis les ressources vers le système de fichiers.
     *
     * @param sourcePath Chemin dans les ressources (ex: "contents/omc_items").
     * @param destDir Dossier destination.
     * @param textTransformer transformeur appliqué aux fichiers texte (peut être null).
     * @throws IOException Si une erreur survient lors de la copie.
     */
    public static void copyResourceFolder(Logger logger, String sourcePath, File destDir,
                                          Function<String, String> textTransformer) throws IOException {
        try {
            Enumeration<URL> resources = CLASS_LOADER.getResources(sourcePath);

            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                String protocol = url.getProtocol();

                if (protocol.equals("file")) {
                    // * méthode de parcours d'un fichier
                    try {
                        var sourceDirPath = Paths.get(url.toURI());
                        if (Files.exists(sourceDirPath) && Files.isDirectory(sourceDirPath)) {
                            try (var stream = Files.walk(sourceDirPath)) {
                                stream.filter(Files::isRegularFile)
                                        .forEach(sourceFile -> {
                                            try {
                                                var relativePath = sourceDirPath.relativize(sourceFile);
                                                File destFile = new File(destDir, relativePath.toString());
                                                File parentDir = destFile.getParentFile();
                                                if (parentDir != null && !parentDir.exists()) {
                                                    if (!parentDir.mkdirs()) {
                                                        logger.warn("Impossible de créer le dossier: {}", parentDir.getAbsolutePath());
                                                    }
                                                }

                                                Files.copy(sourceFile, destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                                                if (textTransformer != null) {
                                                    transformTextFile(logger, destFile, textTransformer);
                                                }
                                            } catch (IOException e) {
                                                logger.warn("Erreur lors de la copie du fichier: {}", e.getMessage());
                                            }
                                        });
                            }
                        }
                    } catch (Exception e) {
                        logger.warn("Erreur lors de la copie en mode fichier: {}", e.getMessage());
                    }
                } else if (protocol.equals("jar")) {
                    // * méthode de parcours d'un jar
                    try {
                        JarURLConnection connection = (JarURLConnection) url.openConnection();
                        try (JarFile jar = connection.getJarFile()) {
                            String prefix = connection.getEntryName();
                            if (prefix == null) {
                                prefix = sourcePath;
                            }
                            if (!prefix.endsWith("/")) {
                                prefix += "/";
                            }

                            Enumeration<JarEntry> entries = jar.entries();
                            while (entries.hasMoreElements()) {
                                JarEntry entry = entries.nextElement();
                                if (!entry.isDirectory() && entry.getName().startsWith(prefix)) {
                                    String relativePath = entry.getName().substring(prefix.length());
                                    File destFile = new File(destDir, relativePath);

                                    // * Copie le dossier parent si nécessaire
                                    File parentDir = destFile.getParentFile();
                                    if (parentDir != null && !parentDir.exists()) {
                                        if (!parentDir.mkdirs()) {
                                            logger.warn("Impossible de créer le dossier: {}", parentDir.getAbsolutePath());
                                        }
                                    }

                                    // * Copie le fichier
                                    try (InputStream in = jar.getInputStream(entry)) {
                                        Files.copy(in, destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                                        if (textTransformer != null) {
                                            transformTextFile(logger, destFile, textTransformer);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        logger.warn("Erreur lors de la copie en mode JAR: {}", e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Erreur lors de l'accès aux ressources: {}", e.getMessage(), e);
            throw new IOException("Erreur lors de la copie des ressources", e);
        }
    }

    /**
     * Dit si le fichier est compatible avec le systeme de transformation
     * @param fileName le nom du fichier
     * @param textTransformer le transformeur de texte à appliquer (peut être null)
     * @return
     */
    private static boolean shouldTransform(String fileName, Function<String, String> textTransformer) {
        if (textTransformer == null || fileName == null) {
            return false;
        }

        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0) {
            return false;
        }

        String extension = fileName.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
        return TEXT_EXTENSIONS.contains(extension);
    }

    /**
     * Transforme un fichier texte en appliquant un transformeur.
     *
     * @param logger logger pour les avertissements (peut être {@code null}).
     * @param file fichier à transformer.
     * @param textTransformer transformeur de contenu.
     * @return {@code true} si un changement a été appliqué, sinon {@code false}.
     */
    public static boolean transformTextFile(Logger logger, File file, Function<String, String> textTransformer) {
        if (file == null || textTransformer == null || !file.isFile()) {
            return false;
        }

        if (!shouldTransform(file.getName(), textTransformer)) {
            return false;
        }

        try {
            String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            String transformed = textTransformer.apply(content);
            if (transformed == null || transformed.equals(content)) {
                return false;
            }

            Files.writeString(file.toPath(), transformed, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            if (logger != null) {
                logger.warn("Erreur lors de la transformation du fichier: {}", file.getAbsolutePath(), e);
            }
            return false;
        }
    }

    /**
     * Crée un dossier s'il n'existe pas
     *
     * @param directory Le dossier à créer
     * @return true si le dossier a été créé ou existe déjà, false sinon
     */
    public static boolean createDirectoryIfNotExists(File directory) {
        if (directory.exists()) {
            return true;
        }
        return directory.mkdirs();
    }

    private static String toFileName(Path rootPath, Path filePath) {
        return rootPath.relativize(filePath).toString().replace("\\", "/");
    }

    private static String toFileName(String prefix, String entryName) {
        return entryName.substring(prefix.length());
    }
}

