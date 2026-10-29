package fr.openmc.core.features.events.contents.open;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"opening"})
@Description("Les commandes d'ouverture")
public class OpenCommand {
    @Subcommand("start")
    @CommandPermission("omc.admins.commands.open.start")
    public void startCommand(Player player) {
        OpenManager.startOpening(player);
    }

    @Subcommand("end")
    @CommandPermission("omc.admins.commands.open.end")
    public void endCommand(Player player) {
        DynamicCooldownManager.clear(OpenManager.COOLDOWN_NAME);
        OpenManager.endOpening();
    }
}
