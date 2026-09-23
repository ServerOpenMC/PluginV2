package fr.openmc.core.features.city.sub.mayor.actions;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.models.CityPermission;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.sub.mayor.ElectionType;
import fr.openmc.core.features.city.sub.mayor.menu.MayorElectionMenu;
import fr.openmc.core.features.city.sub.mayor.menu.MayorMandateMenu;
import fr.openmc.core.features.city.sub.mayor.menu.create.MayorColorMenu;
import fr.openmc.core.features.city.sub.mayor.menu.create.MayorCreateMenu;
import fr.openmc.core.features.city.sub.mayor.menu.create.MenuType;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MayorCommandAction {

    public static void launchInteractionMenu(Player player) {
        City city = City.ofPlayer(player);

        if (city == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.MAYOR)) {
            MessagesManager.sendMessage(player, TranslationManager.translation(
                    "feature.city.mayor.error.feature_locked",
                    Component.text(FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.MAYOR)).color(NamedTextColor.GOLD)
            ), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (city.getElectionType() == ElectionType.ELECTION) {
            if (OMCRegistry.CITY_FEATURES.MAYOR.phaseMayor == 1) {
                MayorElectionMenu menu = new MayorElectionMenu(player);
                menu.open();
            } else {
                MayorMandateMenu menu = new MayorMandateMenu(player);
                menu.open();
            }
        } else {
            if (OMCRegistry.CITY_FEATURES.MAYOR.phaseMayor == 2) {
                MayorMandateMenu menu = new MayorMandateMenu(player);
                menu.open();
            } else if (OMCRegistry.CITY_FEATURES.MAYOR.phaseMayor == 1) {
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
