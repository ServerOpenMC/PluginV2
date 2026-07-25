package fr.openmc.core.features.events.contents.dailyevents.models.dailyevent;

import net.kyori.adventure.bossbar.BossBar;

/**
 * Interface permettant de renseigner la couleur de la boss bar
 */
public interface HasBossBar {
    BossBar.Color getBossBarColor();
    BossBar.Overlay getBossBarOverlay();
}
