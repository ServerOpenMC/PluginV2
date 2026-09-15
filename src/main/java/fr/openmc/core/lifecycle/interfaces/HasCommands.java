package fr.openmc.core.lifecycle.interfaces;

import java.util.Set;

/**
 * Interface permettant aux features d'enregistrer une liste de Commandes étant lié au features
 */
public interface HasCommands {
    /**
     * Commandes à initialiser
     */
    Set<Object> getCommands();
}
