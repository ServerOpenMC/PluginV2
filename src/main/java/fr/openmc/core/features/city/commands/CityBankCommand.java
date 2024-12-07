package fr.openmc.core.features.city.commands;

import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.menu.BankMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class CityBankCommand {
    @Command({"city bank", "ville banque"})
    @Description("Ouvre la banque de la ville")
    @CommandPermission("omc.commands.city.bank")
    void bank(Player player, @Optional @Named("page") @Range(min=0) Integer page) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessageType(player, "Vous n'êtes pas dans une ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!city.hasPermission(player.getUniqueId(), CPermission.BANK)) {
            MessagesManager.sendMessageType(player, "Vous n'avez pas les permissions de voir la banque", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (city.getBankWatcher() != null) {
            MessagesManager.sendMessageType(player, "La banque est déjà ouverte", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if ((page == null)) page = 1;
        if (page < 1) page = 1;
        if (page > city.getBankPages()) page = city.getBankPages();

        new BankMenu(city, page).open(player);
    }

    @Command({"city upgradebank", "ville upgradebanque"})
    @Description("Améliore la banque de la ville")
    @CommandPermission("omc.commands.city.bank_upgrade")
    void upgrade(Player player) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessageType(player, "Vous n'êtes pas dans une ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!city.hasPermission(player.getUniqueId(), CPermission.BANK_UPGRADE)) {
            MessagesManager.sendMessageType(player, "Vous n'avez pas les permissions d'améliorer la banque", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (city.getBankPages() >= 5) {
            MessagesManager.sendMessageType(player, "La banque est déjà au niveau maximum", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        int price = city.getBankPages()*5000; // fonction linéaire f(x)=ax ; a=5000
        if (city.getBalance() < price) {
            MessagesManager.sendMessageType(player, "La ville n'as pas assez d'argent ("+price+" nécessaires)", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        city.updateBalance((double) -price);

        city.upgradeBank();
        MessagesManager.sendMessageType(player, "La banque a été améliorée", Prefix.CITY, MessageType.SUCCESS, false);
    }
}
