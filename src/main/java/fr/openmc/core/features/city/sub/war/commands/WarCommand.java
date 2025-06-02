package fr.openmc.core.features.city.sub.war.commands;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.city.sub.war.menu.MainWarMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;

@Command({"guerre", "war"})
public class WarCommand {
    @DefaultFor("~")
    void main(Player player) {
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());
        if (playerCity == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!playerCity.getType().equals(CityType.WAR)) {
            MessagesManager.sendMessage(player,
                    Component.text("Votre ville n'est pas dans un statut de §cgueere§f! Changez la type de votre ville avec §c/city type §fou depuis le §cMenu Princiapl des Villes"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        new MainWarMenu(player).open();
    }
}
