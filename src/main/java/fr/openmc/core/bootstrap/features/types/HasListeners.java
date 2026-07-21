package fr.openmc.core.bootstrap.features.types;

import org.bukkit.event.Listener;

import java.util.Set;

/**
 * Interface permettant aux classes d'enregistrer une liste de Listeners étant lié a la class
 * (le systeme de chargement de cette classe doit charger les listeners)
 */
public interface HasListeners {
    /**
     * Listeners à initialiser
     */
    Set<Listener> getListeners();
}
