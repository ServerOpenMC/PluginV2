package fr.openmc.core.features.events.contents.dailyevents.display;

import fr.openmc.core.features.displays.bossbar.BaseBossbar;
import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.DailyEvent;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.HasBossBar;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

/**
 * Boss bar affichant le temps restant de l'événement
 */
public class DailyEventBossbar extends BaseBossbar {

    /**
     * @return L'identifiant unique de cette boss bar
     */
    @Override
    protected String id() {
        return "omc:dailyevents";
    }

    /**
     * Met à jour le contenu affiché dans la boss bar.
     *
     * @param player Le joueur
     * @param bar La boss bar à mettre à jour
     */
    @Override
    protected void update(Player player, BossBar bar) {
        DailyEvent event = DailyEventsManager.getActiveDailyEvent();
        if (!(event instanceof HasBossBar hasBossBar)) return;

        bar.name(TranslationManager.translation("feature.dailyevents.bossbar.name",
                Component.text(
                        DateUtils.convertSecondToTime(
                                (long) DailyEventsManager.getRemainingTime(event)), event.getMainColor())
                ));
    }

    @Override
    protected Float progress(Player player) {
        DailyEvent event = DailyEventsManager.getActiveDailyEvent();
        float remainingSeconds = DailyEventsManager.getRemainingTime(event);
        float totalSeconds = event.getDuration() * 60f;
        return remainingSeconds / totalSeconds;
    }

    /**
     * @param player Le joueur
     * @return La couleur de la boss bar
     */
    @Override
    protected BossBar.Color color(Player player) {
        if (DailyEventsManager.isActiveDailyEvent()
                && DailyEventsManager.getActiveDailyEvent() instanceof HasBossBar hasBossBar) {
            return hasBossBar.getBossBarColor();
        }

        return BossBar.Color.WHITE;
    }

    /**
     * @param player Le joueur
     * @return Le style de la boss bar
     */
    @Override
    protected BossBar.Overlay style(Player player) {
        if (DailyEventsManager.isActiveDailyEvent()
                && DailyEventsManager.getActiveDailyEvent() instanceof HasBossBar hasBossBar) {
            return hasBossBar.getBossBarOverlay();
        }

        return BossBar.Overlay.PROGRESS;
    }

    /**
     * @param player Le joueur
     * @return true si la boss bar doit être affichée
     */
    @Override
    protected boolean shouldDisplay(Player player) {
        return DailyEventsManager.isActiveDailyEvent()
                && DailyEventsManager.getActiveDailyEvent() instanceof HasBossBar
                && player.getWorld().getName().equals(DailyEventsManager.getActiveDailyEvent().getWorldEvent());
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
        return 1;
    }
}
