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
import fr.openmc.core.features.events.contents.dailyevents.contents.bloodynight.BloodyNightEvent;
import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.GoldenHarvestEvent;
import fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.MiraculousFishingEvent;
import fr.openmc.core.features.events.contents.dailyevents.models.DailyEvent;
import fr.openmc.core.features.events.contents.dailyevents.models.IncomingEventsDB;
import fr.openmc.core.utils.text.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Credit(developers = {"iambibi_"})
public class DailyEventsManager extends Feature implements LoadAfterItemsAdder, DatabaseFeature {
    // * Constantes
    public static final List<DailyEvent> EVENTS = List.of(
        new MiraculousFishingEvent(),
            new GoldenHarvestEvent(),
            new BloodyNightEvent()
    );

    private final List<Integer> SLOT_HOURS_EVENTS = new ArrayList<>(List.of(
            9, 13, 16, 19, 20
    ));

    // * Données à propos de la gestion des daily event
    private DailyEvent outgoingEvent = null;
    private BukkitTask nextEventTask;
    public static List<DailyEvent> incomingEvents = new ArrayList<>();

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
    private List<DailyEvent> loadIncomingEvents() {
        IncomingEventsDB data = loadIncomingEventsDB();
        List<DailyEvent> incomingEvent = data.getDailyEventsIncomings();

        System.out.println("before incomingEvent " + incomingEvent);

        // * Si la liste est vide, soit c'est la premiere fois qu'on lance le plugin, soit que tout les events sont fini
        if (incomingEvent.isEmpty()) {
            List<DailyEvent> copyEvents = generateRandomOrder();

            for (int _ : SLOT_HOURS_EVENTS) {
                if (copyEvents.isEmpty()) {
                    copyEvents = generateRandomOrder();
                }

                incomingEvent.add(copyEvents.removeFirst());
            }
        }

        System.out.println("after incomingEvent " + incomingEvent);
        return incomingEvent;
    }

    private BukkitTask scheduleNextEventTask() {
        LocalDateTime now = LocalDateTime.now();
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

        System.out.println("Scheduling next daily event in " + delayTicks / 20 + " seconds at " + scheduleTime);

        return Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            outgoingEvent = incomingEvents.removeFirst();

            outgoingEvent.onStart();
            nextEventTask = scheduleNextEventTask();
        }, delayTicks);
    }

    private List<DailyEvent> generateRandomOrder() {
        List<DailyEvent> copyEvents = new ArrayList<>(EVENTS);
        Collections.shuffle(copyEvents);
        return copyEvents;
    }
}
