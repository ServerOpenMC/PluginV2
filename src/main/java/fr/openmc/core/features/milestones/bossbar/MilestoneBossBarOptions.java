package fr.openmc.core.features.milestones.bossbar;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.format.NamedTextColor;

public record MilestoneBossBarOptions(NamedTextColor textColor, BossBar.Color color, BossBar.Overlay style) { }
