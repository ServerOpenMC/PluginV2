package fr.openmc.core.features.city.sub.milestone.requirements;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.milestone.CityLevels;
import fr.openmc.core.features.city.sub.milestone.EventCityRequirement;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.function.TriFunction;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * Classe modèle représentant une condition événementielle pour une ville.
 * Cette classe permet de définir de manière générique un prérequis basé sur un événement.
 */
public class EventTemplateRequirement implements EventCityRequirement {

    /**
     * Prédicat pour déterminer si la condition est remplie pour une ville donnée et un scope.
     */
    private final BiPredicate<City, String> eqBool;

    /**
     * Fonction retournant l'icône associée à la condition pour une ville donnée.
     */
    private final Function<City, ItemStack> item;

    /**
     * Fonction retournant le composant texte décrivant la condition.
     */
    private final TriFunction<City, CityLevels, String, Component> name;

    /**
     * Scope permettant d'identifier la condition de façon unique.
     */
    private final String scope;

    /**
     * Classe de l'événement à surveiller.
     */
    private final Class<? extends Event> eventClass;

    /**
     * Action à exécuter lors du déclenchement de l'événement.
     */
    private final BiConsumer<? super Event, String> onTrigger;

    /**
     * Constructeur permettant d'initialiser le prérequis événementiel.
     *
     * @param isDone     le prédicat permettant de vérifier si la condition est remplie pour une ville donnée
     * @param item       la fonction retournant l'icône associée à la condition
     * @param name       la fonction retournant le nom (composant texte) de la condition
     * @param scope      le scope identifiant cette condition
     * @param eventClass la classe de l'événement à surveiller
     * @param onTrigger  l'action à exécuter lors du déclenchement de l'événement
     */
    public EventTemplateRequirement(
            BiPredicate<City, String> isDone,
            Function<City, ItemStack> item,
            TriFunction<City, CityLevels, String, Component> name,
            String scope,
            Class<? extends Event> eventClass,
            BiConsumer<? super Event, String> onTrigger
    ) {
        this.eqBool = isDone;
        this.item = item;
        this.name = name;
        this.scope = scope;
        this.eventClass = eventClass;
        this.onTrigger = onTrigger;
    }

    /**
     * Vérifie si la condition est remplie pour la ville donnée.
     *
     * @param city la ville concernée
     * @return true si la condition est remplie, false sinon.
     */
    @Override
    public boolean isPredicateDone(City city) {
        return eqBool.test(city, getScope());
    }

    /**
     * Renvoie le scope identifiant de cette condition.
     *
     * @return le scope sous forme d'une chaîne de caractères.
     */
    @Override
    public String getScope() {
        return scope;
    }

    /**
     * Renvoie l'icône associée à la condition pour une ville donnée.
     *
     * @param city la ville concernée
     * @return l'objet ItemStack représentant l'icône.
     */
    @Override
    public ItemStack getIcon(City city) {
        return item.apply(city);
    }

    /**
     * Renvoie le composant texte décrivant la condition pour une ville et un niveau donnés.
     *
     * @param city  la ville concernée
     * @param level le niveau de la ville
     * @return un composant texte décrivant la condition.
     */
    @Override
    public Component getName(City city, CityLevels level) {
        return name.apply(city, level, getScope());
    }

    /**
     * Renvoie la description de la condition.
     *
     * @return actuellement null.
     */
    @Override
    public Component getDescription() {
        return null;
    }

    /**
     * Méthode qui gère l'événement. Si l'événement correspond à la classe attendue,
     * l'action onTrigger est exécutée avec le scope associé.
     *
     * @param event l'événement déclencheur
     */
    @EventHandler
    public void onEvent(Event event) {
        if (!eventClass.isInstance(event)) {
            return;
        }
        if (onTrigger != null) {
            onTrigger.accept(event, getScope());
        }
    }
}
