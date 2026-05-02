package fr.openmc.core.bootstrap.features.types;

import org.bukkit.event.Listener;

import java.util.Set;

/**
 * Interface permettant aux features d'enregistrer une liste de Listeners étant lié au features
 */
public interface HasListeners {
    /**
     * Listeners à initialiser
     */
    Set<Listener> getListeners();
}
