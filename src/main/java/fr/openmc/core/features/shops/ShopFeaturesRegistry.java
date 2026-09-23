package fr.openmc.core.features.shops;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.homes.HomeUpgradeManager;
import fr.openmc.core.features.shops.managers.PlayerShopManager;
import fr.openmc.core.features.shops.managers.ShopDatabaseManager;
import fr.openmc.core.lifecycle.registries.KeyedRegistry;
import fr.openmc.core.lifecycle.registries.SubRegistry;
import fr.openmc.core.registry.features.Feature;

public class ShopFeaturesRegistry extends SubRegistry<String, Feature> {

    public final PlayerShopManager PLAYER_SHOP = register(new PlayerShopManager());
    public final ShopDatabaseManager SHOP_DB = register(new ShopDatabaseManager());

    @Override
    public KeyedRegistry<String, ? super Feature> getParentRegistry() {
        return OMCRegistry.FEATURES;
    }
}
