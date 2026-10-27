package fr.openmc.core.features.events.contents.halloween.festival;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.omcplayer.OMCOfflinePlayer;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.*;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.bootstrap.listeners.ListenerFactory;
import fr.openmc.core.features.events.contents.halloween.festival.listeners.TerrorHouseListener;
import fr.openmc.core.features.events.contents.halloween.festival.npc.WitchNPCS;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.TerrorHouseManager;
import fr.openmc.core.features.events.contents.halloween.festival.listeners.CandyListener;
import fr.openmc.core.features.events.contents.halloween.festival.model.HalloweenDB;
import fr.openmc.core.features.events.contents.halloween.festival.shop.WitchShopManager;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.hooks.WorldEditHook;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.minecraft.core.UUIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class HalloweenFestivalManager extends Feature implements LoadAfterItemsAdder, HasDatabase, HasListeners, HasCommands {

    //TODO quand acheter truc dans shop, augmenté avancement bonbons pour débloqué les étages, quand tous les étages débloqué, faire obtenir des récompenses a tous les joueurs ayant contribué

    @Getter
    private static HalloweenDB halloweenDB;

    private static Dao<HalloweenDB, String> halloweenDao;

    @Getter
    private static Set<UUID> participant;

    private static final String EVENT_GROUP = "halloween_festival_event";
    private static final UUID EVENT_UUID = UUIDUtil.createOfflinePlayerUUID(EVENT_GROUP);

    @Override
    public void init() {
        halloweenDB = loadEvent();
        participant = halloweenDB.getParticipants();
        WitchShopManager.init();
        TerrorHouseManager.init(
                halloweenDB.getGlobalAdvancement(),
                halloweenDB.getFloorAdvancement()
        );
    }

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, HalloweenDB.class);
        halloweenDao = DaoManager.createDao(connectionSource, HalloweenDB.class);
    }

    @Override
    protected void save() {
        saveEvent();
    }

    public static HalloweenDB loadEvent() {
        try {
            HalloweenDB halloweenDB = halloweenDao.queryForFirst();

            if (halloweenDB == null) {
                halloweenDB = new HalloweenDB(0, 0, false);
                halloweenDao.create(halloweenDB);
            }

            return halloweenDB;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void saveEvent() {
        try {
            halloweenDB.setFloorAdvancement(TerrorHouseManager.floorAdvancement);
            halloweenDB.setGlobalAdvancement(TerrorHouseManager.globalAdvancement);
            halloweenDB.setParticipants(participant);

            halloweenDao.createOrUpdate(halloweenDB);
        } catch (Exception e) {
            OMCLogger.error(
                    "Impossible de sauvegarder l'event d'halloween pendant l'arrêt.",
                    e
            );
        }
    }

    public static void startEvent(long duration) {
        if (hasStart())
            return;

        DynamicCooldownManager.use(EVENT_UUID, EVENT_GROUP, duration);
    }

    public static void stopEvent() {
        if (!DynamicCooldownManager.isReady(EVENT_UUID, EVENT_GROUP)) {
            DynamicCooldownManager.clear(EVENT_UUID, EVENT_GROUP, false);
        }
    }

    public static boolean hasStart() {
        return DynamicCooldownManager.getCooldowns(EVENT_UUID) != null;
    }

    public static long getRemainingTime() {
        if (!hasStart()) return 0;
        return DynamicCooldownManager.getRemaining(EVENT_UUID, EVENT_GROUP);
    }

    public static boolean hasParticipant(UUID uuid) {
        return participant.contains(uuid);
    }

    public static boolean isActive() {
        return halloweenDB.isActive();
    }

    public static void addParticipant(UUID uuid) {
        participant.add(uuid);
    }

    public static void broadcast(Component text) {
        OMCPlugin.getInstance().getServer().getOnlinePlayers().forEach(
                player -> MessagesManager.sendMessage(player, text, Prefix.HALLOWEEN, MessageType.INFO, true)
        );
    }

    public static void sendMailItems(OMCPlayer player, OMCOfflinePlayer receiver, ItemStack[] items) {
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            if (!MailboxManager.sendItems(player, receiver, items))
                MailboxManager.givePlayerItems(player, items);
        });
    }

    public static void placeWitchStore(Location location) {
        try {
            OMCRegistry.CUSTOM_STRUCTURES.WITCH_SHOP.place(EVENT_UUID, location);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void removeWitchStore() {
        try {
            OMCRegistry.CUSTOM_STRUCTURES.WITCH_SHOP.remove(EVENT_UUID);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<ListenerFactory> getListeners() {
        return Set.of(
                CandyListener::new,
                TerrorHouseListener::new,
                WitchNPCS::new
        );
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(

        );
    }
}
