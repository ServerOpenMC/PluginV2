package fr.openmc.core.features.events.contents.deposit;

import net.kyori.adventure.text.format.NamedTextColor;

public record NpcSettings(String displayName, String skinUrl, NamedTextColor glowingColor, int visibilityDistance) {
    public NpcSettings(String displayName, String skinUrl, NamedTextColor glowingColor) {
        this(displayName, skinUrl, glowingColor, 50);
    }
}
