package fr.openmc.core.features.homes;

import fr.openmc.api.entity.player.OMCPlayer;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.homes.events.HomeUpgradeEvent;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;

public class HomeUpgradeManager {

    public static HomeLimits getCurrentUpgrade(OMCPlayer player) {
        int currentLimit = player.home().getHomeLimit().getLimit();
        for (HomeLimits upgrade : HomeLimits.values()) {
            if (upgrade.getLimit() == currentLimit) {
                return upgrade;
            }
        }
        return HomeLimits.LIMIT_0;
    }

    public static HomeLimits getNextUpgrade(HomeLimits current) {
        if (current == null)
            return null;

        HomeLimits[] values = HomeLimits.values();
        int nextIndex = current.ordinal() + 1;
        if (nextIndex >= values.length)
            return null;

        return values[nextIndex];
    }

    public static void upgradeHome(OMCPlayer player) {
        int currentHomes = player.home().getHomes().size();
        int currentUpgrade = player.home().getHomeLimit().getLimit();
        HomeLimits nextUpgrade = getNextUpgrade(getCurrentUpgrade(player));
        if (nextUpgrade != null) {
            int price = nextUpgrade.getPrice();
            int ayweniteAmount = nextUpgrade.getAyweniteCost();

            if (currentHomes < currentUpgrade) {
                player.message().sendError(
                        TranslationManager.translation("feature.homes.upgrade.not_reached_limit"),
                        Prefix.HOME,
                        true
                );
                return;
            }

            if (!ItemUtils.hasEnoughItems(player, OMCRegistry.CUSTOM_ITEMS.AYWENITE.getBest(), ayweniteAmount)) {
                player.message().sendError(
                        TranslationManager.translation(
                                "feature.homes.upgrade.not_enough_aywenite",
                                Component.text(ayweniteAmount).color(NamedTextColor.LIGHT_PURPLE)
                        ),
                        Prefix.OPENMC,
                        true
                );
                return;
            }

            if (EconomyManager.getBalance(player.getUniqueId()) < price) {
                player.message().sendError(
                        TranslationManager.translation(
                                "feature.homes.upgrade.not_enough_money",
                                Component.text(price).color(NamedTextColor.YELLOW),
                                Component.text(EconomyManager.getEconomyIcon())
                        ),
                        Prefix.HOME,
                        true
                );
                return;
            }

            ItemUtils.takeAywenite(player, ayweniteAmount);
            EconomyManager.withdrawBalance(player.getUniqueId(), price);

            player.home().updateHomeLimit();

            int updatedHomesLimit = player.home().getHomeLimit().getLimit();

            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                Bukkit.getPluginManager().callEvent(new HomeUpgradeEvent(player));
            });

            player.message().sendSuccess(
                    TranslationManager.translation(
                            "feature.homes.upgrade.success",
                            Component.text(updatedHomesLimit).color(NamedTextColor.YELLOW),
                            Component.text(nextUpgrade.getPrice()).color(NamedTextColor.YELLOW),
                            Component.text(ayweniteAmount).color(NamedTextColor.LIGHT_PURPLE)
                    ), Prefix.HOME, true);
        } else {
            player.message().sendError(
                    TranslationManager.translation("feature.homes.upgrade.max"),
                    Prefix.HOME,
                    true
            );
        }
    }
}
