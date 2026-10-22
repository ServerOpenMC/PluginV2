package fr.openmc.riftengine.api.scanner.items.entry;

import org.bukkit.Material;

public record ItemInfo(String namespace,
                       String key,
                       Material material) {
    public static ItemInfo from(String namespace,
                                String key,
                                Material material) {
        return new ItemInfo(namespace, key, material);
    }

    public String namespacedId() {
        return namespace + ":" + key;
    }
}
