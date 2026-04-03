package fr.openmc.core.features.dream.displays;

import fr.openmc.core.features.displays.bossbar.BaseBossbar;
import fr.openmc.core.features.displays.bossbar.BossbarManager;
import fr.openmc.core.features.displays.bossbar.BossbarsType;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.models.db.DreamPlayer;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

/**
 * Classe utilitaire pour la gestion de la BossBar dans la Dimension des Rêves.
 *
 * <p>Cette classe permet d'ajouter, mettre à jour et cacher la BossBar associée à un joueur.</p>
 */
public class DreamBossBar extends BaseBossbar {
    @Override
    protected String id() {
        return "omc:dream";
    }

    @Override
    protected void update(Player player, BossBar bar) {
        DreamPlayer dreamPlayer = DreamManager.getDreamPlayer(player);

        if (dreamPlayer == null) return;

        float progress = Math.min(1, (float) dreamPlayer.getDreamTime() / dreamPlayer.getMaxDreamTime());

        bar.progress(progress);
    }

    @Override
    protected BossBar.Color color(Player player) {
        return BossBar.Color.BLUE;
    }

    @Override
    protected BossBar.Overlay style(Player player) {
        return BossBar.Overlay.PROGRESS;
    }

    @Override
    protected boolean shouldDisplay(Player player) {
        return DreamUtils.isInDreamWorld(player);
    }

    @Override
    protected int weight() {
        return 10;
    }

    @Override
    protected Integer updateInterval() {
        return 1;
    }
}
