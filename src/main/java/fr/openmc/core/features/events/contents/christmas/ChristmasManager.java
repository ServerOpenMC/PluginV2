package fr.openmc.core.features.events.contents.christmas;

import com.j256.ormlite.support.ConnectionSource;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.HasDatabase;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.bootstrap.features.types.LoadAfterItemsAdder;
import fr.openmc.core.bootstrap.listeners.ListenerFactory;
import fr.openmc.core.features.events.contents.christmas.mecanism.dimension.ChristmasDimensionManager;
import fr.openmc.core.features.events.contents.christmas.mecanism.dimension.listeners.PlayerCommandListener;
import fr.openmc.core.features.events.contents.christmas.mecanism.dimension.listeners.ReplaceBlockListener;
import fr.openmc.core.features.events.contents.christmas.mecanism.gift.GiftManager;
import fr.openmc.core.features.events.contents.christmas.mecanism.gift.commands.AdminGiftCommands;
import fr.openmc.core.features.events.contents.christmas.mecanism.gift.listeners.GiftBreakListener;
import fr.openmc.core.features.events.contents.christmas.mecanism.rift.RiftManager;

import java.sql.SQLException;
import java.util.Set;

public class ChristmasManager extends Feature implements HasListeners, LoadAfterItemsAdder, HasDatabase, HasCommands {
    @Override
    public void init() throws SQLException {
        ChristmasDimensionManager.init();
        OMCPlugin.registerFeature(new RiftManager());
    }

    @Override
    public void save() {
        ChristmasDimensionManager.save();
    }

    @Override
    public Set<ListenerFactory> getListeners() {
        return Set.of(
                PlayerCommandListener::new,
                ReplaceBlockListener::new,
                GiftBreakListener::new
        );
    }

    @Override
    public void initDB(ConnectionSource connectionSource) throws SQLException {
        GiftManager.initDB(connectionSource);
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new AdminGiftCommands()
        );
    }
}
