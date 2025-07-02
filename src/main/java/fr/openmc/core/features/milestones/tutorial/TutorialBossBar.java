package fr.openmc.core.features.milestones.tutorial;

import fr.openmc.core.features.bossbar.BossbarManager;
import fr.openmc.core.features.bossbar.BossbarsType;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class TutorialBossBar {

    public static final String PLACEHOLDER_TUTORIAL_BOSSBAR = "§6Etape %s/%s : %s";

    public static void addTutorialBossBarForPlayer(Player player, Component message, float progress) {
        BossBar bar = BossBar.bossBar(
                message,
                progress,
                BossBar.Color.YELLOW,
                BossBar.Overlay.PROGRESS
        );
        BossbarManager.addBossBar(BossbarsType.TUTORIAL, bar, player);
    }

    public static void update(Player player, Component message, float progress) {
        BossBar bar = BossbarManager.getBossBar(BossbarsType.TUTORIAL, player);

        if (bar != null) {
            bar.name(message);
            bar.progress(progress);
        }
    }

    public static void hide(Player player) {
        BossbarManager.removeBossBar(BossbarsType.TUTORIAL, player);
    }
}
