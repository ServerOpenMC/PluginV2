package fr.openmc.core;

import fr.openmc.api.cooldown.CooldownInterceptor;
import fr.openmc.core.commands.debug.ChronometerCommand;
import fr.openmc.core.commands.debug.CustomItemCommand;
import fr.openmc.core.commands.fun.Diceroll;
import fr.openmc.core.commands.fun.Playtime;
import fr.openmc.core.commands.utils.RTPCommands;
import fr.openmc.core.commands.utils.Restart;
import fr.openmc.core.commands.utils.Socials;
import fr.openmc.core.features.credits.CreditsCommand;
import lombok.Getter;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;

/**
 * Enregistrement des commandes globales du plugin.
 * Initialise Lamp et ajoute l'ensemble des commandes exposees.
 */
public class CommandsManager {
    @Getter
    static Lamp handler;

    /**
     * Initialise le handler de commandes et enregistre les commandes.
     */
    public static void init() {
        handler = BukkitLamp.builder(OMCPlugin.getInstance())
                .commandCondition(new CooldownInterceptor())
                .build();

        registerCommands();
    }

    /**
     * Enregistre toutes les commandes du plugin sur le handler.
     */
    private static void registerCommands() {
        handler.register(
                new Socials(),
                new RTPCommands(),
                new Playtime(),
                new Diceroll(),
                new ChronometerCommand(),
                new Restart(),
                new CreditsCommand(),
                new CustomItemCommand()
        );
    }
}
