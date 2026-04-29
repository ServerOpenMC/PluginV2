package fr.openmc.core.features.city.commands;

import fr.openmc.api.chronometer.Chronometer;
import fr.openmc.api.input.DialogInput;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.actions.*;
import fr.openmc.core.features.city.commands.autocomplete.CityMembersAutoComplete;
import fr.openmc.core.features.city.conditions.CityCreateConditions;
import fr.openmc.core.features.city.conditions.CityLeaveCondition;
import fr.openmc.core.features.city.conditions.CityManageConditions;
import fr.openmc.core.features.city.menu.CityTypeMenu;
import fr.openmc.core.features.city.menu.NoCityMenu;
import fr.openmc.core.features.city.menu.list.CityListDetailsMenu;
import fr.openmc.core.features.city.menu.list.CityListMenu;
import fr.openmc.core.features.city.menu.main.CityMenu;
import fr.openmc.core.utils.InputUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import static fr.openmc.core.utils.InputUtils.MAX_LENGTH_CITY;

@Command({"ville", "city"})
public class CityCommands {
    @CommandPlaceholder()
    public static void mainCommand(Player player) {
        if (!Chronometer.containsChronometer(player.getUniqueId(), "mascot:stick")) {
            City playerCity = CityManager.getPlayerCity(player.getUniqueId());
                if (playerCity == null) {
                    NoCityMenu menu = new NoCityMenu(player);
                    menu.open();
                } else {
                    CityMenu menu = new CityMenu(player);
                    menu.open();
                }
        } else {
	        MessagesManager.sendMessage(player, Component.text("Vous ne pouvez pas ouvrir le menu des villes sans avoir posé la mascotte"), Prefix.CITY, MessageType.ERROR, false);
        }
    }

    @Subcommand("info")
    @CommandPermission("omc.commands.city.info")
    @Description("Avoir des informations sur votre ville")
    void info(Player player) {
        City city = CityManager.getPlayerCity(player.getUniqueId());

        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        new CityListDetailsMenu(player, city).open();
    }


    @Subcommand("create")
    @CommandPermission("omc.commands.city.create")
    @Description("Créer une ville")
    void create(
            Player player,
            @Named("nom de ville") @Optional String name
    ) {
        if (!CityCreateConditions.canCityCreate(player, null)) {
            return;
        }

        if (name != null) {
            CityCreateAction.beginCreateCity(player, name);
            return;
        }

        DialogInput.send(player, Component.text("Entrez le nom de la ville"), MAX_LENGTH_CITY, input -> {
                    if (input == null) return;
                    CityCreateAction.beginCreateCity(player, input);
                }
        );
    }

    @Subcommand("delete")
    @CommandPermission("omc.commands.city.delete")
    @Description("Supprimer votre ville")
    void delete(Player sender) {
        CityDeleteAction.startDeleteCity(sender);
    }

    @Subcommand("rename")
    @CommandPermission("omc.commands.city.rename")
    @Description("Renommer une ville")
    void rename(
            Player player,
            @Named("nouveau nom") String name
    ) {
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

        if (!CityManageConditions.canCityRename(playerCity, player)) return;

        if (!InputUtils.isInputCityName(name)) {
	        MessagesManager.sendMessage(player, Component.text("Le nom de ville est invalide, il doit comporter uniquement des caractères alphanumeriques et maximum " + MAX_LENGTH_CITY + " caractères."), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        playerCity.rename(name);
        MessagesManager.sendMessage(player, Component.text("La ville a été renommée en " + name), Prefix.CITY, MessageType.SUCCESS, false);
    }

    @Subcommand("transfer")
    @CommandPermission("omc.commands.city.transfer")
    @Description("Transfert la propriété de votre ville")
    void transfer(
            Player sender,
            @Named("nouveau propriétaire") @SuggestWith(CityMembersAutoComplete.class) OfflinePlayer player
    ) {
        City playerCity = CityManager.getPlayerCity(sender.getUniqueId());

        if (!CityManageConditions.canCityTransfer(playerCity, sender, player.getUniqueId())) return;

        if (playerCity == null) return;

        CityTransferAction.transfer(sender, playerCity, player);
    }

    @Subcommand("kick")
    @CommandPermission("omc.commands.city.kick")
    @Description("Exclure un habitant de votre ville")
    void kick(
            Player sender,
            @SuggestWith(CityMembersAutoComplete.class) @Named("membre à exclure") OfflinePlayer player
    ) {
        CityKickAction.startKick(sender, player);
    }

    @Subcommand("leave")
    @CommandPermission("omc.commands.city.leave")
    @Description("Quitter votre ville")
    void leave(Player player) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (!CityLeaveCondition.canCityLeave(city, player)) return;

        CityLeaveAction.startLeave(player);
    }

    @Subcommand("list")
    @CommandPermission("omc.commands.city.list")
    public void list(Player player) {
        if (CityManager.getCities().isEmpty()) {
            MessagesManager.sendMessage(player, Component.text("Aucune ville n'existe"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }
        
        CityListMenu menu = new CityListMenu(player);
        menu.open();
    }

    @Subcommand("type")
    @CommandPermission("omc.commands.city.type")
    public void change(Player sender) {
        new CityTypeMenu(sender).open();
    }
}
