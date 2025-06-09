package fr.openmc.core.features.city.sub.mayor.commands;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.actions.MayorSetWarpAction;
import fr.openmc.core.features.city.sub.mayor.CityLaw;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.menu.MayorElectionMenu;
import fr.openmc.core.features.city.sub.mayor.menu.MayorMandateMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class MayorCommands {
    @Subcommand({"city mayor", "ville maire"})
    @CommandPermission("omc.commands.city.mayor")
    @Description("Ouvre le menu des maires")
    void mayor(Player sender) {
        City playerCity = CityManager.getPlayerCity(sender.getUniqueId());

        if (playerCity == null) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
        }

        if (MayorManager.getInstance().phaseMayor == 1) {
            MayorElectionMenu menu = new MayorElectionMenu(sender);
            menu.open();
        } else {
            MayorMandateMenu menu = new MayorMandateMenu(sender);
            menu.open();
        }
    }

    @Subcommand({"city warp", "ville warp"})
    @Description("Teleporte au warp commun de la ville")
    void warp(Player player) {
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

        if (playerCity == null) return;

        CityLaw law = playerCity.getLaw();
        Location warp = law.getWarp();

        if (warp == null) {
            if (MayorManager.getInstance().phaseMayor == 2) {
                MessagesManager.sendMessage(player, Component.text("Le Warp de la Ville n'est pas encore défini ! Demandez au §6Maire §fActuel d'en mettre un ! §8§o*via /city setwarp ou avec le Menu des Lois*"), Prefix.CITY, MessageType.INFO, true);
                return;
            }
            MessagesManager.sendMessage(player, Component.text("Le Warp de la Ville n'est pas encore défini ! Vous devez attendre que un Maire soit élu pour mettre un Warp"), Prefix.CITY, MessageType.INFO, true);
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                player.sendTitle(PlaceholderAPI.setPlaceholders(player, "§0%img_tp_effect%"), "§a§lTéléportation...", 20, 10, 10);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.teleport(warp);
                        MessagesManager.sendMessage(player, Component.text("Vous avez été envoyé au Warp §fde votre §dVille"), Prefix.CITY, MessageType.SUCCESS, true);
                    }
                }.runTaskLater(OMCPlugin.getInstance(), 10);
            }
        }.runTaskLater(OMCPlugin.getInstance(), 15);
    }

    @Subcommand({"city setwarp", "ville setwarp"})
    @Description("Déplacer le warp de votre ville")
    void setWarpCommand(Player player) {
        MayorSetWarpAction.setWarp(player);
    }
}
