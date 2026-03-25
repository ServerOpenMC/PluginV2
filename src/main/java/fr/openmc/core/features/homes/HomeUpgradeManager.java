package fr.openmc.core.features.homes;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.homes.events.HomeUpgradeEvent;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

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
                        Component.text("§cVous n'avez pas atteint la limite de homes pour acheter cette amélioration."),
                        Prefix.HOME,
                        MessageType.ERROR,
                        true
                );
                return;
            }

            if (!ItemUtils.hasEnoughItems(player, Objects.requireNonNull(CustomItemRegistry.getByName("omc_items:aywenite")).getBest(), ayweniteAmount)) {
                MessagesManager.sendMessage(
                        player,
                        Component.text("Vous n'avez pas assez d'§dAywenite §f(" + ayweniteAmount + " nécessaires)"),
                        Prefix.OPENMC,
                        MessageType.ERROR,
                        true
                );
                return;
            }

            if (EconomyManager.getBalance(player.getUniqueId()) < price) {
                MessagesManager.sendMessage(
                        player,
                        Component.text("Tu n'as pas assez d'argent sur toi ! (" + price + EconomyManager.getEconomyIcon() + " nécessaires)"),
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
                    Component.text("§aVous avez amélioré votre limite de homes à " + updatedHomesLimit + " pour " + nextUpgrade.getPrice() + "$ et à §d" + ayweniteAmount + " d'Aywenite"), Prefix.HOME, MessageType.SUCCESS, true);
        } else {
            MessagesManager.sendMessage(
                    player,
                    Component.text("§cVous avez atteint la limite maximale de homes."),
                    Prefix.HOME,
                    MessageType.ERROR,
                    true
            );
        }
    }
}
