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

            Map<?, ?> equipments = YmlUtils.getMap(root, "equipments");

            if (equipments == null) continue;

            String namespace = OMCRegistry.SCANNERS.YAML_ITEMSADDER_NAMESPACE.scan(root);

            for (Map.Entry<?, ?> entry : equipments.entrySet()) {
                String key = String.valueOf(entry.getKey());
                Map<?, ?> keyInfo = YmlUtils.getMap(equipments, key);

                result.add(new EquipementEntry(
                        namespace,
                        key,
                        YmlUtils.getString(keyInfo, "layer_1", null),
                        YmlUtils.getString(keyInfo, "layer_2", null)));
            }
        }

        return result;
    }
}
