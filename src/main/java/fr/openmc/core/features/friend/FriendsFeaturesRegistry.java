package fr.openmc.core.features.friend;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.lifecycle.registries.KeyedRegistry;
import fr.openmc.core.lifecycle.registries.SubRegistry;
import fr.openmc.core.registry.features.Feature;

public class FriendsFeaturesRegistry extends SubRegistry<String, Feature> {
    public final FriendSQLManager FRIEND_DB = register(new FriendSQLManager());

    @Override
    public KeyedRegistry<String, ? super Feature> getParentRegistry() {
        return OMCRegistry.FEATURES;
    }
}
