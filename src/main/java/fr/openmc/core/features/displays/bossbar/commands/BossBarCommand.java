package fr.openmc.core.features.displays.bossbar.commands;

import fr.openmc.core.features.displays.bossbar.BossbarManager;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.CommandPlaceholder;

/**
 * Commande de gestion rapide de la boss bar d'aide.
 */
@Command({"omcbossbar", "bb", "bossbaromc"})
public class BossBarCommand {

    /**
     * Bascule l'affichage de la boss bar d'aide pour le joueur.
     *
     * @param player Le joueur exécutant la commande
     */
    @CommandPlaceholder()
    public void mainCommand(Player player) {
        BossbarManager.toggleBossBar(player, "omc:help");
    }
}
