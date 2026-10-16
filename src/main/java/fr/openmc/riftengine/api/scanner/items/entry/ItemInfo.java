package fr.openmc.riftengine.api.scanner.items.entry;

import org.bukkit.Material;

import java.util.Map;

public record ItemInfo(String namespace,
                       String key,
                       Map<Material, Map<String, Integer>> cmdCache,
                       Map<String, String> blockStateCache) {
    public static ItemInfo from(String namespace,
                                String key,
                                Map<Material, Map<String, Integer>> cmdCache,
                                Map<String, String> blockStateCache) {
        return new ItemInfo(namespace, key, cmdCache, blockStateCache);
    }

    public String namespacedId() {
        return namespace + ":" + key;
    }
}
