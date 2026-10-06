package fr.openmc.core.features.singularity.sub.wormhole;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasDatabase;
import fr.openmc.core.features.singularity.contents.mobs.WormHole;
import fr.openmc.core.features.singularity.sub.world.SingularityWorldManager;
import fr.openmc.core.features.singularity.sub.wormhole.models.WormHoleDB;
import fr.openmc.core.features.singularity.sub.wormhole.models.WormHoleStage;
import fr.openmc.core.registry.mobs.CustomMob;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class WormHoleManager extends Feature implements HasDatabase {
    private static final NamespacedKey OWNER_WORM_UUID = new NamespacedKey(OMCPlugin.getInstance(), "worm_owner_uuid");
    private static final NamespacedKey STAGE_KEY = new NamespacedKey(OMCPlugin.getInstance(), "wormhole_stage");

    private static final HashMap<UUID, Set<WormHoleDB>> wormHoleData = new HashMap<>();
    private static Dao<WormHoleDB, String> wormLocationDao;

    @Override
    public void init() throws SQLException {
        loadAndSpawnEntities();
    }

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, WormHoleDB.class);
        wormLocationDao = DaoManager.createDao(connectionSource, WormHoleDB.class);
    }

    @Override
    public void save() {
        cleanAndSaveEntities();
    }

    public static void createAndSpawn(UUID ownerUUID, Location location, WormHoleStage stage) {
        ItemDisplay spawnedWorm = (ItemDisplay) OMCRegistry.CUSTOM_MOBS.WORM_HOLE.getMob().spawn(location, entity -> {
            entity.getPersistentDataContainer().set(OWNER_WORM_UUID, PersistentDataType.STRING, ownerUUID.toString());
        });

        setStage(spawnedWorm, stage);

        wormHoleData.compute(ownerUUID, (_, wormHoleDBS) -> {
            if (wormHoleDBS == null) wormHoleDBS = new HashSet<>();
            wormHoleDBS.add(new WormHoleDB(ownerUUID, location, stage));
            return wormHoleDBS;
        });
    }

    public static UUID getOwnerUUIDIfWormHole(ItemDisplay itemDisplay) {
        String owner = itemDisplay.getPersistentDataContainer().get(OWNER_WORM_UUID, PersistentDataType.STRING);
        return owner == null ? null : UUID.fromString(owner);
    }

    public static WormHoleStage getStage(ItemDisplay display) {
        return WormHoleStage.values()[display.getPersistentDataContainer().getOrDefault(STAGE_KEY, PersistentDataType.INTEGER, 1) - 1];
    }

    public static void setStage(ItemDisplay display, WormHoleStage stage) {
        display.getPersistentDataContainer().set(STAGE_KEY, PersistentDataType.INTEGER, stage.getStage());
    }

    private void loadAndSpawnEntities() throws SQLException {
        wormLocationDao.queryForAll().forEach(wormHoleDB -> {
            createAndSpawn(wormHoleDB.getOwnerUUID(), wormHoleDB.getLocation(), WormHoleStage.getStage(wormHoleDB.getStageInt()));
        });
    }

    private void cleanAndSaveEntities() {
        for (Entity entity : SingularityWorldManager.getWorldTemplate().getWorld().getEntities()) {
            CustomMob<?> customMob = OMCRegistry.CUSTOM_MOBS.getMob(entity);
            if (customMob == null) continue;
            if (!(customMob instanceof WormHole)) continue;
            if (!(entity instanceof ItemDisplay display)) continue;

            String owner = entity.getPersistentDataContainer().get(OWNER_WORM_UUID, PersistentDataType.STRING);
            if (owner == null) continue;
            WormHoleStage stage = getStage(display);
            UUID ownerUUID = UUID.fromString(owner);

            entity.remove();

            try {
                wormLocationDao.create(new WormHoleDB(ownerUUID, entity.getLocation(), stage));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
