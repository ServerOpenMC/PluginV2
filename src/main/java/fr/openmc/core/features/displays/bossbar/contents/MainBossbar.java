package fr.openmc.core.features.displays.bossbar.contents;

import fr.openmc.core.features.displays.bossbar.BaseBossbar;
import fr.openmc.core.features.dream.DreamUtils;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.entity.Player;

/**
 * Boss bar principale affichant les messages d'aide.
 */
public class MainBossbar extends BaseBossbar {

    private static int indexMessage = 0;

    /**
     * @return L'identifiant unique de cette boss bar
     */
    @Override
    protected String id() {
        return "omc:help";
    }

    /**
     * Met à jour le contenu affiché dans la boss bar.
     *
     * @param player Le joueur
     * @param bar La boss bar à mettre à jour
     */
    @Override
    protected void update(Player player, BossBar bar) {
        indexMessage = (indexMessage + 1) % HelpConfigManager.getHelpMessages().size();

        bar.name(HelpConfigManager.getHelpMessages().get(indexMessage));
    }

    /**
     * @param player Le joueur
     * @return La couleur de la boss bar
     */
    @Override
    protected BossBar.Color color(Player player) {
        return BossBar.Color.RED;
    }

    /**
     * @param player Le joueur
     * @return Le style de la boss bar
     */
    @Override
    protected BossBar.Overlay style(Player player) {
        return BossBar.Overlay.PROGRESS;
    }

    /**
     * @param player Le joueur
     * @return true si la boss bar doit être affichée
     */
    @Override
    protected boolean shouldDisplay(Player player) {
        return !DreamUtils.isInDreamWorld(player);
    }

    /**
     * @return Le poids d'affichage de la boss bar
     */
    @Override
    protected int weight() {
        return 10;
    }

    /**
     * @return L'intervalle de mise à jour en secondes
     */
    @Override
    protected Integer updateInterval() {
        return 20;
    }
}
