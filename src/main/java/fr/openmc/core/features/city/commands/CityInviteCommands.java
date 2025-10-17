package fr.openmc.core.features.city.commands;

import fr.openmc.core.commands.autocomplete.OnlinePlayerAutoComplete;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.conditions.CityInviteConditions;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CityInviteCommands {
    public static final HashMap<Player, List<Player>> invitations = new HashMap<>(); // Invité, Inviteurs

    @Command("city invite")
    @CommandPermission("omc.commands.city.invite")
    @Description("Inviter un joueur dans votre ville")
    public static void invite(
            Player sender,
            @Named("player") @SuggestWith(OnlinePlayerAutoComplete.class) Player target
    ) {
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

    @Command("city accept")
    @CommandPermission("omc.commands.city.accept")
    @Description("Accepter une invitation")
    public static void acceptInvitation(
            Player player,
            @Named("inviteur") @SuggestWith(OnlinePlayerAutoComplete.class) Player inviter
    ) {
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

        MessagesManager.sendMessage(player, Component.text("Tu as rejoint " + newCity.getName()), Prefix.CITY, MessageType.SUCCESS, false);
        if (inviter.isOnline()) {
            MessagesManager.sendMessage(inviter, Component.text(player.getName() + " a accepté ton invitation !"), Prefix.CITY, MessageType.SUCCESS, true);
        }
    }

    @Command("city deny")
    @CommandPermission("omc.commands.city.deny")
    @Description("Refuser une invitation")
    public static void denyInvitation(
            Player player,
            @Named("inviteur") @SuggestWith(OnlinePlayerAutoComplete.class) Player inviter
    ) {
        if (!CityInviteConditions.canCityInviteDeny(player, inviter)) return;

        invitations.remove(player);

        if (inviter.isOnline()) {
            MessagesManager.sendMessage(inviter, Component.text(player.getName() + " a refusé ton invitation"), Prefix.CITY, MessageType.WARNING, true);
        }
    }
}
