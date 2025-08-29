package fr.openmc.core.features.city.sub.milestone;

import net.kyori.adventure.text.Component;

/**
 * Représente la récompense associée à une ville.
 * Cette interface définit une méthode pour obtenir le nom de la récompense.
 */
public interface CityRewards {

    /**
     * Retourne le nom de la récompense sous forme de composant texte.
     *
     * @return le nom de la récompense
     */
    Component getName();
}
