package fr.openmc.riftengine.api.scanner.items;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.utils.YmlUtils;
import fr.openmc.riftengine.api.registry.scanner.AbstractScanner;
import org.bukkit.Material;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Scan tout les items initialisé par ItemsAdder
 */
public class ItemScanner extends AbstractScanner<List<ItemEntry>, Path> {
    public List<ItemEntry> scan(Path itemsAdderContentsPath) throws Exception {
        if (!Files.isDirectory(itemsAdderContentsPath)) return new ArrayList<>();

        List<Path> ymlFiles = OMCRegistry.SCANNERS.YAML.scan(itemsAdderContentsPath);

        List<ItemEntry> result = new ArrayList<>();

        for (Path ymlFile : ymlFiles) {
            System.out.println(ymlFile.toString());
            Map<String, Object> root = YmlUtils.loadYml(ymlFile);

            if (root == null) continue;

            Object itemsObj = root.get("items");
            if (!(itemsObj instanceof Map<?, ?> itemsMap)) continue;

            String namespace = OMCRegistry.SCANNERS.YAML_ITEMSADDER_NAMESPACE.scan(root);
            Map<Material, Map<String, Integer>> mappedCache = OMCRegistry.SCANNERS.CUSTOM_MODEL_DATA_CACHE.scan(null);

            for (Map.Entry<?, ?> entry : itemsMap.entrySet()) {
                String key = String.valueOf(entry.getKey());
                if (!(entry.getValue() instanceof Map<?, ?> data)) continue;

                result.add(ItemEntry.from(mappedCache, ymlFile, namespace, key, data));
            }
        }

        return result;
    }

    public ItemEntry getFromNamespacedId(Path itemsAdderContentsPath, String namespacedId) {
        try {
            return getCache(itemsAdderContentsPath).stream()
                    .filter(itemEntry -> itemEntry.namespacedId().equals(namespacedId))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            OMCLogger.error("Error while loading ItemsAdder items", e);
            return null;
        }
    }
}
