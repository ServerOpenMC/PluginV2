package fr.openmc.core.features.city.sub.mayor.commands;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mayor.actions.MayorCommandAction;
import fr.openmc.core.features.city.sub.mayor.actions.MayorSetWarpAction;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.models.CityLaw;
import fr.openmc.core.utils.PlayerUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class MayorCommands {
    @Command({"city mayor", "ville maire"})
    @CommandPermission("omc.commands.city.mayor")
    @Description("Ouvre le menu des maires")
    void mayor(Player sender) {
        MayorCommandAction.launchInteractionMenu(sender);
    }

    @Command({"city warp", "ville warp"})
    @Description("Teleporte au warp commun de la ville")
    void warp(Player player) {
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

        if (playerCity == null) return;

        CityLaw law = playerCity.getLaw();
        Location warp = law.getWarp();

        if (warp == null) {
            if (MayorManager.phaseMayor == 2) {
                MessagesManager.sendMessage(player, Component.text("Le Warp de la Ville n'est pas encore défini ! Demandez au §6Maire §fActuel d'en mettre un ! §8§o*via /city setwarp ou avec le Menu des Lois*"), Prefix.CITY, MessageType.INFO, true);
                return;
            }
            MessagesManager.sendMessage(player, Component.text("Le Warp de la Ville n'est pas encore défini ! Vous devez attendre que un Maire soit élu pour mettre un Warp"), Prefix.CITY, MessageType.INFO, true);
            return;
        }

        PlayerUtils.sendFadeTitleTeleport(
                player,
                warp
        );
    }

    @Command({"city setwarp", "ville setwarp"})
    @Description("Déplacer le warp de votre ville")
    void setWarpCommand(Player player) {
        MayorSetWarpAction.setWarp(player);
    }
}
