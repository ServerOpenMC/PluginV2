package fr.openmc.core.bootstrap.hooks;

import fr.openmc.core.OMCPlugin;

/**
 * Interface permettant aux hooks d'avoir une classe main reliant au plugin
 */
public interface ApiHook<T> {
    Class<T> apiClass();

    default T api() {
        return OMCPlugin.getInstance()
                .getServer()
                .getServicesManager()
                .load(apiClass());
    }
}

