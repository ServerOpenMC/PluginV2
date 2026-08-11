package fr.openmc.core.features.events.contents.weeklyevents;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.annotations.Credit;
import fr.openmc.core.bootstrap.features.types.HasDatabase;
import fr.openmc.core.bootstrap.features.types.LoadAfterItemsAdder;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.events.contents.weeklyevents.models.WeeklyEvent;
import fr.openmc.core.features.events.contents.weeklyevents.models.WeeklyEventPhase;
import fr.openmc.core.features.events.contents.weeklyevents.models.WeeklyEventsData;
import fr.openmc.core.utils.text.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Credit(developers = {"iambibi_"})
public class WeeklyEventsManager extends Feature implements LoadAfterItemsAdder, HasDatabase {

    private static Dao<WeeklyEventsData, Integer> dao;
    private static WeeklyEventsData data;
    private static BukkitTask currentTask = null;

    /**
     * Initialise la gestion des WeeklyEvents.
     * Au restart : si on est déjà le bon jour pour la phase courante et que l'event
     * était actif, on relance l'action immédiatement.
     */
    @Override
    public void init() {
        data = load();

        WeeklyEventPhase currentPhase = getCurrentPhase();
        if (data.isActive() && currentPhase != null && DateUtils.getCurrentDayOfWeek().equals(currentPhase.getStartDay())) {
            runPhase(getCurrentEvent(), currentPhase);
            return;
        }

        scheduleNextPhase();
    }

