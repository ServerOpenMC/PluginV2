package fr.openmc.core.features.homes;

import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class HomeUpgradeManager {

    private final HomesManager homesManager;
    @Getter public static HomeUpgradeManager instance;

    public HomeUpgradeManager(HomesManager homesManager) {
        this.homesManager = homesManager;
        this.instance = this;
    }

    public HomeLimits getCurrentUpgrade(Player player) {
        int currentLimit = homesManager.getHomeLimit(player.getUniqueId());
        for (HomeLimits upgrade : HomeLimits.values()) {
            if (upgrade.getLimit() == currentLimit) {
                return upgrade;
            }
        }
        return HomeLimits.LIMIT_0;
    }

    public HomeLimits getNextUpgrade(HomeLimits current) {
        return HomeLimits.values()[current.ordinal() + 1];
    }

    public void upgradeHome(Player player) {
        int currentHomes = homesManager.getHomes(player.getUniqueId()).size();
        int currentUpgrade = homesManager.getHomeLimit(player.getUniqueId());
        HomeLimits nextUpgrade = getNextUpgrade(getCurrentUpgrade(player));

        if(nextUpgrade != null) {
            double balance = EconomyManager.getInstance().getBalance(player.getUniqueId());
            int price = nextUpgrade.getPrice();

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

            if(balance >= price) {
                EconomyManager.getInstance().withdrawBalance(player.getUniqueId(), price);
                homesManager.updateHomeLimit(player.getUniqueId());

                int updatedHomesLimit = homesManager.getHomeLimit(player.getUniqueId());


                MessagesManager.sendMessage(player, Component.text("§aVous avez amélioré votre limite de homes à " + updatedHomesLimit + " pour " + nextUpgrade.getPrice() + "$."), Prefix.HOME, MessageType.SUCCESS, true);
            } else {
                MessagesManager.sendMessage(
                        player,
                        Component.text("§cVous n'avez pas assez d'argent pour acheter cette amélioration."),
                        Prefix.HOME,
                        MessageType.ERROR,
                        true
                );
            }
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
