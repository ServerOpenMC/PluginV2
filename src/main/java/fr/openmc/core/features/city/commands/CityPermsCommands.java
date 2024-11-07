package fr.openmc.core.features.city.commands;

import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermissions;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.AutoComplete;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.UUID;

@Command({"ville perms", "city perms"})
public class CityPermsCommands {
    private boolean verification(Player sender, UUID player) {
        String playerCity = CityManager.getPlayerCity(sender.getUniqueId());

        if (playerCity == null) {
            MessagesManager.sendMessageType(sender, "Tu n'habite dans aucune ville", Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!CityManager.getMembers(playerCity).contains(player)) {
            MessagesManager.sendMessageType(sender, "Ce joueur n'est pas dans ta ville", Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (CityPermissions.hasPermission(playerCity, player, CPermission.OWNER)) {
            MessagesManager.sendMessageType(sender, "Le maire a déjà les pleins pouvoirs", Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        return true;
    }

    private boolean verificationForModif(Player sender, CPermission permission) {
        String playerCity = CityManager.getPlayerCity(sender.getUniqueId());

        if (!(CityPermissions.hasPermission(playerCity, sender.getUniqueId(), CPermission.OWNER))) {
            MessagesManager.sendMessageType(sender, "Tu n'as pas la permission d'ajouter des permissions", Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        if (!CityPermissions.hasPermission(playerCity, sender.getUniqueId(), permission) && permission == CPermission.PERMS) {
            MessagesManager.sendMessageType(sender, "Seul le maire peut modifier cette permissions", Prefix.CITY, MessageType.ERROR, false);
            return false;
        }

        return true;
    }
    
    @Subcommand("add")
    @CommandPermission("omc.commands.city.perm_add")
    @Description("Ajouter des permissions à quelqu'un")
    @AutoComplete("@city_members")
    void add(Player sender, UUID player, CPermission permission) {
        if (!verification(sender, player)) return;
        if (!verificationForModif(sender, permission)) return;
        String playerCity = CityManager.getPlayerCity(sender.getUniqueId());

        if (!CityManager.getMembers(playerCity).contains(player)) {
            MessagesManager.sendMessageType(sender, "Ce joueur n'est pas dans ta ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (CityPermissions.hasPermission(playerCity, player, permission)) {
            MessagesManager.sendMessageType(sender, "Ce joueur a déjà cette permission", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        CityPermissions.addPermission(CityManager.getPlayerCity(sender.getUniqueId()), player, permission);
        MessagesManager.sendMessageType(sender, "Les permissions ont été modifiées", Prefix.CITY, MessageType.SUCCESS, false);
    }

    @Subcommand("remove")
    @CommandPermission("omc.commands.city.perm_remove")
    @Description("Retirer des permissions à quelqu'un")
    @AutoComplete("@city_members")
    void remove(Player sender, OfflinePlayer player, CPermission permission) {
        if (!verification(sender, player.getUniqueId())) return;
        if (!verificationForModif(sender, permission)) return;
        String playerCity = CityManager.getPlayerCity(sender.getUniqueId());

        if (!CityManager.getMembers(playerCity).contains(player.getUniqueId())) {
            MessagesManager.sendMessageType(sender, "Ce joueur n'est pas dans ta ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!CityPermissions.hasPermission(playerCity, player.getUniqueId(), permission)) {
            MessagesManager.sendMessageType(sender, "Ce joueur n'a pas cette permission", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        CityPermissions.removePermission(CityManager.getPlayerCity(sender.getUniqueId()), player.getUniqueId(), permission);
        MessagesManager.sendMessageType(sender, "Les permissions ont été modifiées", Prefix.CITY, MessageType.SUCCESS, false);
    }
    @Subcommand("get")
    @CommandPermission("omc.commands.city.perm_remove")
    @Description("Retirer des permissions à quelqu'un")
    @AutoComplete("@city_members")
    void get(Player sender, OfflinePlayer player) {
        if (!verification(sender, player.getUniqueId())) return;
        String playerCity = CityManager.getPlayerCity(sender.getUniqueId());


        if (!(CityPermissions.hasPermission(playerCity, sender.getUniqueId(), CPermission.PERMS))) {
            MessagesManager.sendMessageType(sender, "Tu n'as pas la permission de consulter les permissions", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!CityManager.getMembers(playerCity).contains(player.getUniqueId())) {
            MessagesManager.sendMessageType(sender, "Ce joueur n'est pas dans ta ville", Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        Component content = Component.text(player.getName()).decorate(TextDecoration.UNDERLINED).append(Component.newline());

        for (CPermission permission : CPermission.values()) {
            if (CityPermissions.hasPermission(playerCity, player.getUniqueId(), permission)) {
                content = content.append(Component.text(permission.toString()).append(Component.newline()));
            }
        }

        sender.openBook(Book.book(
                Component.text("Permissions de "+player.getName()).decorate(TextDecoration.BOLD),
                Component.text(""),
                content
        ));
    }
}
