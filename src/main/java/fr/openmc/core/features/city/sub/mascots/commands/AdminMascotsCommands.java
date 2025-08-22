package fr.openmc.core.features.city.sub.mascots.commands;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("admmascot")
@CommandPermission("omc.admins.commands.adminmascot")
public class AdminMascotsCommands {

    @Subcommand("remove")
    @CommandPermission("omc.admins.commands.adminmascot.remove")
    public void forceRemoveMascots(Player sender, @Named("player") Player target) {
        City city = CityManager.getPlayerCity(target.getUniqueId());

        if (city == null) {
            MessagesManager.sendMessage(sender, Component.text("§cVille inexistante"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        MascotsManager.removeMascotsFromCity(city);
        MessagesManager.sendMessage(sender, Component.text("§cVille inexistante"), Prefix.CITY, MessageType.ERROR, false);
    }
}
