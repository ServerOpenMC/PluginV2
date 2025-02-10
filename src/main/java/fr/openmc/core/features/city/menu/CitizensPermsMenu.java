package fr.openmc.core.features.city.menu;

import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.commands.CityPermsCommands;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class CitizensPermsMenu {
    public static void openBookFor(Player sender, UUID player) {
        City hisCity = CityManager.getPlayerCity(sender.getUniqueId());
        City city = CityManager.getPlayerCity(sender.getUniqueId());

        if (hisCity == null) {
            MessagesManager.sendMessage(sender, Component.text("Ce joueur n'habite aucune ville"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (city == null) {
            MessagesManager.sendMessage(sender, Component.text("Tu n'habites dans aucune ville"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!Objects.equals(hisCity.getUUID(), city.getUUID())) {
            MessagesManager.sendMessage(sender, Component.text("Ce joueur n'habite pas dans ta ville"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!city.hasPermission(sender.getUniqueId(), CPermission.PERMS)) {
            MessagesManager.sendMessage(sender, Component.text("Tu n'as pas la permission d'ouvrir ce menu"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        ArrayList<Component> pages = new ArrayList<>();

        Component firstPage = Component.text("        Permissions").append(
                Component.text("\n\n")
                        .decorate(TextDecoration.UNDERLINED)
                        .decorate(TextDecoration.BOLD));

        ArrayList<Component> perms = new ArrayList<>();

        for (CPermission permission: CPermission.values()) {
            if (permission == CPermission.OWNER) continue;

            perms.add(Component.text((city.hasPermission(player, permission) ? "+ ": "- ")+permission.getDisplayName())
                    .decoration(TextDecoration.UNDERLINED, false)
                    .decoration(TextDecoration.BOLD, false)
                    .clickEvent(ClickEvent.callback((plr1) -> {
                        CityPermsCommands.swap(sender, sender.getServer().getOfflinePlayer(player), permission);
                        sender.closeInventory();
                        openBookFor(sender, player);
                    }))
                    .color(city.hasPermission(player, permission) ? NamedTextColor.DARK_GREEN : NamedTextColor.RED)
                    .append(Component.newline()));
        }

        for (int i = 0; i < 9 && !perms.isEmpty(); i++) {
            firstPage = firstPage.append(perms.removeFirst());
        }
        firstPage = firstPage.append(Component.text("\n\n\n⬅ Retour")
                .clickEvent(ClickEvent.callback((plr1) -> {
                    sender.closeInventory();
                    openBook(sender);
                }))
                .color(NamedTextColor.BLACK));

        pages.add(firstPage);

        while (!perms.isEmpty()) {
            Component page = Component.text("");

            for (int i = 0; i < 14 && !perms.isEmpty(); i++) {
                page = page.append(perms.removeFirst());
            }

            pages.add(page);
        }

        sender.openBook(Book.book(Component.text(""), Component.text(""), pages));
    }

    public static void openBook(Player sender) {
        City city = CityManager.getPlayerCity(sender.getUniqueId());

        if (city == null) {
            MessagesManager.sendMessage(sender, Component.text("Tu n'habites dans aucune ville"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!city.hasPermission(sender.getUniqueId(), CPermission.PERMS)) {
            MessagesManager.sendMessage(sender, Component.text("Tu n'as pas la permission d'ouvrir ce menu"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        ArrayList<Component> pages = new ArrayList<>();

        Component firstPage = Component.text("        ").append(
                Component.text("Registres\n\n")
                        .decorate(TextDecoration.UNDERLINED)
                        .decorate(TextDecoration.BOLD));

        ArrayList<Component> players = new ArrayList<>();

        for (UUID citizen: city.getMembers()) {
            OfflinePlayer offlinePlayer = sender.getServer().getOfflinePlayer(citizen);
            players.add(Component.text("- "+Objects.requireNonNullElse(offlinePlayer.getName(), "Inconnu"))
                    .decoration(TextDecoration.UNDERLINED, false)
                    .decoration(TextDecoration.BOLD, false)
                    .clickEvent(ClickEvent.runCommand("/city perms "+offlinePlayer.getName()))
                    .append(Component.newline()));
        }

        for (int i = 0; i < 11 && !players.isEmpty(); i++) {
            firstPage = firstPage.append(players.removeFirst());
        }
        pages.add(firstPage);

        // Creer une page par 14 joueurs
        while (!players.isEmpty()) {
            Component page = Component.text("");

            for (int i = 0; i < 14 && !players.isEmpty(); i++) {
                page = page.append(players.removeFirst());
            }

            pages.add(page);
        }

        sender.openBook(Book.book(Component.text("Registre des permissions"), Component.text(""), pages));
    }
}
