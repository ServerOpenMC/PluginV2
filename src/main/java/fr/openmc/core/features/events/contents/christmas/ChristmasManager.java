package fr.openmc.core.features.events.contents.christmas;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.features.events.contents.christmas.mecanism.rift.RiftManager;

public class ChristmasManager extends Feature {
    @Override
    public void init() {
        ChristmasDimensionManager.init();
        OMCPlugin.registerFeature(new RiftManager());
    }

    @Override
    public void save() {
        ChristmasDimensionManager.save();
    }
}
