package fr.openmc.riftengine.api.scanner.items.entry.behaviours.block;

import fr.openmc.core.utils.YmlUtils;
import fr.openmc.riftengine.api.scanner.items.entry.ItemInfo;
import fr.openmc.riftengine.api.scanner.items.entry.behaviours.IABehaviour;

import java.util.List;
import java.util.Map;

// note: toutes les options ne sont pas mappé
public record BlocksBehaviour(PlacedModel placedModel,
                               String blockState,
                               boolean dropWhenMined,
                               int lightLevel,
                               List<String> breakToolsBlacklist,
                               List<String> breakToolsWhitelist,
                               double hardness,
                               double blastResistance,
                               boolean noExplosion,
                               BlocksSound sounds
) implements IABehaviour {
    public static BlocksBehaviour from(ItemInfo info, Map<?, ?> block) {
        double hardness = YmlUtils.getDouble(block, "hardness", 2d);

        String blockState = info.blockStateCache().get(info.namespacedId());

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
}
