package fr.openmc.core.features.city.sub.war.actions;

import fr.openmc.api.menulib.default_menu.ConfirmMenu;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.city.sub.war.menu.selection.WarChooseParticipantsMenu;
import fr.openmc.core.features.city.sub.war.menu.selection.WarChooseSizeMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class WarActions {

    public static void beginLaunchWar(Player player, City cityAttack) {
        UUID launcherUUID = player.getUniqueId();
        City launchCity = CityManager.getPlayerCity(launcherUUID);

        if (launchCity == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!launchCity.getType().equals(CityType.WAR)) {
            MessagesManager.sendMessage(player,
                    Component.text("Votre ville n'est pas dans un statut de §cgueere§f! Changez la type de votre ville avec §c/city type §fou depuis le §cMenu Princiapl des Villes"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!cityAttack.getType().equals(CityType.WAR)) {
            MessagesManager.sendMessage(player,
                    Component.text("La ville que vous essayez d'attaquer n'est pas dans un statut de guerre!"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!launchCity.hasPermission(player.getUniqueId(), CPermission.LAUNCH_WAR)) {
            MessagesManager.sendMessage(player,
                    Component.text("Vous n'avez pas la permission de lancer une guerre pour la ville"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (launchCity.isInWar()) {
            MessagesManager.sendMessage(player,
                    Component.text("Votre ville est en déjà en guerre!"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (cityAttack.isInWar()) {
            MessagesManager.sendMessage(player,
                    Component.text("La ville que vous essayez d'attaquer est déjà en guerre!"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (cityAttack.getMascot().isImmunity()) {
            MessagesManager.sendMessage(player,
                    Component.text("La ville que vous essayez d'attaquer est en période d'immunité!"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (launchCity.getMascot().isImmunity()) {
            MessagesManager.sendMessage(player,
                    Component.text("Votre ville est en période d'immunité!"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (cityAttack.getOnlineMembers().isEmpty()) {
            MessagesManager.sendMessage(player,
                    Component.text("La ville que vous essayez d'attaquer a aucun membre de connecté!"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        int attackers = launchCity.getOnlineMembers().size();
        int defenders = cityAttack.getOnlineMembers().size();
        int maxSize = Math.min(attackers, defenders);

        if (maxSize < 1) {
            MessagesManager.sendMessage(player,
                    Component.text("Aucun combat possible (pas assez de joueurs connectés)"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        new WarChooseSizeMenu(player, launchCity, cityAttack, maxSize).open();
    }

    public static void preFinishLaunchWar(Player player, City cityLaunch, City cityAttack, int count) {
        List<UUID> available = cityLaunch.getOnlineMembers().stream().toList();

        if (available.size() < count) {
            player.sendMessage("§cPas assez de membres connectés pour lancer un combat en " + count + " vs " + count);
            return;
        }

        new WarChooseParticipantsMenu(player, cityLaunch, cityAttack, count, new HashSet<>()).open();
    }

    public static void confirmLaunchWar(Player player, City cityLaunch, City cityAttack, List<UUID> attackers) {
        if (cityLaunch.isInWar() || cityAttack.isInWar()) {
            MessagesManager.sendMessage(player,
                    Component.text("Une des villes est déjà en guerre!"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        ConfirmMenu menu = new ConfirmMenu(player,
                () -> {
                    // OUI
                    //lancer message en face et prévenir cityLaunch que tata et bidule
                },
                () -> {
                    player.closeInventory();
                },
                List.of(
                        Component.text("§c§lATTENTION"),
                        Component.text("§7Vous êtes sur le point de lancer une guerre contre §c" + cityAttack.getName()),
                        Component.text("§7avec §c" + attackers.size() + " §7joueurs de votre ville."),
                        Component.text("§e§lCLIQUEZ ICI POUR CONFIRMER")
                ),
                List.of(
                        Component.text("§7Ne pas lancer une guerre contre §c" + cityAttack.getName())
                )
        );
        menu.open();

    }
}