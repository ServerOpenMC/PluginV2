package fr.openmc.riftengine.api.scanner.items.entry.behaviours.block;

import fr.openmc.core.utils.YmlUtils;

import java.util.Map;

public record BlocksSound(IASound breakSound, IASound placeSound) {
    static BlocksSound from(Map<?, ?> m) {
        return new BlocksSound(
                IASound.from(YmlUtils.getMap(m, "break")),
                IASound.from(YmlUtils.getMap(m, "place"))
        );
    }

    public record IASound(String name, float volume, float pitch) {
        static IASound from(Map<?, ?> m) {
            if (m == null) return null;
            return new IASound(
                    YmlUtils.getString(m, "name", null),
                    YmlUtils.getDouble(m, "volume", 1.0).floatValue(),
                    YmlUtils.getDouble(m, "pitch", 1.0).floatValue()
            );
        }
    }
}

