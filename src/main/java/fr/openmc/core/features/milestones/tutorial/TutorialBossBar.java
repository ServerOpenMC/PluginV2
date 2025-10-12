package fr.openmc.core.features.milestones.tutorial;

import fr.openmc.core.features.displays.bossbar.BossbarManager;
import fr.openmc.core.features.displays.bossbar.BossbarsType;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class TutorialBossBar {
    
    public static final String PLACEHOLDER_TUTORIAL_BOSSBAR = "§6Étape %s : %s";

    /**
     * Adds a tutorial boss bar for the player with the given message and progress.
     *
     * @param player   The player to add the boss bar for.
     * @param message  The message to display on the boss bar.
     * @param progress The progress of the tutorial step (0.0 to 1.0).
     */
    public static void addTutorialBossBarForPlayer(Player player, Component message, float progress) {
        BossBar bar = BossBar.bossBar(
                message,
                progress,
                BossBar.Color.YELLOW,
                BossBar.Overlay.PROGRESS
        );
        BossbarManager.addBossBar(BossbarsType.TUTORIAL, bar, player);
    }

    /**
     * Updates the tutorial boss bar for the player with the given message and progress.
     *
     * @param player   The player to update the boss bar for.
     * @param message  The new message to display on the boss bar.
     * @param progress The new progress of the tutorial step (0.0 to 1.0).
     */
    public static void update(Player player, Component message, float progress) {
        BossBar bar = BossbarManager.getBossBar(BossbarsType.TUTORIAL, player);

        if (bar != null) {
            bar.name(message);
            bar.progress(progress);
        }
    }

    /**
     * Hides the tutorial boss bar for the player.
     *
     * @param player The player to hide the boss bar for.
     */
    public static void hide(Player player) {
        BossbarManager.removeBossBar(BossbarsType.TUTORIAL, player);
    }
}
