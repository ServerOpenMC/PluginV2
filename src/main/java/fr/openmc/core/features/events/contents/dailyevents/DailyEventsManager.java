package fr.openmc.core.features.events.contents.dailyevents;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.annotations.Credit;
import fr.openmc.core.bootstrap.features.types.DatabaseFeature;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.bootstrap.features.types.LoadAfterItemsAdder;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.events.contents.dailyevents.commands.DailyEventCommand;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.BloodyNightEvent;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.GoldenHarvestEvent;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.MiraculousFishingEvent;
import fr.openmc.core.features.events.contents.dailyevents.listeners.DailyEventAmbientListeners;
import fr.openmc.core.features.events.contents.dailyevents.models.IncomingEventsDB;
import fr.openmc.core.features.events.contents.dailyevents.models.ScheduleDailyEvent;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.DailyEvent;
import fr.openmc.core.features.events.contents.dailyevents.tasks.NextEventTask;
import fr.openmc.core.features.events.contents.dailyevents.tasks.ShowBeginningEventTask;
import fr.openmc.core.utils.RandomUtils;
import fr.openmc.core.utils.text.DateUtils;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


//todo: tester les toasts lorsqu'ils refonctionneront (before, start, end)
@Credit(developers = {"iambibi_"})
public class DailyEventsManager extends Feature implements LoadAfterItemsAdder, DatabaseFeature, HasListeners, HasCommands {
    // * Constantes
    public static final List<DailyEvent> EVENTS = List.of(
            new MiraculousFishingEvent(),
            new GoldenHarvestEvent(),
            new BloodyNightEvent()
    );

    public static final DailyEvent MIRACULOUS_FISHING = getDailyEvent("miraculous_fishing");
    public static final DailyEvent GOLDEN_HARVEST = getDailyEvent("golden_harvest");
    public static final DailyEvent BLOODY_NIGHT = getDailyEvent("bloody_night");

    private static final List<Integer> SLOT_HOURS_EVENTS = new ArrayList<>(List.of(
            9, 13, 16, 19, 21
    ));

    public static final int SHOW_BEGINNING_DELAY = 60; // en secondes

    // * Données à propos de la gestion des daily event
    public static ScheduleDailyEvent outgoingEvent = null;
    public static BukkitTask endEventTask = null;
    public static BukkitTask nextEventTask;
    public static List<ScheduleDailyEvent> incomingEvents = new ArrayList<>();

    private static Dao<IncomingEventsDB, Integer> dao;

    @Override
    public void init() {
        incomingEvents = loadIncomingEvents();
        nextEventTask = scheduleNextEventTask();
    }

