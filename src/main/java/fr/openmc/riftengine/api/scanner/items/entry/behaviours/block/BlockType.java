package fr.openmc.riftengine.api.scanner.items.entry.behaviours.block;

public enum BlockType {
    REAL_WIRE,
    REAL_NOTE,
    REAL,
    REAL_TRANSPARENT,
    ENTITY,
    FIRE, // le feu est un bloc cool
    UNKNOWN
    ;

    static BlockType from(String s) {
        if (s == null) return UNKNOWN;
        try {
            return valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
