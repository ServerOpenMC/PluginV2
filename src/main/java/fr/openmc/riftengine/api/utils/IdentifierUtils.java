package fr.openmc.riftengine.api.utils;

import java.nio.file.Path;

public class IdentifierUtils {
    /**
     * Normalise un identifiant (qui n'a pas un namespace devant
     * @param id l'id (font/default.png, minecraft:font/default.png)
     * @return l'id final
     */
    public static String normalizeId(String id) {
        return normalizeId(id, "minecraft");
    }

    /**
     * Normalise un identifiant (qui n'a pas un namespace devant
     * @param id l'id (font/default.png, minecraft:font/default.png)
     * @param defaultNamespace le namespace par défaut à utiliser, si on est dans un fichier d'items adder par ex
     * @return l'id final
     */
    public static String normalizeId(String id, String defaultNamespace) {
        if (id.split(":").length == 2) return id;

        return defaultNamespace + ":" + id;
    }

    /**
     * Résout un identifiant en un chemin de fichier dans les assets
     */
    public static Path resolveTextureId(Path rootPath, String id) {
        String normalizedId = normalizeId(id);
        String[] split = normalizedId.split(":", 2);

        String namespace;
        String texturePath;

        if (split.length == 2) {
            namespace = split[0];
            texturePath = split[1];
        } else {
            namespace = "minecraft";
            texturePath = split[0];
        }

        if (!texturePath.endsWith(".png"))
            texturePath += ".png";


        return rootPath
                .resolve("assets")
                .resolve(namespace)
                .resolve("textures")
                .resolve(texturePath);
    }

    /**
     * Résout un identifiant en un chemin de fichier dans les assets pour les models
     */
    public static Path resolveModelId(Path rootPath, String id) {
        String normalizedId = normalizeId(id);
        String[] split = normalizedId.split(":", 2);

        String namespace;
        String modelPath;

        if (split.length == 2) {
            namespace = split[0];
            modelPath = split[1];
        } else {
            namespace = "minecraft";
            modelPath = split[0];
        }

        if (!modelPath.endsWith(".json"))
            modelPath += ".json";

        return rootPath
                .resolve("assets")
                .resolve(namespace)
                .resolve("models")
                .resolve(modelPath);
    }

    public static String toBedrockTexturePath(String javaPath) {
        if (javaPath.startsWith("textures/item/"))
            return "textures/items/" + javaPath.substring("textures/item/".length());
        if (javaPath.startsWith("textures/block/"))
            return "textures/blocks/" + javaPath.substring("textures/block/".length());

        return javaPath;
    }

    public static String removeExtensionPath(String resourcePath) {
        if (resourcePath.contains("."))
            return resourcePath.substring(0, resourcePath.lastIndexOf('.'));
        return resourcePath;
    }
}