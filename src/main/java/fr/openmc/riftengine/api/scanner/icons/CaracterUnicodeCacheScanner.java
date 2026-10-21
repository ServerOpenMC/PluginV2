package fr.openmc.riftengine.api.scanner.icons;

import fr.openmc.core.utils.YmlUtils;
import fr.openmc.riftengine.api.registry.scanner.AbstractScanner;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class CaracterUnicodeCacheScanner extends AbstractScanner<Map<Character, String>, Path> {
    private final String TARGET_FILE = "storage/font_images_unicode_cache.yml";

    public Map<Character, String> scan(Path itemsAdderDataPath) throws IOException {
        Map<Character, String> caractersByNamespacedId = new HashMap<>();

        Map<String, Object> cache = YmlUtils.loadYml(itemsAdderDataPath.resolve(TARGET_FILE));
        if (cache == null) return caractersByNamespacedId;

        for (Map.Entry<String, Object> entry : cache.entrySet()) {
            String namespacedId = entry.getKey();
            String character = YmlUtils.getString(cache, namespacedId, "");

            if (character.isEmpty()) continue;

            caractersByNamespacedId.put(character.charAt(0), namespacedId);
        }

        return caractersByNamespacedId;
    }
}
