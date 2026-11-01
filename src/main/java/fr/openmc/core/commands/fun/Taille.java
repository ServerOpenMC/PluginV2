package fr.openmc.core.commands.fun;

import org.bukkit.entity.Player;
import org.bukkit.attribute.Attribute;

import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("taille")
public class Taille {

    @CommandPermission("omc.commands.taille")
    @Description("Afficher les commandes de la gestion de la taille")
    public void taille(Player player) {
        player.sendMessage("§6§lLite de Commandes pour changer sa taille");
        player.sendMessage("§e/taille taille1 - Taille 0.5");
        player.sendMessage("§e/taille taille2 - Taille 1.5");
        player.sendMessage("§e/taille normal - Taille normale");
    }

    @Subcommand("taille1")
    @CommandPermission("omc.commands.taille.taille1")
    @Description("Mettre la taille sur 0.5")
    public void taille1(Player player) {
        changerTaille(player, 0.5);
    }

    @Subcommand("taille2")
    @CommandPermission("omc.commands.taille.taille2")
    @Description("Mettre la taille sur 1.5")
    public void taille2(Player player) {
        changerTaille(player, 1.5);
    }

    @Subcommand("normal")
    @CommandPermission("omc.commands.taille.normal")
    @Description("Mettre la taille normalement")
    public void normal(Player player) {
        changerTaille(player, 1.0);
    }

    private void changerTaille(Player player, double taille) {
        var attribute = player.getAttribute(Attribute.SCALE);

        if (attribute == null) {
            player.sendMessage("§cImpossible de modifier votre taille.");
            return;
        }

        attribute.setBaseValue(taille);
        player.sendMessage("§aVotre taille est maintenant de §e" + taille);
    }
}