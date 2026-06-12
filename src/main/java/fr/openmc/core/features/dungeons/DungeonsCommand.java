package fr.openmc.core.features.dungeons;

import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.CommandPlaceholder;

@Command({"keyvault", "kv", "vault", "keys"})
public class DungeonsCommand {
    @CommandPlaceholder
    public void dungeonsCommands(Player player) {
        new DungeonsMenu(player).open();
    }
}
