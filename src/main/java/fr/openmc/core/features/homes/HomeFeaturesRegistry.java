package fr.openmc.core.features.homes;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.homes.world.DisabledWorldHome;
import fr.openmc.core.lifecycle.registries.KeyedRegistry;
import fr.openmc.core.lifecycle.registries.SubRegistry;
import fr.openmc.core.registry.features.Feature;

public class HomeFeaturesRegistry extends SubRegistry<String, Feature> {

    public final HomeUpgradeManager HOME_UPGRADE = register(new HomeUpgradeManager());
    public final DisabledWorldHome DISABLED_WORLD_HOME = register(new DisabledWorldHome());

    @Override
    public KeyedRegistry<String, ? super Feature> getParentRegistry() {
        return OMCRegistry.FEATURES;
    }
}
