package fr.openmc.riftengine.api.scanner.items.entry.behaviours.block;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.utils.YmlUtils;
import fr.openmc.riftengine.api.scanner.blocks.BlockStateScanner;
import fr.openmc.riftengine.api.scanner.items.entry.ItemInfo;
import fr.openmc.riftengine.api.scanner.items.entry.behaviours.EmptyBehaviour;
import fr.openmc.riftengine.api.scanner.items.entry.behaviours.IABehaviour;

import java.util.List;
import java.util.Map;

public record BlocksBehaviour(PlacedModel placedModel,
                              BlockStateScanner.CustomBlockState blockState,
                               boolean dropWhenMined,
                               int lightLevel,
                               List<String> breakToolsBlacklist,
                               List<String> breakToolsWhitelist,
                               double hardness,
                               double blastResistance,
                               boolean noExplosion,
                               BlocksSound sounds
) implements IABehaviour {
    private static BlocksBehaviour from(ItemInfo info, Map<?, ?> block) throws Exception {
        double hardness = YmlUtils.getDouble(block, "hardness", 2d);

        BlockStateScanner.CustomBlockState blockState = OMCRegistry.SCANNERS.BLOCKSTATES_CACHE.getCache(null)
                .get(info.namespacedId());

        return new BlocksBehaviour(
                PlacedModel.from(YmlUtils.getMap(block, "placed_model")),
                blockState,
                YmlUtils.getBool(block, "drop_when_mined", true),
                YmlUtils.getInt(block, "light_level", 0),
                YmlUtils.getStringList(block, "break_tools_blacklist"),
                YmlUtils.getStringList(block, "break_tools_whitelist"),
                hardness,
                YmlUtils.getDouble(block, "blast_resistance", hardness * 3),
                YmlUtils.getBool(block, "no_explosion", false),
                BlocksSound.from(YmlUtils.getMap(block, "sound"))
        );
    }

    public static IABehaviour detectBlockBehaviour(ItemInfo info, Map<?, ?> data, Object resourceObj) throws Exception {
        Object specificPropertiesObj = data.get("specific_properties");

        if (resourceObj == null && specificPropertiesObj == null) return new EmptyBehaviour();

        Map<?, ?> behaviours;

        if (resourceObj instanceof Map<?, ?> b) {
            behaviours = b;
        } else if (specificPropertiesObj instanceof Map<?, ?> b) {
            behaviours = b;
        } else return new EmptyBehaviour();


        if (behaviours.get("block") instanceof Map<?, ?> block)
            return BlocksBehaviour.from(info, block);

        return new EmptyBehaviour();
    }
}
