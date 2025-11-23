package fr.openmc.core.features.dream.mecanism.singularity;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class SingularityManager {
    public static final HashMap<UUID, SingularityContents> singularityContents = new HashMap<>();

    private static Dao<SingularityContents, String> singularityContentsDao;

    public static void init() {
        loadAllSingularityContentsData();
    }

    public static void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, SingularityContents.class);
        singularityContentsDao = DaoManager.createDao(connectionSource, SingularityContents.class);
    }

    public static void disable() {
        SingularityManager.saveAllSingularityContentsData();
    }

    private static void loadAllSingularityContentsData() {
        try {
            singularityContents.clear();
            singularityContentsDao.queryForAll().forEach(singularityContent ->
                    singularityContents.put(singularityContent.getPlayerUUID(), singularityContent)
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveAllSingularityContentsData() {
        try {
            for (SingularityContents contents : singularityContents.values()) {
                singularityContentsDao.createOrUpdate(contents);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addSingularityContents(Player player, ItemStack[] items) {
        if (singularityContents.containsKey(player.getUniqueId())) return;

        singularityContents.put(player.getUniqueId(), new SingularityContents(player.getUniqueId(), items));
    }

    public static SingularityContents getSingularityContents(Player player) {
        if (!singularityContents.containsKey(player.getUniqueId())) return null;

        return singularityContents.get(player.getUniqueId());
    }
}
