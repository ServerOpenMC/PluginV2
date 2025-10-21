package fr.openmc.core.features.city.sub.milestone;

import fr.openmc.core.features.city.City;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

/**
 * Représente une exigence pour une ville.
 */
public interface CityRequirement {

    /**
     * Vérifie si le prédicat de la condition est satisfait pour la ville donnée.
     *
     * @param city la ville concernée
     * @return {@code true} si la condition est satisfaite, {@code false} sinon
     */
    boolean isPredicateDone(City city);

    /**
     * Vérifie si la condition est remplie pour la ville en fonction de son niveau.
     * Si le niveau de la ville est supérieur au niveau attendu, la condition est considérée comme satisfaite.
     *
     * @param city  la ville concernée
     * @param level le niveau de la ville
     * @return {@code true} si la condition est remplie, {@code false} sinon
     */
    default boolean isDone(City city, CityLevels level) {
        if (city.getLevel() > level.ordinal()) {
            return true;
        }
        return isPredicateDone(city);
    }

    /**
     * Retourne le scope associé à cette exigence.
     *
     * @return une chaîne représentant le scope
     */
    String getScope();

    /**
     * Retourne l'icône associée à cette exigence pour la ville donnée.
     *
     * @param city la ville concernée
     * @return l'icône sous forme d'ItemStack
     */
    ItemStack getIcon(City city);

    /**
     * Retourne le nom de l'exigence sous forme de composant texte.
     *
     * @param city la ville concernée
     * @param level le niveau de la ville
     * @return le composant texte représentant le nom de l'exigence
     */
    Component getName(City city, CityLevels level);

    /**
     * Retourne la description de l'exigence sous forme de composant texte.
     *
     * @return le composant texte décrivant l'exigence
     */
    Component getDescription();
}
