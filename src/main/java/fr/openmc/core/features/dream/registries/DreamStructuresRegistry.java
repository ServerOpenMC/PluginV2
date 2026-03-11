package fr.openmc.core.features.dream.registries;

import fr.openmc.core.features.dream.models.registry.DreamStructure;
import org.bukkit.entity.Player;

public class DreamStructuresRegistry {
    public static boolean isInDreamStructure(Player player, DreamStructure dreamStructure) {
        return !player.getLocation().getChunk().getStructures(dreamStructure.getStructure()).isEmpty();
    }

    public static DreamStructure getDreamStructure(Player player) {
        for (DreamStructure dreamStructure : DreamStructure.values()) {
            if (player.getLocation().getChunk().getStructures(dreamStructure.getStructure()).isEmpty()) continue;

            return dreamStructure;
        }

        return null;
    }
}
