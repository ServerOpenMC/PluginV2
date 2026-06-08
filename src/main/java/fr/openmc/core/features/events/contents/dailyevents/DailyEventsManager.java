package fr.openmc.core.features.events.contents.dailyevents;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.annotations.Credit;
import fr.openmc.core.bootstrap.features.types.DatabaseFeature;
import fr.openmc.core.bootstrap.features.types.LoadAfterItemsAdder;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.BloodyNightEvent;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.GoldenHarvestEvent;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.MiraculousFishingEvent;
import fr.openmc.core.features.events.contents.dailyevents.models.DailyEvent;
import fr.openmc.core.features.events.contents.dailyevents.models.IncomingEventsDB;
import fr.openmc.core.features.events.contents.dailyevents.models.ScheduleDailyEvent;
import fr.openmc.core.utils.text.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


//todo: ajouter des javadocs et commentaires sur certaines parties
@Credit(developers = {"iambibi_"})
public class DailyEventsManager extends Feature implements LoadAfterItemsAdder, DatabaseFeature {
    // * Constantes
    public static final List<DailyEvent> EVENTS = List.of(
        new MiraculousFishingEvent(),
            new GoldenHarvestEvent(),
            new BloodyNightEvent()
    );

    private final List<Integer> SLOT_HOURS_EVENTS = new ArrayList<>(List.of(
            9, 13, 16, 21
    ));

    // * Données à propos de la gestion des daily event
    private ScheduleDailyEvent outgoingEvent = null;
    private BukkitTask nextEventTask;
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
    private List<ScheduleDailyEvent> loadIncomingEvents() {
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

    //todo: diviser ça en plusieurs sous méthodes
    private BukkitTask scheduleNextEventTask() {
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

        OMCLogger.info("Les prochains evenement : " + incomingEvents);
        OMCLogger.info("Prochain Evenement journalier : " + scheduleTime + "s (dans " + DateUtils.convertTime(DateUtils.getDelayBetweenNow(scheduleTime)) + ")");

        //todo: toast 1 min avant commencement

        return Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            if (incomingEvents.isEmpty()) {
                incomingEvents = loadIncomingEvents();
            }

            outgoingEvent = incomingEvents.removeFirst();

            // * Commencement de l'evenement
            outgoingEvent.getDailyEvent().onStart().run();

            //todo: setup ambient (interface EventAmbient)

            // * Programmation de la fin de l'evenement
            Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
                outgoingEvent.getDailyEvent().onEnd().run();
                outgoingEvent = null;

                //todo: remove ambient
                //todo: end toast

            }, outgoingEvent.getDailyEvent().getDuration() * 20L * 20L);

            // * 10 secondes d'attente avant de schedule un autre event (evite que plusieurs events se lancent en meme temps)
            Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () ->
                    nextEventTask = scheduleNextEventTask(), 20L * 10);
        }, delayTicks);
    }

    private List<DailyEvent> generateRandomOrder() {
        List<DailyEvent> copyEvents = new ArrayList<>(EVENTS);
        Collections.shuffle(copyEvents);
        return copyEvents;
    }
}
