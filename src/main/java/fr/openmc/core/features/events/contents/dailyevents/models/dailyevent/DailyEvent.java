package fr.openmc.core.features.events.contents.dailyevents.models.dailyevent;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.events.contents.dailyevents.DailyEventsManager;
import fr.openmc.core.features.events.contents.dailyevents.tasks.EndEventTask;
import fr.openmc.core.features.events.models.Event;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.stream.Collectors;

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

    /**
     * Procédure complète de lancement de l'évenement, qui s'occupe de tout (broadcast, toast, ambiance, etc...)
     */
    public void start() {
        Collection<Player> receivers = Bukkit.getOnlinePlayers()
                .stream()
                .filter(p -> p.getWorld().getName().equals(this.getWorldEvent()))
                .collect(Collectors.toSet());

        // * Lancement de l'evenement
        this.onStart().run();

        // * Application de l'ambience
        if (this instanceof HasAmbient ambient) {
            ambient.apply(receivers);
        }

        // * Message de début
        if (this instanceof HasBroadcast broadcast) {
            broadcast.sendStartBroadcast(receivers);
        }

        // * Toast de début
        if (this instanceof HasToast toast) {
            toast.getStartToastData().send(receivers);
        }

        // * Programmation de la fin de l'evenement
        DailyEventsManager.endEventTask = new EndEventTask()
                .runTaskLater(OMCPlugin.getInstance(),
                        DailyEventsManager.outgoingEvent.getDailyEvent().getDuration() * 60L * 20L);
    }

    /**
     * Procédure complète de fin d'évenement (toast, ambient, broadcast, ...)
     */
    public void end() {
        Collection<Player> receivers = Bukkit.getOnlinePlayers()
                .stream()
                .filter(p -> p.getWorld().getName().equals(this.getWorldEvent()))
                .collect(Collectors.toSet());

        DailyEventsManager.outgoingEvent = null;
        DailyEventsManager.endEventTask = null;

        // * Arret de l'evenement
        this.onEnd().run();

        // * Suppression de la l'ambience
        if (this instanceof HasAmbient ambient) {
            ambient.reset(receivers);
        }

        // * Message de fin
        if (this instanceof HasBroadcast broadcast) {
            broadcast.sendEndBroadcast(receivers);
        }

        // * Toast de fin
        if (this instanceof HasToast toast) {
            toast.getEndToastData().send(receivers);
        }
    }
}
