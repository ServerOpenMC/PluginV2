package fr.openmc.core.features.city.commands;

import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.menu.ChestMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class CityChestCommand {
    @Command({"city chest", "ville coffre"})
    @Description("Ouvre le coffre de la ville")
    @CommandPermission("omc.commands.city.chest")
    void chest(Player player, @Optional @Named("page") @Range(min=0) Integer page) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessage(player, Component.text("Vous n'êtes pas dans une ville"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!city.hasPermission(player.getUniqueId(), CPermission.CHEST)) {
            MessagesManager.sendMessage(player, Component.text("Vous n'avez pas les permissions de voir le coffre"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (city.getChestWatcher() != null) {
            MessagesManager.sendMessage(player, Component.text("Le coffre est déjà ouvert"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if ((page == null)) page = 1;
        if (page < 1) page = 1;
        if (page > city.getChestPages()) page = city.getChestPages();

        new ChestMenu(city, page).open(player);
    }

    @Command({"city upgradechest", "ville upgradecoffre"})
    @Description("Améliore la coffre de la ville")
    @CommandPermission("omc.commands.city.chest_upgrade")
    void upgrade(Player player) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessage(player, Component.text("Vous n'êtes pas dans une ville"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!city.hasPermission(player.getUniqueId(), CPermission.CHEST_UPGRADE)) {
            MessagesManager.sendMessage(player, Component.text("Vous n'avez pas les permissions d'améliorer le coffre de la ville"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (city.getChestPages() >= 5) {
            MessagesManager.sendMessage(player, Component.text("Le coffre de la Ville est déjà au niveau maximum"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        int price = city.getChestPages()*5000; // fonction linéaire f(x)=ax ; a=5000
        if (city.getBalance() < price) {
            MessagesManager.sendMessage(player, Component.text("La ville n'as pas assez d'argent ("+price+" nécessaires)"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        city.updateBalance((double) -price);

        city.upgradeChest();
        MessagesManager.sendMessage(player, Component.text("Le coffre a été amélioré"), Prefix.CITY, MessageType.SUCCESS, false);
    }
}
