package fr.openmc.core.features.dream.displays;

import fr.openmc.core.features.displays.bossbar.BossbarManager;
import fr.openmc.core.features.displays.bossbar.BossbarsType;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

/**
 * Classe utilitaire pour la gestion de la BossBar dans la Dimension des Rêves.
 *
 * <p>Cette classe permet d'ajouter, mettre à jour et cacher la BossBar associée à un joueur.</p>
 */
public class DreamBossBar {

    private static final Component TEXTURE_BOSSBAR = Component.text("");

    /**
     * Ajoute une BossBar pour le joueur spécifié dans la Dimension des Rêves.
     *
     * @param player le joueur auquel ajouter la BossBar
     * @param progress la progression de la BossBar
     */
    public static void addDreamBossBarForPlayer(Player player, float progress) {
        BossBar bar = BossBar.bossBar(
                TEXTURE_BOSSBAR,
                progress,
                BossBar.Color.BLUE,
                BossBar.Overlay.PROGRESS
        );
        BossbarManager.addBossBar(BossbarsType.DREAM, bar, player);
    }

    /**
     * Met à jour la progression de la BossBar pour le joueur spécifié.
     *
     * @param player le joueur dont la BossBar doit être mise à jour
     * @param progress la nouvelle progression de la BossBar
     */
    public static void update(Player player, float progress) {
        BossBar bar = BossbarManager.getBossBar(BossbarsType.DREAM, player);
        if (bar != null) {
            bar.progress(progress);
        }
    }

    /**
     * Cache (supprime) la BossBar du joueur spécifié.
     *
     * @param player le joueur dont la BossBar doit être supprimée
     */
    public static void hide(Player player) {
        BossbarManager.removeBossBar(BossbarsType.DREAM, player);
    }
}
