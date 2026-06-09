package fr.openmc.core.features.events.contents.dailyevents.models.dailyevent;

import fr.openmc.core.features.events.models.Event;

public abstract class DailyEvent extends Event {
    /**
     * L'identifiant de l'evenement afin de le serialize dans la db
     * @return un string
     */
    public abstract String getEventId();

    /**
     * Le monde où l'evenement est actif
     * @return un monde (minecraft:the_nether, minecraft:world, omc_dream:dream, ...)
     */
    public abstract String getWorldEvent();

    /**
     * Le temps de durée de l'évenement
     * @return un entier représentant les minutes
     */
    public abstract int getDuration();

    /**
     * Les procédures à lancer au début de l'évenement
     * @return une méthode
     */
    public abstract Runnable onStart();
    /**
     * Les procédures à lancer à la fin de l'évenement
     * @return une méthode
     */
    public abstract Runnable onEnd();
}
