package fr.openmc.core.bootstrap.hooks;

import fr.openmc.core.OMCPlugin;

/**
 * Interface permettant d'acceder a un service expose par un plugin externe.
 */
public interface ApiHook<T> {
    /**
     * Retourne la classe du service a recuperer via le ServicesManager.
     *
     * @return Classe du service exposé
     */
    Class<T> apiClass();

    /**
     * Charge le service expose par le plugin externe, ou null si indisponible.
     *
     * @return Instance du service, ou null si non disponible
     */
    default T api() {
        return OMCPlugin.getInstance()
                .getServer()
                .getServicesManager()
                .load(apiClass());
    }
}