    @Override
    public void save() {
        saveIncomingEventsDB(new IncomingEventsDB(incomingEvents));
    }

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        dao = DaoManager.createDao(connectionSource, IncomingEventsDB.class);
        TableUtils.createTableIfNotExists(connectionSource, IncomingEventsDB.class);
    }

    @Override
    public Set<Listener> getListeners() {
        Set<Listener> listeners = new HashSet<>(Set.of(
                new DailyEventAmbientListeners()
        ));

        for (DailyEvent event : EVENTS) {
            if (!(event instanceof HasListeners hasListeners)) continue;
            listeners.addAll(hasListeners.getListeners());
        }

        return listeners;
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new DailyEventCommand()
        );
    }

    /**
     * Charge les données de la DB, généralement pdt le démarrage
     *
     * @return les données des daily event (ordre actuel)
     */
    public static IncomingEventsDB loadIncomingEventsDB() {
        try {
            IncomingEventsDB data = dao.queryForId(1);
            if (data == null) {
                data = new IncomingEventsDB(List.of());
                dao.create(data);
            }
            return data;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du chargement de IncomingEventsDB", e);
        }
    }

    /**
     * Sauvegarde les données des Daily Event dans la DB, généralement pdt l'arrêt du serveur
     *
     * @param data les données des daily events
     */
    public static void saveIncomingEventsDB(IncomingEventsDB data) {
        try {
            dao.createOrUpdate(data);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde de IncomingEventsDB", e);
        }
    }

    /**
     * On charge les evenements à venir
     * Lors du premier chargement, on remplit directement notre liste de taille identique à nos slots horaires.
     *
     * @return la liste prévue des x prochains évenements
     */
    public static List<ScheduleDailyEvent> loadIncomingEvents() {
        List<ScheduleDailyEvent> scheduledEvents = new ArrayList<>();
        LocalDateTime now = DateUtils.getLocalDateTime();
        IncomingEventsDB data = loadIncomingEventsDB();
        List<DailyEvent> incomingEvent = data.getDailyEventsIncomings();

        // * Si la liste est vide, soit c'est la premiere fois qu'on lance le plugin, soit que tout les events sont fini
        List<DailyEvent> copyEvents = new ArrayList<>(incomingEvent);
        for (int hourSlot : SLOT_HOURS_EVENTS) {
            if (copyEvents.isEmpty()) {
                copyEvents = RandomUtils.generateRandomOrder(EVENTS);
            }

            LocalDateTime scheduledDailyEvent;
            if (hourSlot > now.getHour()) {
                scheduledDailyEvent = now.withHour(hourSlot).withMinute(0).withSecond(0).withNano(0);
            } else {
                scheduledDailyEvent = now.plusDays(1)
                        .withHour(hourSlot).withMinute(0).withSecond(0).withNano(0);
            }
            scheduledEvents.add(new ScheduleDailyEvent(copyEvents.removeFirst(), scheduledDailyEvent));
        }

        return scheduledEvents;
    }

    /**
     * On schedule le prochain événement à venir.
     * - On cherche la prochaine heure, en faisant gaffe si l'heure est passée, dans ce cas on schedule pour demain
     * - Apres la prochaine heure on lance les taches associés à l'événement (tache pour avant le commencement,
     * et pour le lancement de l'événement et la planification du prochain
     *
     * @return la tache de lancement de l'événement, qui sera executé à l'heure exacte du début de l'événement
     */
    public static BukkitTask scheduleNextEventTask() {
        LocalDateTime now = DateUtils.getLocalDateTime();
        // * On cherche la prochaine heure
        Integer nextHour = SLOT_HOURS_EVENTS.stream()
                .filter(hour -> hour > now.getHour())
                .findFirst()
                .orElse(null);

        // si c'est null, alors on rempli la file et on schedule pour le premier horaire
        LocalDateTime scheduleTime;
        if (nextHour == null) {
            incomingEvents = loadIncomingEvents();
            scheduleTime = now.toLocalDate().plusDays(1).atTime(SLOT_HOURS_EVENTS.getFirst(), 0);
        } else {
            scheduleTime = now.toLocalDate().atTime(nextHour, 0);
        }

        long delayTicks = DateUtils.getDelayBetweenNow(scheduleTime) * 20;

        OMCLogger.infoFormatted("Les prochains evenement : " + incomingEvents.stream()
                .map(s -> s.getDailyEvent().getClass().getSimpleName()).toList());
        OMCLogger.infoFormatted("Prochain Evenement journalier : " + scheduleTime + "s (dans " + DateUtils.convertSecondToTime(DateUtils.getDelayBetweenNow(scheduleTime)) + ")");

        // * Programation de la tâche qui s'executera peu avant le commencement
        new ShowBeginningEventTask()
                .runTaskLater(OMCPlugin.getInstance(), delayTicks - SHOW_BEGINNING_DELAY * 20L);

        // * Renvoie la tache qui executera le début de l'evenement
        return new NextEventTask().runTaskLater(OMCPlugin.getInstance(), delayTicks);
    }

    /**
     * Méthode plus claire afin de dire s'il y a un evenement journalier actif ou non
     *
     * @return un boolean
     */
    public static boolean isActiveDailyEvent() {
        return outgoingEvent != null;
    }

    /**
     * Méthode plus claire afin de dire renvoyer l'evenement journalier actif
     *
     * @return un daily event
     */
    public static DailyEvent getActiveDailyEvent() {
        return outgoingEvent.getDailyEvent();
    }

    public static DailyEvent getDailyEvent(String id) {
        return EVENTS.stream()
                .filter(event -> event.getEventId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
