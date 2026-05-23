package fr.openmc.core.features.homes;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.homes.events.HomeUpgradeEvent;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HomeUpgradeManager {

    public static HomeLimits getCurrentUpgrade(Player player) {
        int currentLimit = HomesManager.getHomeLimit(player.getUniqueId());
        for (HomeLimits upgrade : HomeLimits.values()) {
            if (upgrade.getLimit() == currentLimit) {
                return upgrade;
            }
        }
        return HomeLimits.LIMIT_0;
    }

    public static HomeLimits getNextUpgrade(HomeLimits current) {
        return HomeLimits.values()[current.ordinal() + 1];
    }

    public static void upgradeHome(Player player) {
        int currentHomes = HomesManager.getHomes(player.getUniqueId()).size();
        int currentUpgrade = HomesManager.getHomeLimit(player.getUniqueId());
        HomeLimits nextUpgrade = getNextUpgrade(getCurrentUpgrade(player));
        if(nextUpgrade != null) {
            int price = nextUpgrade.getPrice();
            int ayweniteAmount = nextUpgrade.getAyweniteCost();

            if(currentHomes < currentUpgrade) {
                MessagesManager.sendMessage(
                        player,
                        TranslationManager.translation("feature.homes.upgrade.not_reached_limit"),
                        Prefix.HOME,
                        MessageType.ERROR,
                        true
                );
                return;
            }

            if (!ItemUtils.hasEnoughItems(player, OMCRegistry.CUSTOM_ITEMS.get("omc_items:aywenite").getBest(), ayweniteAmount)) {
                MessagesManager.sendMessage(
                        player,
                        TranslationManager.translation(
                                "feature.homes.upgrade.not_enough_aywenite",
                                Component.text(ayweniteAmount).color(NamedTextColor.LIGHT_PURPLE)
                        ),
                        Prefix.OPENMC,
                        MessageType.ERROR,
                        true
                );
                return;
            }

            if (EconomyManager.getBalance(player.getUniqueId()) < price) {
                MessagesManager.sendMessage(
                        player,
                        TranslationManager.translation(
                                "feature.homes.upgrade.not_enough_money",
                                Component.text(price).color(NamedTextColor.YELLOW),
                                Component.text(EconomyManager.getEconomyIcon())
                        ),
                        Prefix.HOME,
                        MessageType.ERROR,
                        true
                );
                return;
            }

            ItemUtils.takeAywenite(player, ayweniteAmount);
            EconomyManager.withdrawBalance(player.getUniqueId(), price);

            HomesManager.updateHomeLimit(player.getUniqueId());

            int updatedHomesLimit = HomesManager.getHomeLimit(player.getUniqueId());

            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                Bukkit.getPluginManager().callEvent(new HomeUpgradeEvent(player));
            });

            MessagesManager.sendMessage(player,
                    TranslationManager.translation(
                            "feature.homes.upgrade.success",
                            Component.text(updatedHomesLimit).color(NamedTextColor.YELLOW),
                            Component.text(nextUpgrade.getPrice()).color(NamedTextColor.YELLOW),
                            Component.text(ayweniteAmount).color(NamedTextColor.LIGHT_PURPLE)
                    ), Prefix.HOME, MessageType.SUCCESS, true);
        } else {
            MessagesManager.sendMessage(
                    player,
                    TranslationManager.translation("feature.homes.upgrade.max"),
                    Prefix.HOME,
                    MessageType.ERROR,
                    true
            );
        }
    }
}
