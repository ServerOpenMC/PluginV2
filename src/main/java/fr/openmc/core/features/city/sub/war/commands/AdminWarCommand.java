package fr.openmc.core.features.city.sub.war.commands;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.war.War;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("admwar")
@CommandPermission("omc.admins.commands.adminwar")
public class AdminWarCommand {
    @Subcommand("startCombat")
    @CommandPermission("omc.admins.commands.adminwar.startCombat")
    void startCombat(Player player, @Named("name") String cityName) {
        City city = CityManager.getCityByName(cityName);

        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.CITY_NOT_FOUND.getMessage(), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        if (!city.isInWar() && city.getWar().getPhase() != War.WarPhase.PREPARATION) {
            MessagesManager.sendMessage(player, Component.text("Cette ville n'est pas en preparation de guerre!"), Prefix.STAFF, MessageType.ERROR, false);
            return;
        }

        city.getWar().startCombat();
    }
}
