package fr.openmc.core.features.city.sub.mayor.actions;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mayor.ElectionType;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.menu.MayorElectionMenu;
import fr.openmc.core.features.city.sub.mayor.menu.MayorMandateMenu;
import fr.openmc.core.features.city.sub.mayor.menu.create.MayorColorMenu;
import fr.openmc.core.features.city.sub.mayor.menu.create.MayorCreateMenu;
import fr.openmc.core.features.city.sub.mayor.menu.create.MenuType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MayorCommandAction {

    public static void launchInteractionMenu(Player player) {
        City city = CityManager.getPlayerCity(player.getUniqueId());

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
                if (city.hasPermission(player.getUniqueId(), CPermission.OWNER)) {
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
