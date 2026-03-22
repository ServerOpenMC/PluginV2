package fr.openmc.core.features.events.weeklyevents;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.events.weeklyevents.contents.contest.Contest;
import fr.openmc.core.features.events.weeklyevents.models.WeeklyEvent;
import fr.openmc.core.features.events.weeklyevents.models.WeeklyEventPhase;
import fr.openmc.core.features.events.weeklyevents.models.WeeklyEventsData;
import fr.openmc.core.utils.DateUtils;
import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.util.List;

public class WeeklyEventsManager {

    private static final List<WeeklyEvent> EVENTS = List.of(
            new Contest()
    );

    private static Dao<WeeklyEventsData, Integer> dao;
    private static WeeklyEventsData data;

    public static void initDB(ConnectionSource connectionSource) throws SQLException {
        dao = DaoManager.createDao(connectionSource, WeeklyEventsData.class);
        TableUtils.createTableIfNotExists(connectionSource, WeeklyEventsData.class);

        data = load();
        scheduleNextPhase();
    }

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

    public static void save(WeeklyEventsData data) {
        try {
            dao.createOrUpdate(data);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde de WeeklyEventData", e);
        }
    }

    public static WeeklyEvent getCurrentEvent() {
        return EVENTS.get(data.getCurrentEventIndex());
    }

    public static WeeklyEventPhase getCurrentPhase() {
        List<WeeklyEventPhase> phases = getCurrentEvent().getPhases();
        int index = data.getCurrentPhaseIndex();
        if (index < 0 || index >= phases.size()) return null;
        return phases.get(index);
    }

    public static void scheduleNextPhase() {
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

        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            runPhase(nextPhase);
            scheduleNextPhase();
        }, delayTicks);
    }

    private static void runPhase(WeeklyEventPhase phase) {
        Runnable action = phase.runAction();
        if (action != null) action.run();

        List<WeeklyEventPhase> phases = getCurrentEvent().getPhases();
        int nextPhaseIndex = data.getCurrentPhaseIndex() + 1;

        if (nextPhaseIndex >= phases.size()) {
            advanceToNextEvent();
        } else {
            data.setCurrentPhaseIndex(nextPhaseIndex);
            save(data);
        }
    }

    private static void advanceToNextEvent() {
        int nextIndex = (data.getCurrentEventIndex() + 1) % EVENTS.size();
        data.setCurrentEventIndex(nextIndex);
        data.setCurrentPhaseIndex(0);
        save(data);

        OMCPlugin.getInstance().getSLF4JLogger().info("[WeeklyEvents] Passage à l'event suivant : {}", getCurrentEvent().getName().toString());
    }

    private static WeeklyEventPhase findNextPhase() {
        WeeklyEvent event = getCurrentEvent();
        List<WeeklyEventPhase> phases = event.getPhases();

        int phaseStart = data.getCurrentPhaseIndex();

        for (int i = phaseStart; i < phases.size(); i++) {
            WeeklyEventPhase phase = phases.get(i);
            long delay = DateUtils.getSecondsUntilDayOfWeekTime(
                    phase.getStartDay(),
                    phase.getStartHour(),
                    phase.getStartMinutes(),
                    0
            );
            if (delay >= 0) return phase;
        }
        return null;
    }
}
