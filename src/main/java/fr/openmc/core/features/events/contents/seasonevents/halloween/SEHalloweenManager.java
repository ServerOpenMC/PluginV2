package fr.openmc.core.features.events.contents.seasonevents.halloween;

import com.j256.ormlite.support.ConnectionSource;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.HasDatabase;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.bootstrap.listeners.ListenerFactory;

import java.sql.SQLException;
import java.util.Set;

public class SEHalloweenManager extends Feature implements HasDatabase, HasListeners, HasCommands {

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {

    }

    @Override
    public Set<ListenerFactory> getListeners() {
        return Set.of();
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of();
    }
}
