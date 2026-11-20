package fr.openmc.core.features.events.contents.christmas;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.bootstrap.listeners.ListenerFactory;
import fr.openmc.core.features.events.contents.christmas.mecanism.dimension.ChristmasDimensionManager;
import fr.openmc.core.features.events.contents.christmas.mecanism.dimension.listeners.PlayerCommandListener;
import fr.openmc.core.features.events.contents.christmas.mecanism.rift.RiftManager;

import java.util.Set;

public class ChristmasManager extends Feature implements HasListeners {
    @Override
    public void init() {
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
                PlayerCommandListener::new
        );
    }
}
