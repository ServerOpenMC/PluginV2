package fr.openmc.core.features.credits;

import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Cooldown;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Description;

@Command("credits")
@Description("Ouvre l'interface des crédits !")
public class CreditsCommand {
    @Cooldown(4)
    @DefaultFor("~")
    public static void mainCommand(Player player) {
        new CreditsMenu(player).open();
    }
}
