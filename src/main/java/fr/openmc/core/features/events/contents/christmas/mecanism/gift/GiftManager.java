package fr.openmc.core.features.events.contents.christmas.mecanism.gift;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasDatabase;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.events.contents.christmas.mecanism.gift.models.DBGift;
import fr.openmc.core.features.events.contents.christmas.mecanism.gift.models.GiftType;
import fr.openmc.core.utils.world.block.BlockPos;
import org.bukkit.block.Block;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class GiftManager extends Feature implements HasDatabase {
    private static final Map<BlockPos, GiftType> gifts = new HashMap<>();

    private static Dao<DBGift, String> giftDao;

    @Override
    public void init() {
        loadGiftData();
    }

    @Override
    public void save() {
        saveAllGiftData();
    }


    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, DBGift.class);
        giftDao = DaoManager.createDao(connectionSource, DBGift.class);
    }

    private static void loadGiftData() {
        try {
            gifts.clear();
            giftDao.queryForAll().forEach(dbGift -> {
                gifts.put(dbGift.getBlockPos(), dbGift.getType());
                try {
                    giftDao.delete(dbGift);
                } catch (SQLException e) {
                    OMCLogger.error("Cannot load gift data", e);
                }
            });
        } catch (SQLException e) {
            OMCLogger.error("Cannot load gift data", e);
        }
    }

    public static void saveAllGiftData() {
        gifts.forEach((blockPos, giftType) -> {
            try {
                giftDao.createOrUpdate(new DBGift(giftType, blockPos));
            } catch (SQLException e) {
                OMCLogger.error("Cannot save gift data}", e);
            }
        });
    }

    public static void placeGiftInBlock(Block block) {

    }
}
