package fr.openmc.riftengine.api.scanner.blocks;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.hooks.itemsadder.ItemsAdderHook;
import fr.openmc.core.utils.FilesUtils;
import fr.openmc.core.utils.YmlUtils;
import fr.openmc.riftengine.api.registry.scanner.AbstractScanner;
import fr.openmc.riftengine.api.utils.IdentifierUtils;
import org.bukkit.Material;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Scan le cache des BlockStates et des BlockMaterials qui sont assignés par ItemsAdder
 */
public class BlockStateScanner extends AbstractScanner<Map<String, BlockStateScanner.CustomBlockState>, Void> {

    public Map<String, CustomBlockState> scan(Void voyd) throws Exception {
        Path blockstatesFolderPath = ItemsAdderHook.getItemsAdderPath(OMCPlugin.getInstance().getDataPath())
                .resolve("storage").resolve("blockstates");
        Map<String, CustomBlockState> blockstates = new HashMap<>(); // <Id du custom block, CustomBlockState>

        for (String fileName : FilesUtils.listFileNames(blockstatesFolderPath.toAbsolutePath().toString())) {
            Path filePath = blockstatesFolderPath.resolve(fileName);
            Map<String, Object> root = YmlUtils.loadYml(filePath);
            if (root == null) continue;

            for (var iterRoot : root.entrySet()) {
                String key = iterRoot.getKey();

                if (!(iterRoot.getValue() instanceof String valueStr)) continue;

                blockstates.put(key, new CustomBlockState(key,
                        "minecraft:" + IdentifierUtils.removeExtensionPath(fileName),
                        normalizeState(valueStr)
                ));
            }
        }

        return blockstates;
    }

    // ex d'id: omc_blocks:aywenite_ore, javaBlock: minecraft:note_block, state: [powered=true]
    public record CustomBlockState(String id, String javaBlock, String state) {
        public Material materialBlock() {
            return Material.matchMaterial(javaBlock);
        }
    }

    /**
     * Enleve les crochets et mets dans l'ordre les states afin que geyser ne throw pas d'erreur
     * @param state le state d'un block [powered=true, gngng...]
     * @return le state normalisé
     */
    private String normalizeState(String state) {
        return Arrays.stream(state.replace("[", "").replace("]", "").split(","))
                .map(String::trim)
                .filter(p -> !p.isEmpty())
                .sorted(Comparator.comparing((String p) -> p.substring(0, p.indexOf('='))))
                .collect(Collectors.joining(","));
    }
}
