package fr.openmc.core.features.city.commands;

import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.menu.CitizensPermsMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.UUID;

@Command({"ville perms", "city perms"})
public class CityPermsCommands {
    private static boolean verification(Player sender, UUID player) {
        City city = CityManager.getPlayerCity(player);

        if (city == null) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!city.getMembers().contains(player)) {
            MessagesManager.sendMessage(sender, Component.text("Ce joueur n'est pas dans ta ville"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (city.hasPermission(player, CPermission.OWNER)) {
            MessagesManager.sendMessage(sender, Component.text("Le maire a déjà les pleins pouvoirs"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        return true;
    }

    private static boolean verificationForModif(Player sender, CPermission permission) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());

        if (city == null) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!(city.hasPermission(sender.getUniqueId(), CPermission.OWNER))) {
            MessagesManager.sendMessage(sender, Component.text("Tu n'as pas la permission de gérer les permissions"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!city.hasPermission(sender.getUniqueId(), permission) && permission == CPermission.PERMS) {
            MessagesManager.sendMessage(sender, Component.text("Seul le maire peut modifier cette permission"), Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        return true;
    }

    @DefaultFor("~")
    @CommandPermission("omc.commands.city.perm_get")
    @AutoComplete("@city_members")
    void getGUI(Player sender, @Optional OfflinePlayer member) {
        if (member == null) {
            CitizensPermsMenu.openBook(sender);
            return;
        }

        CitizensPermsMenu.openBookFor(sender, member.getUniqueId());
    }

    @Subcommand("switch")
    @CommandPermission("omc.commands.city.perm_switch")
    @Description("Inverse la permission d'un joueur")
    @AutoComplete("@city_members")
    public static void swap(Player sender, OfflinePlayer player, CPermission permission) {
        if (!verification(sender, player.getUniqueId())) return;
        if (!verificationForModif(sender, permission)) return;
        City city = CityManager.getPlayerCity(sender.getUniqueId());

        if (city == null) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!city.getMembers().contains(player.getUniqueId())) {
            MessagesManager.sendMessage(sender, Component.text("Ce joueur n'est pas dans ta ville"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (city.hasPermission(player.getUniqueId(), permission)) {
            city.removePermission(player.getUniqueId(), permission);
            MessagesManager.sendMessage(sender, Component.text(player.getName()+" a perdu la permission \""+permission.toString()+"\""), Prefix.CITY, MessageType.SUCCESS, false);
        } else {
            city.addPermission(player.getUniqueId(), permission);
            MessagesManager.sendMessage(sender, Component.text(player.getName()+" a gagné la permission \""+permission.toString()+"\""), Prefix.CITY, MessageType.SUCCESS, false);
        }
    }
    
    @Subcommand("add")
    @CommandPermission("omc.commands.city.perm_add")
    @Description("Ajouter des permissions à un membre")
    @AutoComplete("@city_members")
    void add(Player sender, OfflinePlayer player, CPermission permission) {
        if (!verification(sender, player.getUniqueId())) return;
        if (!verificationForModif(sender, permission)) return;
        City city = CityManager.getPlayerCity(sender.getUniqueId());

        if (city == null) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!city.getMembers().contains(player)) {
            MessagesManager.sendMessage(sender, Component.text("Ce joueur n'est pas dans ta ville"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (city.hasPermission(player.getUniqueId(), permission)) {
            MessagesManager.sendMessage(sender, Component.text(player.getName() + " a déjà cette permission"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        city.addPermission(player.getUniqueId(), permission);
        MessagesManager.sendMessage(sender, Component.text("Les permissions de "+ player.getName() + " ont été modifiées"), Prefix.CITY, MessageType.SUCCESS, false);
    }

    @Subcommand("remove")
    @CommandPermission("omc.commands.city.perm_remove")
    @Description("Retirer des permissions à un membre")
    @AutoComplete("@city_members")
    void remove(Player sender, OfflinePlayer player, CPermission permission) {
        if (!verification(sender, player.getUniqueId())) return;
        if (!verificationForModif(sender, permission)) return;
        City city = CityManager.getPlayerCity(sender.getUniqueId());

        if (city == null) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!city.getMembers().contains(player)) {
            MessagesManager.sendMessage(sender, Component.text("Ce joueur n'est pas dans ta ville"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!city.hasPermission(player.getUniqueId(), permission)) {
            MessagesManager.sendMessage(sender, Component.text(player.getName() + " n'a pas cette permission"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        city.removePermission(player.getUniqueId(), permission);
        MessagesManager.sendMessage(sender, Component.text("Les permissions de "+ player.getName() + " ont été modifiées"), Prefix.CITY, MessageType.SUCCESS, false);
    }


    @Subcommand("get")
    @CommandPermission("omc.commands.city.perm_remove")
    @Description("Obtenir les permissions d'un membre")
    @AutoComplete("@city_members")
    void get(Player sender, OfflinePlayer player) {
        if (!verification(sender, player.getUniqueId())) return;
        City city = CityManager.getPlayerCity(sender.getUniqueId());

        if (city == null) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!city.getMembers().contains(player)) {
            MessagesManager.sendMessage(sender, Component.text("Ce joueur n'est pas dans ta ville"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!(city.hasPermission(sender.getUniqueId(), CPermission.PERMS))) {
            MessagesManager.sendMessage(sender, Component.text("Tu n'as pas la permission de consulter les permissions de "+player.getName()), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!city.getMembers().contains(player.getUniqueId())) {
            MessagesManager.sendMessage(sender, Component.text("Ce joueur n'est pas dans ta ville"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        Component content = Component.text(player.getName()).decorate(TextDecoration.UNDERLINED).append(Component.newline());

        for (CPermission permission : CPermission.values()) {
            if (city.hasPermission(player.getUniqueId(), permission)) {
                content = content.append(Component.text(permission.toString()).append(Component.newline()));
            }
        }

        sender.openBook(Book.book(
                Component.text("Permissions de "+player.getName()+": ").decorate(TextDecoration.BOLD),
                Component.text(""),
                content
        ));
    }
}
