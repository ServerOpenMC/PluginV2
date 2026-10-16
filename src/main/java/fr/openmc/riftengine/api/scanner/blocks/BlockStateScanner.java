package fr.openmc.riftengine.api.scanner.blocks;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.hooks.itemsadder.ItemsAdderHook;
import fr.openmc.core.utils.FilesUtils;
import fr.openmc.core.utils.YmlUtils;
import fr.openmc.riftengine.api.registry.scanner.AbstractScanner;
import fr.openmc.riftengine.api.utils.IdentifierUtils;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Scan le cache des CustomModelData qui sont assignés par ItemsAdder
 */
public class BlockStateScanner extends AbstractScanner<Map<String, String>, Void> {

    public Map<String, String> scan(Void voyd) throws Exception {
        Path blockstatesFolderPath = ItemsAdderHook.getItemsAdderPath(OMCPlugin.getInstance().getDataPath())
                .resolve("storage").resolve("blockstates");
        Map<String, String> blockstates = new HashMap<>(); // <Id du custom block, Blockstates avec le material + []>

        for (String fileName : FilesUtils.listFileNames(blockstatesFolderPath.toAbsolutePath().toString())) {
            Path filePath = blockstatesFolderPath.resolve(fileName);
            Map<String, Object> root = YmlUtils.loadYml(filePath);

            for (var iterRoot : root.entrySet()) {
                String key = iterRoot.getKey();

                if (!(iterRoot.getValue() instanceof String valueStr)) continue;

                blockstates.put(key, IdentifierUtils.removeExtensionPath(valueStr) + valueStr);
            }
        }

        return blockstates;
    }
}
