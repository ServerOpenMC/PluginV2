package fr.openmc.core.features.events.contents.deposit;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcManager;
import fr.openmc.api.omcplayer.OMCOfflinePlayer;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.*;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.bootstrap.listeners.ListenerFactory;
import fr.openmc.core.features.events.contents.deposit.commands.DepositEventAdminCommands;
import fr.openmc.core.features.events.contents.deposit.listener.DepositListener;
import fr.openmc.core.features.events.contents.deposit.models.DepositDataModel;
import fr.openmc.core.features.events.contents.deposit.models.DepositEventModel;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.hooks.FancyNpcsHook;
import fr.openmc.core.utils.scheduler.OMCScheduler;
import fr.openmc.core.utils.scheduler.OMCTask;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DepositEventManager extends Feature implements HasDatabase, HasCommands, HasListeners, LoadIfEnable<FancyNpcsHook>, LoadAfterItemsAdder {

    private static final Map<String, Map<UUID, DepositDataModel>> depositData = new ConcurrentHashMap<>();
    private static final Map<String, DepositEventModel> eventInfo = new ConcurrentHashMap<>();
    private static final Map<String, OMCTask> endTasks = new ConcurrentHashMap<>();

    private static Dao<DepositDataModel, String> depositDataDao;
    private static Dao<DepositEventModel, String> eventInfoDao;

    @Override
    public void init() {
        loadAll();

        for (DepositEvent event : OMCRegistry.DEPOSIT_EVENTS.values()) {
            syncWithEventDates(event);
            scheduleEnd(event);
        }
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(new DepositEventAdminCommands());
    }

    @Override
    public Set<ListenerFactory> getListeners() {
        return Set.of(DepositListener::new);
    }

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, DepositDataModel.class);
        TableUtils.createTableIfNotExists(connectionSource, DepositEventModel.class);
        depositDataDao = DaoManager.createDao(connectionSource, DepositDataModel.class);
        eventInfoDao = DaoManager.createDao(connectionSource, DepositEventModel.class);
    }

    public static void loadAll() {
        try {
            for (DepositDataModel data : depositDataDao.queryForAll()) {
                depositData.computeIfAbsent(data.getEventId(), k -> new ConcurrentHashMap<>()).put(data.getPlayerUUID(), data);
            }
            for (DepositEventModel info : eventInfoDao.queryForAll()) {
                eventInfo.put(info.getEventId(), info);
            }
        } catch (SQLException e) {
            OMCLogger.error("Failed to load deposit data from database", e);
        }
    }

    public static Optional<DepositEventModel> getInfo(DepositEvent event) {
        return Optional.ofNullable(eventInfo.get(event.getId()));
    }

    public static boolean isActive(DepositEvent event) {
        DepositEventModel info = eventInfo.get(event.getId());
        if (info == null) return false;
        return info.isActive();
    }

    public static Optional<String> getTimeLeft(DepositEvent event) {
        DepositEventModel info = eventInfo.get(event.getId());
        if (info == null || !info.isActive()) return Optional.empty();
        return Optional.of(DateUtils.convertSecondToTime(DateUtils.getDelayBetweenNow(info.getEndDate())));
    }

    private static void syncWithEventDates(DepositEvent event) {
        LocalDateTime start = event.getStartDate();
        LocalDateTime end = event.getEndDate();
        DepositEventModel info = eventInfo.get(event.getId());

        if (info == null) {
            if (!end.isAfter(DateUtils.getLocalDateTime())) return;
            info = new DepositEventModel(event.getId(), start, end);
            eventInfo.put(event.getId(), info);
            saveInfo(info);
        } else if (!info.isEnded() && (!info.getStartDate().equals(start) || !info.getEndDate().equals(end))) {
            info.setStartAt(start);
            info.setEndAt(end);

            if (info.isEnded() && end.isAfter(DateUtils.getLocalDateTime()))
                info.reopen();
            saveInfo(info);
        }
    }

    public static void start(DepositEvent event, Duration duration) {
        LocalDateTime now = DateUtils.getLocalDateTime();
        DepositEventModel info = new DepositEventModel(event.getId(), now, now.plus(duration));
        eventInfo.put(event.getId(), info);
        saveInfo(info);
        scheduleEnd(event);
    }

    public static void setEnabled(DepositEvent event, boolean enabled) {
        DepositEventModel info = eventInfo.get(event.getId());
        if (info == null) return;
        info.setEnabled(enabled);
        saveInfo(info);
    }

    private static void scheduleEnd(DepositEvent event) {
        cancelEndTask(event);

        DepositEventModel info = eventInfo.get(event.getId());
        if (info == null || info.isEnded()) return;

        if (info.shouldEnd(DateUtils.getLocalDateTime())) {
            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> end(event));
            return;
        }

        endTasks.put(event.getId(), OMCScheduler.runAt(info.getEndDate(), () -> end(event)));
    }

    private static void cancelEndTask(DepositEvent event) {
        OMCTask task = endTasks.remove(event.getId());
        if (task != null) task.cancel();
    }

    private static void saveInfo(DepositEventModel info) {
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            try {
                eventInfoDao.createOrUpdate(info);
            } catch (SQLException e) {
                OMCLogger.error("Failed to save deposit event info {}", info.getEventId(), e);
            }
        });
    }

    private static Map<UUID, DepositDataModel> dataOf(DepositEvent event) {
        return depositData.computeIfAbsent(event.getId(), k -> new ConcurrentHashMap<>());
    }

    public static int getCount(DepositEvent event, UUID player) {
        DepositDataModel data = dataOf(event).get(player);
        return data == null ? 0 : data.getCount();
    }

    public static void deposit(DepositEvent event, UUID player, int amount) {
        DepositDataModel data = dataOf(event).computeIfAbsent(player, u -> new DepositDataModel(event.getId(), u));
        data.deposit(amount);
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> save(data));
    }

    private static void save(DepositDataModel data) {
        try {
            depositDataDao.createOrUpdate(data);
        } catch (SQLException e) {
            OMCLogger.error("Failed to save deposit data {}", data.getEventId(), e);
        }
    }

    public static void end(DepositEvent event) {
        cancelEndTask(event);

        DepositEventModel info = eventInfo.get(event.getId());
        if (info != null && info.isEnded()) return;

        List<DepositDataModel> top = dataOf(event).values().stream()
                .filter(d -> d.getCount() > 0)
                .sorted(Comparator.comparingInt(DepositDataModel::getCount).reversed())
                .limit(event.getLeaderboardSize())
                .toList();

        Map<OfflinePlayer, ItemStack[]> mail = new HashMap<>();
        int rank = 1;
        for (DepositDataModel d : top) {
            OMCOfflinePlayer player = OMCOfflinePlayer.of(d.getPlayerUUID());
            DepositReward reward = event.getReward(rank++, player);
            if (reward == null) continue;

            if (reward.money() > 0) player.economy().addBalance(reward.money());
            if (!reward.items().isEmpty()) mail.put(player, reward.items().toArray(new ItemStack[0]));
        }

        if (!mail.isEmpty()) MailboxManager.sendItemsToAOfflinePlayerBatch(mail);
        removeNpc(event);

        if (info != null) {
            info.markEnded();
            saveInfo(info);
        }

        MessagesManager.broadcastMessage(
                TranslationManager.translation(event.key("event.end.broadcast.line1")).color(TextColor.color(event.getMainColor()))
                        .append(TranslationManager.translation(event.key("event.end.broadcast.line2")).color(TextColor.color(event.getMainColor())))
                        .append(TranslationManager.translation(event.key("event.end.broadcast.line3")).color(TextColor.color(event.getMainColor()))),
                event.getPrefix(), MessageType.NONE
        );
    }

    public static void reset(DepositEvent event) {
        try {
            cancelEndTask(event);

            for (DepositDataModel d : depositDataDao.queryForEq("event_id", event.getId()))
                depositDataDao.delete(d);
            dataOf(event).clear();

            eventInfoDao.deleteById(event.getId());
            eventInfo.remove(event.getId());

            removeNpc(event);
        } catch (SQLException e) {
            OMCLogger.error("failed to reset deposit data for {}", event.getId(), e);
        }
    }

    public static void removeNpc(DepositEvent event) {
        NpcManager npcManager = FancyNpcsPlugin.get().getNpcManager();
        if (npcManager == null) return;
        Npc npc = npcManager.getNpc(event.getNpcId());
        if (npc == null) return;
        npc.removeForAll();
        npcManager.removeNpc(npc);
    }

    public static List<DepositDataModel> getTop(DepositEvent event, int limit) {
        return dataOf(event).values().stream()
                .filter(d -> d.getCount() > 0)
                .sorted(Comparator.comparingDouble(DepositDataModel::getCount).reversed())
                .limit(limit)
                .toList();
    }
}