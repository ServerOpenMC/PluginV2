package fr.openmc.core.features.events.contents.christmas.mecanism.gift;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.events.contents.christmas.mecanism.gift.models.DBGift;
import fr.openmc.core.features.events.contents.christmas.mecanism.gift.models.GiftType;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.registry.loottable.loots.IdLoot;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.world.block.BlockPos;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.sql.SQLException;
import java.util.concurrent.*;

public class GiftManager {
    private static Dao<DBGift, String> giftDao;
    private static final ExecutorService threadDb =
            Executors.newSingleThreadExecutor(r -> new Thread(r, "Gift-DB"));

    public static void save() {
        threadDb.shutdown();
        try {
            threadDb.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {}
    }

    public static void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, DBGift.class);
        giftDao = DaoManager.createDao(connectionSource, DBGift.class);
    }

    public static void addGift(GiftType type, BlockPos pos, Entity entity) {
        threadDb.execute(() -> {
            try {
                giftDao.create(new DBGift(type, pos, entity));
            } catch (SQLException e) {
                OMCLogger.error("Failed to add gift at {}", pos, e);
            }
        });
    }

    public static void removeGift(DBGift dbGift) {
        if (dbGift == null) return;

        threadDb.execute(() -> {
            try {
                giftDao.delete(dbGift);
            } catch (SQLException e) {
                OMCLogger.error("Failed to remove gift", e);
            }
        });
    }

    public static CompletableFuture<DBGift> getGift(BlockPos pos) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                QueryBuilder<DBGift, String> query = giftDao.queryBuilder();
                query.where()
                        .eq("x", pos.x()).and()
                        .eq("y", pos.y()).and()
                        .eq("z", pos.z());
                return query.queryForFirst();
            } catch (SQLException e) {
                OMCLogger.error("Failed to get gift at {}", pos, e);
                return null;
            }
        }, threadDb);
    }

    public static void placeGiftInBlock(Block block) {
        placeGiftInBlock(block, false);
    }

    public static void placeGiftInBlock(Block block, boolean bypassRandom) {
        if (!bypassRandom && ThreadLocalRandom.current().nextDouble() >= 0.1) return;

        CustomLoot loot = OMCRegistry.CUSTOM_LOOT_TABLES.CHRISTMAS_ICE.selectRandomLoot();

        if (!(loot instanceof IdLoot idLoot && idLoot.getId() != null)) return;

        Location center = block.getLocation();
        BlockPos b = BlockPos.of(block);

        switch (idLoot.getId()) {
            case "common" -> {
                Entity entity = OMCRegistry.CUSTOM_MOBS.GIFT_COMMON.spawn(center);
                addGift(GiftType.COMMON, b, entity);
            }
            case "rare" -> {
                Entity entity = OMCRegistry.CUSTOM_MOBS.GIFT_RARE.spawn(center);
                addGift(GiftType.RARE, b, entity);
            }
            case "legendary" -> {
                Entity entity = OMCRegistry.CUSTOM_MOBS.GIFT_LEGENDARY.spawn(center);
                addGift(GiftType.LEGENDARY, b, entity);
            }
            case "onirisme" -> {
                Entity entity = OMCRegistry.CUSTOM_MOBS.GIFT_ONIRISME.spawn(center);
                addGift(GiftType.ONIRISME, b, entity);
            }
            default -> {}
        }
    }

    public static void breakGift(BlockPos pos, OMCPlayer player) {
        getGift(pos).thenApply(gift -> {
            if (gift == null) return null;

            switch (gift.getType()) {
                case COMMON -> {
                    ItemUtils.giveItem(
                            player,
                            OMCRegistry.CUSTOM_ITEMS.COMMON_GIFT.getBest(),
                            1);
                }
                case RARE -> {
                    ItemUtils.giveItem(
                            player,
                            OMCRegistry.CUSTOM_ITEMS.RARE_GIFT.getBest(),
                            1);
                }
                case LEGENDARY -> {
                    ItemUtils.giveItem(
                            player,
                            OMCRegistry.CUSTOM_ITEMS.LEGENDARY_GIFT.getBest(),
                            1);
                }
                case ONIRISME -> {
                    ItemUtils.giveItem(
                            player,
                            OMCRegistry.CUSTOM_ITEMS.ONIRISIME_GIFT.getBest(),
                            1);
                }
            }

            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                gift.removeGiftEntity();
                removeGift(gift);
            });

            return gift;
        });
    }
}