    /**
     * Initialise la BDD : crée la table si nécessaire, charge les données, gère le cas restart
     */
    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        dao = DaoManager.createDao(connectionSource, WeeklyEventsData.class);
        TableUtils.createTableIfNotExists(connectionSource, WeeklyEventsData.class);
    }

    /**
     * Charge les données depuis la BDD, ou crée une ligne par défaut si inexistante.
     */
    public static WeeklyEventsData load() {
        try {
            WeeklyEventsData data = dao.queryForId(1);
            if (data == null) {
                WeeklyEvent contest = OMCRegistry.WEEKLY_EVENTS.CONTEST;
                data = new WeeklyEventsData(contest, null);
                dao.create(data);
            }
            return data;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du chargement de WeeklyEventData", e);
        }
    }

    /**
     * Sauvegarde les données en BDD.
     */
    public static void save(WeeklyEventsData data) {
        try {
            dao.createOrUpdate(data);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde de WeeklyEventData", e);
        }
    }

    /**
     * Retourne l'event en cours.
     */
    public static WeeklyEvent getCurrentEvent() {
        return OMCRegistry.WEEKLY_EVENTS.get(data.getCurrentEvent()).orElse(null);
    }

    /**
     * Retourne la phase en cours selon l'index en BDD, ou null si invalide.
     */
    public static WeeklyEventPhase getCurrentPhase() {
        List<WeeklyEventPhase> phases = getCurrentEvent().getPhases();
        String phaseId = data.getCurrentPhase();
        return phases.stream()
                .filter(phase -> phase.getId().equals(phaseId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retourne true si un event est actuellement actif (flag BDD).
     * Source de vérité : le flag active, pas le temps.
     */
    public static boolean isEventActive() {
        return data.isActive();
    }

    /**
     * Planifie la prochaine phase.
     * Cancel la task précédente pour éviter les doublons.
     * Guard intégré : si findNextPhase() a changé entre le schedule et l'exécution
     * (suite à un force), on se recalibre sans exécuter la mauvaise action.
     */
    public static void scheduleNextPhase() {
        if (currentTask != null) {
            currentTask.cancel();
            currentTask = null;
        }

        WeeklyEventPhase nextPhase = findNextPhase();
        if (nextPhase == null) return;

        long delayTicks = DateUtils.getSecondsUntilDayOfWeekTime(
                nextPhase.getStartDay(),
                nextPhase.getStartHour(),
                nextPhase.getStartMinutes(),
                0
        ) * 20L;

        if (delayTicks <= 0) {
            runPhase(getCurrentEvent(), nextPhase);
            return;
        }

        OMCLogger.infoFormatted("Prochaine Phase ({}) de l'évenement weekly ({}) le "
                + nextPhase.getStartDay().getDisplayName(TextStyle.FULL_STANDALONE, Locale.FRENCH) + " "
                + nextPhase.getStartHour() + "h " + nextPhase.getStartMinutes() + "m "
                + "(dans " + DateUtils.convertSecondToTime(DateUtils.getSecondsUntilDayOfWeekTime(
                        nextPhase.getStartDay(),
                    nextPhase.getStartHour(),
                    nextPhase.getStartMinutes(),
                    0
        )) + ")", nextPhase.getId(),
                getCurrentEvent().getId());


        currentTask = Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            if (findNextPhase() != nextPhase) {
                scheduleNextPhase();
                return;
            }
            runPhase(getCurrentEvent(), nextPhase);
        }, delayTicks);
    }

    /**
     * Exécute l'action de la phase, marque l'event comme actif,
     * avance l'état, puis schedule la suivante.
     */
    private static void runPhase(WeeklyEvent event, WeeklyEventPhase phase) {
        data.setCurrentEvent(event.getId());
        data.setCurrentPhase(phase.getId());
        data.setActive(true);
        save(data);

        advancePhase();
        scheduleNextPhase();

        try {
            Runnable action = phase.runAction();
            if (action != null) action.run();
        } catch (Exception e) {
            OMCLogger.error("Erreur lors de l'exécution de la phase {}", phase.getId(), e);
        }
    }

    /**
     * Avance à la phase suivante.
     * Si c'était la dernière phase, passe à l'event suivant (et marque inactif).
     */
    private static void advancePhase() {
        WeeklyEvent event = getCurrentEvent();
        WeeklyEventPhase current = getCurrentPhase();

        event.getNextPhase(current).ifPresentOrElse(
                next -> {
                    data.setCurrentPhase(next.getId());
                    save(data);
                },
                WeeklyEventsManager::advanceToNextEvent
        );
    }

    /**
     * Force un event à une phase spécifique.
     * Met à jour la BDD, exécute l'action, gère le cas dernière phase,
     * puis reschedule proprement.
     */
    public static void forceEventAtPhase(WeeklyEvent event, WeeklyEventPhase phase) {
        if (event == null || phase == null || !event.hasPhase(phase)) {
            OMCLogger.error("[WeeklyEvents] Event ou phase non trouvé");
            return;
        }

        data.setCurrentEvent(event.getId());
        data.setCurrentPhase(phase.getId());
        save(data);

        OMCLogger.info("[WeeklyEvents] Event forcé : {} à la phase {}",
                event.getId(),
                phase.getId());

        runPhase(event, phase);
    }

    /**
     * Passe à l'event suivant, réinitialise la phase à 0 et marque l'event comme inactif.
     */
    private static void advanceToNextEvent() {
        WeeklyEvent current = getCurrentEvent();
        WeeklyEvent next = OMCRegistry.WEEKLY_EVENTS.getNextEvent(current)
                .orElseThrow(() -> new IllegalStateException("Aucun event enregistré"));

        data.setActive(false);
        data.setCurrentEvent(next.getId());
        data.setCurrentPhase(null);
        save(data);

        OMCLogger.info("[WeeklyEvents] Passage à l'event suivant : {}",
                next.getId());
    }

    /**
     * Cherche la prochaine phase à venir en parcourant tous les events cycliquement.
     * Commence à la phase courante de l'event courant, puis les events suivants depuis 0.
     */
    private static WeeklyEventPhase findNextPhase() {
        WeeklyEvent event = getCurrentEvent();
        WeeklyEventPhase current = getCurrentPhase();

        if (current == null) {
            return event.getFirstPhase();
        }

        return event.getNextPhase(current)
                .or(() -> OMCRegistry.WEEKLY_EVENTS.getNextEvent(event).map(WeeklyEvent::getFirstPhase))
                .orElse(null);
    }
}