package fr.openmc.core.features.events.contents.dailyevents;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.annotations.Credit;
import fr.openmc.core.bootstrap.features.types.DatabaseFeature;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.bootstrap.features.types.LoadAfterItemsAdder;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.BloodyNightEvent;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.GoldenHarvestEvent;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.MiraculousFishingEvent;
import fr.openmc.core.features.events.contents.dailyevents.listeners.DailyEventAmbientListeners;
import fr.openmc.core.features.events.contents.dailyevents.models.IncomingEventsDB;
import fr.openmc.core.features.events.contents.dailyevents.models.ScheduleDailyEvent;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.DailyEvent;
import fr.openmc.core.features.events.contents.dailyevents.tasks.ScheduleNextEventTask;
import fr.openmc.core.features.events.contents.dailyevents.tasks.ShowBeginningEventToastTask;
import fr.openmc.core.utils.text.DateUtils;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;


//todo: ajouter des javadocs et commentaires sur certaines parties
@Credit(developers = {"iambibi_"})
public class DailyEventsManager extends Feature implements LoadAfterItemsAdder, DatabaseFeature, HasListeners {
    // * Constantes
    public static final List<DailyEvent> EVENTS = List.of(
        new MiraculousFishingEvent(),
            new GoldenHarvestEvent(),
            new BloodyNightEvent()
    );

    private static final List<Integer> SLOT_HOURS_EVENTS = new ArrayList<>(List.of(
            9, 13, 16, 19, 21
    ));

    public static final int SHOW_BEGINNING_TOAST_DELAY = 60; // en secondes

    // * Données à propos de la gestion des daily event
    public static ScheduleDailyEvent outgoingEvent = null;
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
        return Set.of(
                new DailyEventAmbientListeners()
        );
    }

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
                copyEvents = generateRandomOrder();
            }

            LocalDateTime scheduledDailyEvent;
            if (hourSlot > now.getHour()) {
                scheduledDailyEvent=now.withHour(hourSlot).withMinute(0).withSecond(0).withNano(0);
            } else {
                scheduledDailyEvent=now.plusDays(1)
                        .withHour(hourSlot).withMinute(0).withSecond(0).withNano(0);
            }
            scheduledEvents.add(new ScheduleDailyEvent(copyEvents.removeFirst(), scheduledDailyEvent));
        }

        return scheduledEvents;
    }

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

        OMCLogger.infoFormatted("Les prochains evenement : " + incomingEvents);
        OMCLogger.infoFormatted("Prochain Evenement journalier : " + scheduleTime + "s (dans " + DateUtils.convertSecondToTime(DateUtils.getDelayBetweenNow(scheduleTime)) + ")");

        new ShowBeginningEventToastTask()
                .runTaskLater(OMCPlugin.getInstance(), delayTicks - SHOW_BEGINNING_TOAST_DELAY * 20L);

        return new ScheduleNextEventTask().runTaskLater(OMCPlugin.getInstance(), delayTicks);
    }

    private static List<DailyEvent> generateRandomOrder() {
        List<DailyEvent> copyEvents = new ArrayList<>(EVENTS);
        Collections.shuffle(copyEvents);
        return copyEvents;
    }

    public static boolean isActiveDailyEvent() {
        return outgoingEvent != null;
    }
}
