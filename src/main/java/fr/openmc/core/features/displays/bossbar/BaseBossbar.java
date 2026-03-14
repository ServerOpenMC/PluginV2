package fr.openmc.core.features.displays.bossbar;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;

public abstract class BaseBossbar {
    /**
     * Initialise la boss bar pour un joueur
     *
     * @param player Le joueur
     */
    public void init(Player player, BossBar bar) {
        update(player, bar);
    }

    protected abstract String id();

    /**
     * Met à jour la boss bar
     *
     * @param player Le joueur
     */
    protected abstract void update(Player player, BossBar bar);

    /**
     * Détermine la couleur de la boss bar
     * @param player Le joueur
     * @return La couleur de la boss bar
     */
    protected abstract BossBar.Color color(Player player);

    /**
     * Détermine le style de la boss bar
     * @param player Le joueur
     * @return Le style de la boss bar
     */
    protected abstract BossBar.Overlay style(Player player);

    /**
     * Détermine si le boss bar doit être affiché pour un joueur
     *
     * @param player Le joueur à vérifier
     * @return true si la boss bar doit être affiché, false sinon
     */
    protected abstract boolean shouldDisplay(Player player);

    /**
     * @return Le poids de la boss bar (plus la valeur est haute, plus la position de la boss bar sera haute).
     */
    protected abstract int weight();

    /**
     * @return L'intervalle de mise à jour en secondes
     */
    protected abstract Integer updateInterval();
}
