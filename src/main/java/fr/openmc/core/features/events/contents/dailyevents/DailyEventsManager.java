package fr.openmc.core.features.events.contents.dailyevents;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.annotations.Credit;
import fr.openmc.core.bootstrap.features.types.*;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.bootstrap.listeners.ListenerFactory;
import fr.openmc.core.features.events.contents.dailyevents.commands.DailyEventCommand;
import fr.openmc.core.features.events.contents.dailyevents.listeners.DailyEventAmbientListeners;
import fr.openmc.core.features.events.contents.dailyevents.models.IncomingEventsDB;
import fr.openmc.core.features.events.contents.dailyevents.models.ScheduleDailyEvent;
import fr.openmc.core.features.events.contents.dailyevents.models.SlotDailyEvent;
import fr.openmc.core.features.events.contents.dailyevents.models.dailyevent.DailyEvent;
import fr.openmc.core.utils.RandomUtils;
import fr.openmc.core.utils.nms.toast.ToastUtils;
import fr.openmc.core.utils.scheduler.OMCScheduler;
import fr.openmc.core.utils.scheduler.OMCTask;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import fr.openmc.core.utils.world.WorldUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;


@Credit(developers = {"iambibi_"})
public class DailyEventsManager extends Feature implements LoadAfterItemsAdder, HasDatabase, HasListeners, HasCommands {
    public static final int SHOW_BEGINNING_DELAY = 60; // en secondes

    // * Données à propos de la gestion des daily event
    public static ScheduleDailyEvent outgoingEvent = null;
    public static OMCTask endEventTask = null;
    public static OMCTask nextEventTask;
    public static List<ScheduleDailyEvent> incomingEvents = new ArrayList<>();

    private static Dao<IncomingEventsDB, Integer> dao;

    @Override
    public void init() {
        // * Register les sous features
        for (DailyEvent event : OMCRegistry.DAILY_EVENTS.values()) {
            if (!(event instanceof HasFeature hasFeature)) continue;

            OMCPlugin.registerFeature(hasFeature.getFeature());
        }

        incomingEvents = loadIncomingEvents();
        nextEventTask = scheduleNextEventTask();
    }

    @Override
    public void save() {
        if (outgoingEvent != null) {
            outgoingEvent.getDailyEvent().end();
        }

        saveIncomingEventsDB(incomingEvents);
    }

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        dao = DaoManager.createDao(connectionSource, IncomingEventsDB.class);
        TableUtils.createTableIfNotExists(connectionSource, IncomingEventsDB.class);
    }

    @Override
    public Set<ListenerFactory> getListeners() {
        Set<ListenerFactory> listeners = new HashSet<>(Set.of(
                DailyEventAmbientListeners::new
        ));

        for (DailyEvent event : OMCRegistry.DAILY_EVENTS.values()) {
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
     * @param scheduledEvents les données des daily events
     */
    public static void saveIncomingEventsDB(List<ScheduleDailyEvent> scheduledEvents) {
        try {
            dao.deleteById(1);
            dao.create(new IncomingEventsDB(scheduledEvents));
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
        LocalDateTime now = DateUtils.getLocalDateTime();
        IncomingEventsDB data = loadIncomingEventsDB();
        List<DailyEvent> copyEvents = new ArrayList<>(data.getDailyEventsIncomings());

        List<ScheduleDailyEvent> scheduledEvents = new ArrayList<>();
        for (LocalDateTime slotDate : SlotDailyEvent.getSlotsBetween(now, now.plusDays(1))) {
            if (copyEvents.isEmpty()) {
                copyEvents = RandomUtils.generateRandomOrder(OMCRegistry.DAILY_EVENTS.values().stream().toList());
            }
            scheduledEvents.add(new ScheduleDailyEvent(copyEvents.removeFirst(), slotDate));
        }

        // * Tri chornologique des dates
        scheduledEvents.sort(Comparator.comparing(ScheduleDailyEvent::getScheduledStartDate));

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
    public static OMCTask scheduleNextEventTask() {
        LocalDateTime now = DateUtils.getLocalDateTime();

        // si c'est null, alors on rempli la file et on schedule pour le premier horaire
        if (incomingEvents.isEmpty()) {
            incomingEvents = loadIncomingEvents();
        }

        LocalDateTime scheduleTime = SlotDailyEvent.getNextSlot(now);
        if (scheduleTime == null) return null;

        long delaySeconds = DateUtils.getDelayBetweenNow(scheduleTime);
        OMCLogger.infoFormatted("Les prochains evenement : " + incomingEvents.stream()
                .map(s -> s.getDailyEvent().getClass().getSimpleName()).toList());
        OMCLogger.infoFormatted("Prochain Evenement journalier : " + scheduleTime
                + " (dans " + DateUtils.convertSecondToTime(delaySeconds) + ")");

        // * Programation de la tâche qui s'executera peu avant le commencement
        OMCScheduler.runAt(scheduleTime.minusSeconds(SHOW_BEGINNING_DELAY), () -> {
            ScheduleDailyEvent nextEvent = DailyEventsManager.incomingEvents.getFirst();
            DailyEvent dailyEvent = nextEvent.getDailyEvent();

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                Component name;

                // ** Affichage d'un message dans le chat
                if (dailyEvent.getWorldEvent().contains(onlinePlayer.getWorld().getName())) {
                    onlinePlayer.sendMessage(
                            TranslationManager.translation("feature.dailyevents.broadcast.soon"));
                }

                // ** Affichage du Toast
                if (dailyEvent.getWorldEvent().contains(onlinePlayer.getWorld().getName())) {
                    name = TranslationManager.translation("feature.dailyevents.toast.beginning_event_in_world",
                            Component.text(DailyEventsManager.SHOW_BEGINNING_DELAY, NamedTextColor.YELLOW));
                } else {
                    name = TranslationManager.translation("feature.dailyevents.toast.beginning_event_out_world",
                            TranslationManager.translation(WorldUtils.getDisplayedWorldName(dailyEvent.getWorldEvent())),
                            Component.text(DailyEventsManager.SHOW_BEGINNING_DELAY, NamedTextColor.YELLOW));
                }

                ToastUtils.sendCustomToast(
                        onlinePlayer,
                        Material.NOTE_BLOCK,
                        name,
                        AdvancementType.TASK
                );
            }
        });

        // * Renvoie la tache qui executera le début de l'evenement
        return OMCScheduler.runAt(scheduleTime, () -> {
            // * Choix de l'evenement à lancer
            if (DailyEventsManager.incomingEvents.isEmpty()) {
                DailyEventsManager.incomingEvents = DailyEventsManager.loadIncomingEvents();
            }
            DailyEventsManager.outgoingEvent = DailyEventsManager.incomingEvents.removeFirst();

            // * Lancement de l'evenement
            DailyEventsManager.outgoingEvent.getDailyEvent().start();

            // * 10 secondes d'attente avant de schedule un autre event (evite que plusieurs events se lancent en meme temps)
            Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () ->
                    DailyEventsManager.nextEventTask = DailyEventsManager.scheduleNextEventTask(), 20L * 10);
        });
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

    /**
     * Permet d'obtenir le temps restant d'un evenement journalier en secondes
     * @param event l'event
     * @return le temps restant en secondes
     */
    public static float getRemainingTime(DailyEvent event) {
        if (!isActiveDailyEvent()) return -1;
        if (getActiveDailyEvent() != event) return -1;

        float remainingTime = DateUtils.getLocalDateTime().until(outgoingEvent.getScheduledEndDate(), ChronoUnit.SECONDS);
        return remainingTime < 0 ? 0 : remainingTime;
    }
}
