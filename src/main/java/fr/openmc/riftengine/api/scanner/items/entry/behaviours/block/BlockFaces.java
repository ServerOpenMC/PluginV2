package fr.openmc.riftengine.api.scanner.items.entry.behaviours.block;

import fr.openmc.riftengine.api.utils.IdentifierUtils;

import java.util.List;
import java.util.Map;

public record BlockFaces(String north, String south, String east, String west, String up, String down) {
    private static final List<String> BLOCK_FACES = List.of(
            "north", "south", "east", "west", "up", "down"
    );

    public static BlockFaces from(Map<String, String> faces) {
        if (faces == null) return null;

        if (!faces.keySet().containsAll(BLOCK_FACES) && !faces.isEmpty()) {
            String face = removeExt(faces.values().stream().findFirst().get());
            return new BlockFaces(face, face, face, face, face, face);
        }

        return new BlockFaces(
                removeExt(faces.get("north")),
                removeExt(faces.get("south")),
                removeExt(faces.get("east")),
                removeExt(faces.get("west")),
                removeExt(faces.get("up")),
                removeExt(faces.get("down"))
        );
    }

    private static String removeExt(String face) {
        return IdentifierUtils.removeExtensionPath(face);
    }


    public Map<String, String> getFaces() {
        return Map.of(
                "north", north,
                "south", south,
                "east", east,
                "west", west,
                "up", up,
                "down", down
        );
    }
}
