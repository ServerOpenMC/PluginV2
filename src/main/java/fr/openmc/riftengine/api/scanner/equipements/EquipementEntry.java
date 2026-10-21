package fr.openmc.riftengine.api.scanner.equipements;

import fr.openmc.riftengine.api.utils.IdentifierUtils;

import java.nio.file.Path;

public record EquipementEntry(String namespace, String key, String layer1Path, String layer2Path) {
    public String namespacedId() {
        return namespace + ":" + key;
    }

    public Path getLayer1Path(Path javaRootPath) {
        return IdentifierUtils.resolveTextureId(javaRootPath, namespace + ":" + layer1Path);
    }

    public Path getLayer2Path(Path javaRootPath) {
        return IdentifierUtils.resolveTextureId(javaRootPath, namespace + ":" + layer2Path);
    }
}
