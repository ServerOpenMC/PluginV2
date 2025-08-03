package fr.openmc.core.features.city.sub.war.commands;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.city.sub.war.WarManager;
import fr.openmc.core.features.city.sub.war.WarPendingDefense;
import fr.openmc.core.features.city.sub.war.actions.WarActions;
import fr.openmc.core.features.city.sub.war.menu.main.MainWarMenu;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Command({"guerre", "war"})
@CommandPermission("omc.commands.city.war")
public class WarCommand {
    @DefaultFor("~")
    void main(Player player) {
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());
        if (playerCity == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!playerCity.getType().equals(CityType.WAR)) {
            MessagesManager.sendMessage(player,
                    Component.text("Votre ville n'est pas dans un statut de §cgueere§f! Changez la type de votre ville avec §c/city type §fou depuis le §cMenu Princiapl des Villes"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (playerCity.isImmune()) {
            MessagesManager.sendMessage(player,
                    Component.text("Votre ville est actuellement en période d'immunité, vous ne pouvez pas lancer de guerre pour le moment. \nTemps restant : " + DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(playerCity.getUUID(), "city:immunity"))),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!playerCity.hasPermission(player.getUniqueId(), CPermission.LAUNCH_WAR)) {
            MessagesManager.sendMessage(player,
                    Component.text("Vous n'avez pas la permission de lancer une guerre pour la ville"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (playerCity.isInWar()) {
            MessagesManager.sendMessage(player,
                    Component.text("Vous êtes déjà en guerre !"),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        new MainWarMenu(player).open();
    }

    @Subcommand("acceptdefense")
    @CommandPermission("omc.commands.city.war.acceptdefense")
    @Description("Accepter de participer a une guerre")
    public void acceptDefense(Player player) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city == null) {
            player.sendMessage("§cVous n'avez pas de ville.");
            return;
        }

        WarPendingDefense pending = WarManager.getPendingDefenseFor(city);
        if (pending == null) {
            MessagesManager.sendMessage(player,
                    Component.text("Aucune guerre en cours de préparation."),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        boolean accepted = pending.accept(player.getUniqueId());
        if (!accepted) {
            MessagesManager.sendMessage(player,
                    Component.text("Le nombre maximal de défenseurs est atteint."),
                    Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        MessagesManager.sendMessage(player,
                Component.text("Vous participez désormais à la défense ! Plus aucun retour en arrière possible."),
                Prefix.CITY, MessageType.ERROR, false);

        if (pending.getAcceptedDefenders().size() >= pending.getRequired() && !pending.isAlreadyExecuted()) {
            pending.setAlreadyExecuted(true);

            City defendingCity = pending.getDefender();
            City attackingCity = pending.getAttacker();
            List<UUID> attackers = pending.getAttackers();

            WarActions.launchWar(attackingCity, defendingCity, attackers,
                    new ArrayList<>(defendingCity.getMembers()), pending.getRequired(), pending);
        }
    }
}
