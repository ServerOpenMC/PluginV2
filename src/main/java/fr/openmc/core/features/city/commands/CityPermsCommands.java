package fr.openmc.core.features.city.commands;

import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
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
    private final MessagesManager msgCity  = new MessagesManager(Prefix.CITY);

    private boolean verification(Player sender, UUID player) {
        City city = CityManager.getPlayerCity(player);

        if (city == null) {
            msgCity.error(sender, MessagesManager.Message.PLAYERNOCITY.getMessage());
            return false;
        }

        if (!city.getMembers().contains(player)) {
            msgCity.error(sender, "Ce joueur n'est pas dans ta ville");
            return false;
        }

        if (city.hasPermission(player, CPermission.OWNER)) {
            msgCity.error(sender, "Le maire a déjà les pleins pouvoirs");
            return false;
        }

        return true;
    }

    private boolean verificationForModif(Player sender, CPermission permission) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());

        if (city == null) {
            msgCity.error(sender, MessagesManager.Message.PLAYERNOCITY.getMessage());
            return false;
        }

        if (!(city.hasPermission(sender.getUniqueId(), CPermission.OWNER))) {
            msgCity.error(sender, "Tu n'as pas la permission de gérer les permissions");
            return false;
        }

        if (!city.hasPermission(sender.getUniqueId(), permission) && permission == CPermission.PERMS) {
            msgCity.error(sender, "Seul le maire peut modifier cette permission");
            return false;
        }

        return true;
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
            msgCity.error(sender, MessagesManager.Message.PLAYERNOCITY.getMessage());
            return;
        }

        if (!city.getMembers().contains(player)) {
            msgCity.error(sender, "Ce joueur n'est pas dans ta ville");
            return;
        }

        if (city.hasPermission(player.getUniqueId(), permission)) {
            msgCity.error(sender, player.getName() + " a déjà cette permission");
            return;
        }

        city.addPermission(player.getUniqueId(), permission);
        msgCity.success(sender, "Les permissions de "+ player.getName() + " ont été modifiées");
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
            msgCity.error(sender, MessagesManager.Message.PLAYERNOCITY.getMessage());
            return;
        }

        if (!city.getMembers().contains(player)) {
            msgCity.error(sender, "Ce joueur n'est pas dans ta ville");
            return;
        }

        if (!city.hasPermission(player.getUniqueId(), permission)) {
            msgCity.error(sender, player.getName() + " n'a pas cette permission");
            return;
        }

        city.removePermission(player.getUniqueId(), permission);
        msgCity.success(sender, "Les permissions de "+ player.getName() + " ont été modifiées");
    }


    @Subcommand("get")
    @CommandPermission("omc.commands.city.perm_remove")
    @Description("Obtenir les permissions d'un membre")
    @AutoComplete("@city_members")
    void get(Player sender, OfflinePlayer player) {
        if (!verification(sender, player.getUniqueId())) return;
        City city = CityManager.getPlayerCity(sender.getUniqueId());

        if (city == null) {
            msgCity.error(sender, MessagesManager.Message.PLAYERNOCITY.getMessage());
            return;
        }

        if (!city.getMembers().contains(player)) {
            msgCity.error(sender, "Ce joueur n'est pas dans ta ville");
            return;
        }

        if (!(city.hasPermission(sender.getUniqueId(), CPermission.PERMS))) {
            msgCity.error(sender, "Tu n'as pas la permission de consulter les permissions de "+player.getName());
            return;
        }

        if (!city.getMembers().contains(player.getUniqueId())) {
            msgCity.error(sender, "Ce joueur n'est pas dans ta ville");
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
