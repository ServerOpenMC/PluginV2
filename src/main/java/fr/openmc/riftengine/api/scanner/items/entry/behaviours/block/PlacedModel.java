package fr.openmc.riftengine.api.scanner.items.entry.behaviours.block;

import fr.openmc.core.utils.YmlUtils;

import java.util.Map;

public record PlacedModel(BlockType type, String breakParticles, int rotX, int rotY) {
    static PlacedModel from(Map<?, ?> m) {
        return new PlacedModel(
                BlockType.from(YmlUtils.getString(m, "type", null)),
                YmlUtils.getString(m, "break_particles", "BLOCK"),
                YmlUtils.getInt(m, "rotx", 0),
                YmlUtils.getInt(m, "roty", 0)
        );
    }
}