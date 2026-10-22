package fr.openmc.riftengine.api.scanner.equipements;

import java.nio.file.Path;

public record EquipementEntry(String namespace, String key, String layer1Path, String layer2Path) {
    public String namespacedId() {
        return namespace + ":" + key;
    }

    public Path getLayer1Path(Path javaRootPath) {
        return baseLayerPath(javaRootPath).resolve("humanoid").resolve(key + ".png");
    }

    public Path getLayer2Path(Path javaRootPath) {
        return baseLayerPath(javaRootPath).resolve("humanoid_leggings").resolve(key + ".png");
    }

    private Path baseLayerPath(Path javaRootPath) {
        return javaRootPath.resolve("assets")
                .resolve(namespace).resolve("textures/entity/equipment/");
    }

    public Path getLayer1BedrockPath(Path bedrockRootPath) {
        return bedrockRootPath.resolve("textures").resolve(layer1Path + ".png");
    }

    public Path getLayer2BedrockPath(Path bedrockRootPath) {
        return bedrockRootPath.resolve("textures").resolve(layer2Path + ".png");
    }
}
