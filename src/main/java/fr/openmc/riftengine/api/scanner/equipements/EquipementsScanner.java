package fr.openmc.riftengine.api.scanner.equipements;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.utils.YmlUtils;
import fr.openmc.riftengine.api.registry.scanner.AbstractScanner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Scan tout les equipements initialisé par ItemsAdder
 */
public class EquipementsScanner extends AbstractScanner<List<EquipementEntry>, Path> {
    public List<EquipementEntry> scan(Path itemsAdderContentsPath) throws Exception {
        if (!Files.isDirectory(itemsAdderContentsPath)) return new ArrayList<>();

        List<Path> ymlFiles = OMCRegistry.SCANNERS.YAML.scan(itemsAdderContentsPath);

        List<EquipementEntry> result = new ArrayList<>();

        for (Path ymlFile : ymlFiles) {
            Map<String, Object> root = YmlUtils.loadYml(ymlFile);

            if (root == null) continue;

            Object equipements = root.get("equipements");
            if (!(equipements instanceof Map<?, ?> equipementsMap)) continue;

            String namespace = OMCRegistry.SCANNERS.YAML_ITEMSADDER_NAMESPACE.scan(root);

            for (Map.Entry<?, ?> entry : equipementsMap.entrySet()) {
                String key = String.valueOf(entry.getKey());

                if (!(key.equals("layer_1") && key.equals("layer_2"))) continue;

                result.add(new EquipementEntry(
                        namespace,
                        key,
                        YmlUtils.getString(root, "layer_1", null),
                        YmlUtils.getString(root, "layer_2", null)));
            }
        }

        return result;
    }
}
