package fr.openmc.riftengine.api.scanner.icons;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.utils.YmlUtils;
import fr.openmc.riftengine.api.registry.scanner.AbstractScanner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Scan tout les icons initialisé par ItemsAdder
 */
public class IconScanner extends AbstractScanner<List<IconEntry>, Path> {
    public List<IconEntry> scan(Path itemsAdderContentsPath) throws Exception {
        if (!Files.isDirectory(itemsAdderContentsPath)) return new ArrayList<>();

        List<Path> ymlFiles = OMCRegistry.SCANNERS.YAML.scan(itemsAdderContentsPath);

        List<IconEntry> result = new ArrayList<>();

        for (Path ymlFile : ymlFiles) {
            Map<String, Object> root = YmlUtils.loadYml(ymlFile);

            Object fontImagesObj = root.get("font_images");
            if (!(fontImagesObj instanceof Map<?, ?> fontImagesMap)) continue;

            String namespace = OMCRegistry.SCANNERS.YAML_ITEMSADDER_NAMESPACE.scan(root);

            for (Map.Entry<?, ?> entry : fontImagesMap.entrySet()) {
                String key = String.valueOf(entry.getKey());
                if (!(entry.getValue() instanceof Map<?, ?> data)) continue;

                result.add(IconEntry.from(ymlFile, namespace, key, data));
            }
        }

        return result;
    }
}
