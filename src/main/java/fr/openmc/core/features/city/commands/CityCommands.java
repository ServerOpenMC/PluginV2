package fr.openmc.core.features.city.commands;

import fr.openmc.api.chronometer.Chronometer;
import fr.openmc.api.input.DialogInput;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.actions.*;
import fr.openmc.core.features.city.conditions.*;
import fr.openmc.core.features.city.menu.CityChunkMenu;
import fr.openmc.core.features.city.menu.CityMenu;
import fr.openmc.core.features.city.menu.CityTypeMenu;
import fr.openmc.core.features.city.menu.NoCityMenu;
import fr.openmc.core.features.city.menu.list.CityListDetailsMenu;
import fr.openmc.core.features.city.menu.list.CityListMenu;
import fr.openmc.core.features.city.view.CityViewManager;
import fr.openmc.core.utils.InputUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static fr.openmc.core.utils.InputUtils.MAX_LENGTH_CITY;

@Command({"ville", "city"})
public class CityCommands {
    public static final HashMap<Player, List<Player>> invitations = new HashMap<>(); // Invité, Inviteurs

    @DefaultFor("~")
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
    void create(Player player, @Optional String name) {
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

    @Subcommand("invite")
    @CommandPermission("omc.commands.city.invite")
    @Description("Inviter un joueur dans votre ville")
    public static void invite(Player sender, @Named("invité") Player target) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());

        if (!CityInviteConditions.canCityInvitePlayer(city, sender, target)) return;

        List<Player> playerInvitations = invitations.get(target);
        if (playerInvitations == null) {
            List<Player> newInvitations = new ArrayList<>();
            newInvitations.add(sender);
            invitations.put(target, newInvitations);
        } else {
            playerInvitations.add(sender);
        }
        MessagesManager.sendMessage(sender, Component.text("Tu as invité " + target.getName() + " dans ta ville"), Prefix.CITY, MessageType.SUCCESS, false);
        MessagesManager.sendMessage(target,
                Component.text("Tu as été invité(e) par " + sender.getName() + " dans la ville " + city.getName() + "\n")
                        .append(Component.text("§8Faite §a/city accept §8pour accepter\n").clickEvent(ClickEvent.runCommand("/city accept " + sender.getName())).hoverEvent(HoverEvent.showText(Component.text("Accepter l'invitation"))))
                        .append(Component.text("§8Faite §c/city deny §8pour refuser\n").clickEvent(ClickEvent.runCommand("/city deny " + sender.getName())).hoverEvent(HoverEvent.showText(Component.text("Refuser l'invitation")))),
                Prefix.CITY, MessageType.INFO, false);
    }

    @Subcommand("accept")
    @CommandPermission("omc.commands.city.accept")
    @Description("Accepter une invitation")
    public static void acceptInvitation(Player player, Player inviter) {
        List<Player> playerInvitations = invitations.get(player);

        if (playerInvitations == null) {
            MessagesManager.sendMessage(player, Component.text("Tu n'as aucune invitation en attente"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!playerInvitations.contains(inviter)) {
            MessagesManager.sendMessage(player, Component.text(inviter.getName() + " ne vous a pas invité"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        City newCity = CityManager.getPlayerCity(inviter.getUniqueId());

        if (!CityInviteConditions.canCityInviteAccept(newCity, inviter, player)) return;

        newCity.addPlayer(player.getUniqueId());

        invitations.remove(player);

        MessagesManager.sendMessage(player, Component.text("Tu as rejoint "+ newCity.getName()), Prefix.CITY, MessageType.SUCCESS, false);
        if (inviter.isOnline()) {
            MessagesManager.sendMessage(inviter, Component.text(player.getName()+" a accepté ton invitation !"), Prefix.CITY, MessageType.SUCCESS, true);
        }
    }

    @Subcommand("deny")
    @CommandPermission("omc.commands.city.deny")
    @Description("Refuser une invitation")
    public static void denyInvitation(Player player, Player inviter) {
        if (!CityInviteConditions.canCityInviteDeny(player, inviter)) return;

        invitations.remove(player);

        if (inviter.isOnline()) {
            MessagesManager.sendMessage(inviter, Component.text(player.getName() + " a refusé ton invitation"), Prefix.CITY, MessageType.WARNING, true);
        }
    }

    @Subcommand("rename")
    @CommandPermission("omc.commands.city.rename")
    @Description("Renommer une ville")
    void rename(Player player, @Named("nouveau nom") String name) {
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
    @AutoComplete("@city_members")
    void transfer(Player sender, @Named("owner") OfflinePlayer player) {
        City playerCity = CityManager.getPlayerCity(sender.getUniqueId());

        if (!CityManageConditions.canCityTransfer(playerCity, sender, player.getUniqueId())) return;

        if (playerCity == null) return;

        CityTransferAction.transfer(sender, playerCity, player);
    }

    @Subcommand("kick")
    @CommandPermission("omc.commands.city.kick")
    @Description("Exclure un habitant de votre ville")
    @AutoComplete("@city_members")
    void kick(Player sender, @Named("exclu") OfflinePlayer player) {
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

    @Subcommand("claim")
    @CommandPermission("omc.commands.city.claim")
    @Description("Claim un chunk pour votre ville")
    @DefaultFor("~")
    void claim(Player sender) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());

        if (!CityClaimCondition.canCityClaim(city, sender)) return;

        Chunk chunk = sender.getLocation().getChunk();

        CityClaimAction.startClaim(sender, chunk.getX(), chunk.getZ());
    }

    @Subcommand("unclaim")
    @CommandPermission("omc.commands.city.unclaim")
    @Description("Unclaim un chunk pour votre ville")
    void unclaim(Player sender) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());

        if (!CityUnclaimCondition.canCityUnclaim(city, sender)) return;

        Chunk chunk = sender.getLocation().getChunk();

        CityUnclaimAction.startUnclaim(sender, chunk.getX(), chunk.getZ());
    }

    @Subcommand("claim view")
    @Description("Voir les villes aux alentours")
    @CommandPermission("omc.commands.city.view")
    void view(Player player) {
        CityViewManager.startView(player);
    }

    @Subcommand("map")
    @CommandPermission("omc.commands.city.map")
    @Description("Affiche la map des claims.")
    void map(Player sender) {
        new CityChunkMenu(sender).open();
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
