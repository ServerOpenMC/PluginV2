package fr.openmc.core.features.events.contents.weeklyevents;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.annotations.Credit;
import fr.openmc.core.bootstrap.features.types.DatabaseFeature;
import fr.openmc.core.bootstrap.features.types.LoadAfterItemsAdder;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.Contest;
import fr.openmc.core.features.events.contents.weeklyevents.models.WeeklyEvent;
import fr.openmc.core.features.events.contents.weeklyevents.models.WeeklyEventPhase;
import fr.openmc.core.features.events.contents.weeklyevents.models.WeeklyEventsData;
import fr.openmc.core.utils.text.DateUtils;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.util.List;

@Credit(developers = {"iambibi_"})
public class WeeklyEventsManager extends Feature implements LoadAfterItemsAdder, DatabaseFeature {

    private static final List<WeeklyEvent> EVENTS = List.of(
            new Contest()
    );

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
            Runnable action = currentPhase.runAction();
            if (action != null) action.run();
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
                data = new WeeklyEventsData(0, 0);
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
        return EVENTS.get(data.getCurrentEventIndex());
    }

    /**
     * Retourne l'instance de WeeklyEvent correspondant à la classe donnée, ou null si introuvable.
     */
    public static WeeklyEvent getEvent(Class<? extends WeeklyEvent> eventClass) {
        return EVENTS.stream()
                .filter(event -> event.getClass().equals(eventClass))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retourne la phase en cours selon l'index en BDD, ou null si invalide.
     */
    public static WeeklyEventPhase getCurrentPhase() {
        List<WeeklyEventPhase> phases = getCurrentEvent().getPhases();
        int index = data.getCurrentPhaseIndex();
        if (index < 0 || index >= phases.size()) return null;
        return phases.get(index);
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
            runPhase(nextPhase);
            return;
        }

        currentTask = Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            if (findNextPhase() != nextPhase) {
                scheduleNextPhase();
                return;
            }
            runPhase(nextPhase);
        }, delayTicks);
    }

    /**
     * Exécute l'action de la phase, marque l'event comme actif,
     * avance l'état, puis schedule la suivante.
     */
    private static void runPhase(WeeklyEventPhase phase) {
        data.setActive(true);
        save(data);

        Runnable action = phase.runAction();
        if (action != null) action.run();

        advancePhase();
        scheduleNextPhase();
    }

    /**
     * Avance à la phase suivante.
     * Si c'était la dernière phase, passe à l'event suivant (et marque inactif).
     */
    private static void advancePhase() {
        List<WeeklyEventPhase> phases = getCurrentEvent().getPhases();
        int nextPhaseIndex = data.getCurrentPhaseIndex() + 1;

        if (nextPhaseIndex >= phases.size()) {
            advanceToNextEvent();
        } else {
            data.setCurrentPhaseIndex(nextPhaseIndex);
            save(data);
        }
    }

    /**
     * Force un event à une phase spécifique.
     * Met à jour la BDD, exécute l'action, gère le cas dernière phase,
     * puis reschedule proprement.
     */
    public static void forceEventAtPhase(WeeklyEvent event, WeeklyEventPhase phase) {
        int eventIndex = EVENTS.indexOf(event);
        int phaseIndex = event.getPhases().indexOf(phase);

        if (eventIndex == -1 || phaseIndex == -1) {
            OMCPlugin.getInstance().getSLF4JLogger().error("[WeeklyEvents] Event ou phase non trouvé");
            return;
        }

        data.setCurrentEventIndex(eventIndex);
        data.setCurrentPhaseIndex(phaseIndex);
        data.setActive(true);
        save(data);

        Runnable action = phase.runAction();
        if (action != null) action.run();

        boolean isLastPhase = phaseIndex == event.getPhases().size() - 1;
        if (isLastPhase) {
            advanceToNextEvent();
        }

        scheduleNextPhase();

        OMCPlugin.getInstance().getSLF4JLogger().info("[WeeklyEvents] Event forcé : {} à la phase {}",
                PlainTextComponentSerializer.plainText().serialize(event.getName()),
                PlainTextComponentSerializer.plainText().serialize(phase.getName()));
    }

    /**
     * Passe à l'event suivant, réinitialise la phase à 0 et marque l'event comme inactif.
     */
    private static void advanceToNextEvent() {
        data.setActive(false);
        int nextIndex = (data.getCurrentEventIndex() + 1) % EVENTS.size();
        data.setCurrentEventIndex(nextIndex);
        data.setCurrentPhaseIndex(0);
        save(data);

        OMCPlugin.getInstance().getSLF4JLogger().info("[WeeklyEvents] Passage à l'event suivant : {}",
                PlainTextComponentSerializer.plainText().serialize(getCurrentEvent().getName()));
    }

    /**
     * Cherche la prochaine phase à venir en parcourant tous les events cycliquement.
     * Commence à la phase courante de l'event courant, puis les events suivants depuis 0.
     */
    private static WeeklyEventPhase findNextPhase() {
        int totalEvents = EVENTS.size();

        for (int i = 0; i < totalEvents; i++) {
            int eventIdx = (data.getCurrentEventIndex() + i) % totalEvents;
            WeeklyEvent event = EVENTS.get(eventIdx);
            List<WeeklyEventPhase> phases = event.getPhases();

            int phaseStart = (i == 0) ? data.getCurrentPhaseIndex() : 0;

            for (int j = phaseStart; j < phases.size(); j++) {
                WeeklyEventPhase phase = phases.get(j);
                long delay = DateUtils.getSecondsUntilDayOfWeekTime(
                        phase.getStartDay(),
                        phase.getStartHour(),
                        phase.getStartMinutes(),
                        0
                );
                if (delay >= 0) return phase;
            }
        }

        return null;
    }
}