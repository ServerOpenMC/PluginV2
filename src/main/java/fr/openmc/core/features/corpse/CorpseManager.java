package fr.openmc.core.features.corpse;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasDatabase;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.corpse.model.DBCorpse;
import fr.openmc.core.features.corpse.npc.CorpseNPCManager;
import fr.openmc.core.features.mailboxes.MailboxManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.*;

public class CorpseManager extends Feature implements HasDatabase, HasListeners {

    @Getter
    private static Map<UUID, DBCorpse> corpsesDB;

    private static Dao<DBCorpse, String> corpsesDao;

    public CorpseManager() {
        new CorpseNPCManager();
    }

    @Override
    public void init() {
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
        ItemStack[] contents = player.getInventory().getContents().clone();
        float exp = player.getExp();
        int level = player.getLevel();

        System.out.println("total : "+ exp);

        if (hasCorpseDB(player.getUniqueId())) return false;

        corpsesDB.put(player.getUniqueId(), new DBCorpse(
                player.getUniqueId(), contents, player.getLocation(), exp, level, killByPlayer
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

    public static void deleteCorpse(UUID ownerUUID, boolean found) {
        DBCorpse dbCorpse = corpsesDB.remove(ownerUUID);

        try {
            corpsesDao.delete(dbCorpse);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        CorpseNPCManager.removeNPCS(ownerUUID);
        if (found) {
            DynamicCooldownManager.clear(ownerUUID, CorpseNPCManager.COOLDOWN_GROUP, false);
            // TODO message
        } else {
            //TODO message
        }
    }

    public static void sendMailItems(Player player, OfflinePlayer receiver, ItemStack[] items) {
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
            if (!MailboxManager.sendItems(player, receiver, items))
                MailboxManager.givePlayerItems(player, items);
        });
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
