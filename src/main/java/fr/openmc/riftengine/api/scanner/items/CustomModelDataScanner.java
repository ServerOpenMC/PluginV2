package fr.openmc.riftengine.api.scanner.items;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.hooks.itemsadder.ItemsAdderHook;
import fr.openmc.core.utils.YmlUtils;
import fr.openmc.riftengine.api.registry.scanner.AbstractScanner;
import org.apache.commons.io.file.PathUtils;
import org.bukkit.Material;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Scan le cache des CustomModelData qui sont assignés par ItemsAdder
 */
public class CustomModelDataScanner extends AbstractScanner<Map<Material, Map<String, Integer>>, Void> {
    private final String CACHE_FILE_NAME = "items_ids_cache.yml";

    public Map<Material, Map<String, Integer>> scan(Void voyd) throws Exception {
        Path cmdPath = ItemsAdderHook.getItemsAdderPath(OMCPlugin.getInstance().getDataPath())
                .resolve("storage").resolve(CACHE_FILE_NAME);

        Map<String, Object> root = YmlUtils.loadYml(cmdPath);
        Map<Material, Map<String, Integer>> mappedCache = new HashMap<>();

        for (var iterRoot : root.entrySet()) {
            String key = iterRoot.getKey();
            Material materialKey = Material.valueOf(key);

            Map<String, Integer> value = new HashMap<>();

            if (!(iterRoot.getValue() instanceof Map<?, ?> valueMap)) continue;

            Map<String, Integer> namespacedIdCmdMap = (Map<String, Integer>) valueMap;

            for (var entryIdCmd : namespacedIdCmdMap.entrySet()) {
                String namespacedId = entryIdCmd.getKey();
                // tqdq rift engine __manually_handled, qui serait utile lors de l'impl des CustomEmotes et des models par ailleurs
                if (namespacedId.startsWith("__")) continue;

                Integer cmd = entryIdCmd.getValue();

                value.put(namespacedId, cmd);
            }

            mappedCache.put(materialKey, value);
        }

        return mappedCache;
    }
}
