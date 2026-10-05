package fr.openmc.core.features.singularity;

import com.j256.ormlite.support.ConnectionSource;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasDatabase;
import fr.openmc.core.bootstrap.features.types.LoadAfterItemsAdder;
import fr.openmc.core.features.singularity.sub.wormhole.WormManager;

import java.sql.SQLException;

public class SingularityManager extends Feature implements HasDatabase, LoadAfterItemsAdder {
    WormManager wormManager;

    @Override
    public void init() {
        OMCPlugin.registerFeature(wormManager);
    }

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        // todo: laisser comme ça en attendant le registre des features plus propre
        wormManager = new WormManager();
        wormManager.startDB(connectionSource);
    }
}
