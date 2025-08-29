package fr.openmc.core.features.city.sub.milestone.requirements;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.milestone.CityLevels;
import fr.openmc.core.features.city.sub.milestone.CityRequirement;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Représente un prérequis basé sur un modèle générique.
 * Permet d'utiliser des fonctions lambda pour définir la condition, l'icône et le nom.
 */
public class TemplateRequirement implements CityRequirement {

    /**
     * Prédicat qui détermine si la condition est satisfaite pour la ville.
     */
    private final Predicate<City> eqBool;

    /**
     * Fonction qui renvoie l'icône associée à la condition.
     */
    private final Function<City, ItemStack> item;

    /**
     * Fonction qui renvoie le nom sous forme d'un composant texte en fonction de la ville et de son niveau.
     */
    private final BiFunction<City, CityLevels, Component> name;

    /**
     * Initialise le prérequis modèle avec les fonctions pour la condition, l'icône et le nom.
     *
     * @param isDone le prédicat de vérification de la condition
     * @param item   la fonction renvoyant l'icône de la condition
     * @param name   la fonction renvoyant le nom de la condition
     */
    public TemplateRequirement(Predicate<City> isDone, Function<City, ItemStack> item, BiFunction<City, CityLevels, Component> name) {
        this.eqBool = isDone;
        this.item = item;
        this.name = name;
    }

    /**
     * Vérifie si la condition est satisfaite pour la ville.
     *
     * @param city la ville concernée
     * @return true si la condition est remplie, false sinon
     */
    @Override
    public boolean isPredicateDone(City city) {
        return eqBool.test(city);
    }

    /**
     * Retourne le scope associé à la condition.
     *
     * @return null car non défini pour ce modèle
     */
    @Override
    public String getScope() {
        return null;
    }

    /**
     * Renvoie l'icône associée à la condition.
     *
     * @param city la ville concernée
     * @return l'icône sous forme d'ItemStack
     */
    @Override
    public ItemStack getIcon(City city) {
        return item.apply(city);
    }

    /**
     * Renvoie le nom de la condition sous forme d'un composant texte en fonction de la ville et de son niveau.
     *
     * @param city  la ville concernée
     * @param level le niveau de la ville
     * @return un composant texte décrivant la condition
     */
    @Override
    public Component getName(City city, CityLevels level) {
        return name.apply(city, level);
    }

    /**
     * Renvoie la description de la condition.
     *
     * @return null, aucune description définie
     */
    @Override
    public Component getDescription() {
        return null;
    }
}