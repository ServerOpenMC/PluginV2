package fr.openmc.core.features.city.sub.mayor.actions;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.sub.mayor.ElectionType;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.menu.MayorElectionMenu;
import fr.openmc.core.features.city.sub.mayor.menu.MayorMandateMenu;
import fr.openmc.core.features.city.sub.mayor.menu.create.MayorColorMenu;
import fr.openmc.core.features.city.sub.mayor.menu.create.MayorCreateMenu;
import fr.openmc.core.features.city.sub.mayor.menu.create.MenuType;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MayorCommandAction {

    public static void launchInteractionMenu(Player player) {
        City city = CityManager.getPlayerCity(player.getUniqueId());

        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.MAYOR)) {
	        MessagesManager.sendMessage(player, Component.text("Vous n'avez pas débloqué cette feature ! Veuillez améliorer votre ville au niveau " + FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.MAYOR) + " !"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (city.getElectionType() == ElectionType.ELECTION) {
            if (MayorManager.phaseMayor == 1) {
                MayorElectionMenu menu = new MayorElectionMenu(player);
                menu.open();
            } else {
                MayorMandateMenu menu = new MayorMandateMenu(player);
                menu.open();
            }
        } else {
            if (MayorManager.phaseMayor == 2) {
                MayorMandateMenu menu = new MayorMandateMenu(player);
                menu.open();
            } else if (MayorManager.phaseMayor == 1) {
                if (city.hasPermission(player.getUniqueId(), CityPermission.OWNER)) {
                    if (!city.hasMayor()) {
                        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                            new MayorCreateMenu(player, null, null, null, MenuType.OWNER).open();
                        });
                    } else {
                        new MayorColorMenu(player, null, null, null, "change", null).open();
                    }

                }
            }
        }
    }
}
