package fr.openmc.core.features.singularity;

import com.j256.ormlite.support.ConnectionSource;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasDatabase;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.bootstrap.features.types.LoadAfterItemsAdder;
import fr.openmc.core.bootstrap.listeners.ListenerFactory;
import fr.openmc.core.features.singularity.sub.wormhole.WormHoleManager;
import fr.openmc.core.features.singularity.sub.wormhole.listeners.SingularityThrowListener;

import java.sql.SQLException;
import java.util.Set;

public class SingularityManager extends Feature implements HasDatabase, HasListeners, LoadAfterItemsAdder {
    WormHoleManager wormManager;

    @Override
    public void init() {
        OMCPlugin.registerFeature(wormManager);
    }

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        // todo: laisser comme ça en attendant le registre des features plus propre
        wormManager = new WormHoleManager();
        wormManager.startDB(connectionSource);
    }

    @Override
    public Set<ListenerFactory> getListeners() {
        return Set.of(
                SingularityThrowListener::new
        );
    }
}
