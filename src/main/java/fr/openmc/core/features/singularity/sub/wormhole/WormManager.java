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

public class WormManager extends Feature implements HasDatabase {
    private static final NamespacedKey OWNER_WORM_UUID = new NamespacedKey(OMCPlugin.getInstance(), "worm_owner_uuid");

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

    public static void createAndSpawn(UUID ownerUUID, Location location) {
        OMCRegistry.CUSTOM_MOBS.WORM_HOLE.getMob().spawn(location, entity -> {
            entity.getPersistentDataContainer().set(OWNER_WORM_UUID, PersistentDataType.STRING, ownerUUID.toString());
        });

        wormHoleData.compute(ownerUUID, (_, wormHoleDBS) -> {
            if (wormHoleDBS == null) wormHoleDBS = new HashSet<>();
            wormHoleDBS.add(new WormHoleDB(ownerUUID, location));
            return wormHoleDBS;
        });
    }

    public static UUID getOwnerUUIDIfWormHole(ItemDisplay itemDisplay) {
        String owner = itemDisplay.getPersistentDataContainer().get(OWNER_WORM_UUID, PersistentDataType.STRING);
        return owner == null ? null : UUID.fromString(owner);
    }

    private void loadAndSpawnEntities() throws SQLException {
        wormLocationDao.queryForAll().forEach(wormHoleDB -> {
            ItemDisplay wormHole = (ItemDisplay) OMCRegistry.CUSTOM_MOBS.WORM_HOLE.getMob().spawn(wormHoleDB.getLocation());
            wormHole.getPersistentDataContainer().set(OWNER_WORM_UUID, PersistentDataType.STRING, wormHoleDB.getOwnerUUID().toString());
        });
    }

    private void cleanAndSaveEntities() {
        for (Entity entity : SingularityWorldManager.getWorldTemplate().getWorld().getEntities()) {
            CustomMob<?> customMob = OMCRegistry.CUSTOM_MOBS.getMob(entity);
            if (customMob == null) continue;
            if (!(customMob instanceof WormHole)) continue;

            String owner = entity.getPersistentDataContainer().get(OWNER_WORM_UUID, PersistentDataType.STRING);
            if (owner == null) continue;
            UUID ownerUUID = UUID.fromString(owner);

            entity.remove();

            try {
                wormLocationDao.create(new WormHoleDB(ownerUUID, entity.getLocation()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
