package fr.openmc.core.features.corpse;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.annotations.Credit;
import fr.openmc.core.bootstrap.features.types.HasDatabase;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.corpse.model.DBCorpse;
import fr.openmc.core.features.corpse.npc.CorpseNPCManager;
import fr.openmc.core.features.mailboxes.MailboxManager;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.*;

@Credit(developers = {"Nocolm"})
public class CorpseManager extends Feature implements HasDatabase, HasListeners {

    @Getter
    private static Map<UUID, DBCorpse> corpsesDB;

    private static Dao<DBCorpse, String> corpsesDao;

    @Override
    public void init() {
        CorpseNPCManager.init();
        corpsesDB = loadAllCorpses();
    }

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, DBCorpse.class);
        corpsesDao = DaoManager.createDao(connectionSource, DBCorpse.class);
    }

    @Override
    protected void save() {
        saveAllCorpses();
    }

    private static void saveAllCorpses() {
        try {
            corpsesDao.callBatchTasks(() -> {
                for (DBCorpse player : corpsesDB.values()) {
                    corpsesDao.createOrUpdate(player);
                }
                return null;
            });
        } catch (Exception e) {
            OMCLogger.error("Impossible de sauvegarder les corpses pendant l'arret.", e);
        }
    }

    public static Map<UUID, DBCorpse> loadAllCorpses() {
        Map<UUID, DBCorpse> corpses = new HashMap<>();

        try {
            List<DBCorpse> dbCorpses = corpsesDao.queryForAll();

            for (DBCorpse corpse : dbCorpses) {
                corpses.put(corpse.getPlayerUUID(), corpse);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return corpses;
    }

    public static boolean createCorpse(Player player, boolean killByPlayer) {

        if (hasCorpseDB(player.getUniqueId())) return false;

        ItemStack[] contents = player.getInventory().getContents().clone();
        float percentage = player.getExp();
        // keep 40% of the current experience of the player
        int exp = (int) ((getLevelToExp(player.getLevel()) + getExpToLevelUp(player.getLevel()) * percentage) * 0.4);

        corpsesDB.put(player.getUniqueId(), new DBCorpse(
                player.getUniqueId(), contents, player.getLocation(), exp, killByPlayer
        ));

        return CorpseNPCManager.createNPCS(
                player,
                player.getLocation(),
                player.getEquipment().getHelmet(),
                player.getEquipment().getChestplate(),
                player.getEquipment().getLeggings(),
                player.getEquipment().getBoots(),
                killByPlayer
        );
    }

    public static void deleteCorpse(UUID ownerUUID, FoundTypes found) {
        DBCorpse dbCorpse = corpsesDB.remove(ownerUUID);

        try {
            corpsesDao.delete(dbCorpse);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        CorpseNPCManager.removeNPCS(ownerUUID);

        OfflinePlayer offlinePlayer = CacheOfflinePlayer.getOfflinePlayer(ownerUUID);

        switch (found) {
            case FOUND -> {
                DynamicCooldownManager.clear(ownerUUID, CorpseNPCManager.COOLDOWN_GROUP, false);
                MessagesManager.sendMessage(offlinePlayer, TranslationManager.translation("feature.corpse.messages.found"),
                        Prefix.OPENMC, MessageType.WARNING, true);
            }

            case NOT_FOUND -> MessagesManager.sendMessage(offlinePlayer, TranslationManager.translation("feature.corpse.messages.not_found"),
                    Prefix.OPENMC, MessageType.WARNING, true);

            case STRIP -> DynamicCooldownManager.clear(ownerUUID, CorpseNPCManager.COOLDOWN_GROUP, false);
        }
    }

    public static void sendMailItems(Player player, OfflinePlayer receiver, ItemStack[] items) {
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            if (!MailboxManager.sendItems(player, receiver, items))
                MailboxManager.givePlayerItems(player, items);
        });
    }

    private static int getExpToLevelUp(int level){
        if(level <= 15){
            return 2*level+7;
        } else if(level <= 30){
            return 5*level-38;
        } else {
            return 9*level-158;
        }
    }

    private static int getLevelToExp(int level) {
        if(level <= 16){
            return (int) (Math.pow(level,2) + 6*level);
        } else if(level <= 31){
            return (int) (2.5*Math.pow(level,2) - 40.5*level + 360.0);
        } else {
            return (int) (4.5*Math.pow(level,2) - 162.5*level + 2220.0);
        }
    }

    public static boolean hasCorpseDB(UUID playerUUID) {
        if (corpsesDB == null) return false;
        return corpsesDB.containsKey(playerUUID);
    }

    @Override
    public Set<Listener> getListeners() {
        return Set.of(
                new CorpseListener()
        );
    }
}
