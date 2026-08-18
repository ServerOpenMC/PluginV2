package fr.openmc.core.lifecycle.interfaces;

import fr.openmc.core.lifecycle.listeners.ListenerFactory;

import java.util.Set;

/**
 * Interface permettant aux classes d'enregistrer une liste de Listeners étant lié a la class
 * (le systeme de chargement de cette classe doit charger les listeners)
 */
public interface HasListeners {
    /**
     * Listeners à initialiser
     * () -> : nécessaire si y'a un package d'api externe (ex com.comphenix.protocol)
     *
     */
    Set<ListenerFactory> getListeners();
}
