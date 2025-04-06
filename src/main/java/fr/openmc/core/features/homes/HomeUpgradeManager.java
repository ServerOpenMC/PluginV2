package fr.openmc.core.features.homes;

import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
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
        Material matAywenite = CustomItemRegistry.getByName("omc_items:aywenite").getBest().getType();
        if(nextUpgrade != null) {
            double balance = EconomyManager.getInstance().getBalance(player.getUniqueId());
            int price = nextUpgrade.getPrice();
            int aywenite = nextUpgrade.getAyweniteCost();

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
                if (!ItemUtils.hasEnoughItems(player, matAywenite, aywenite)) {
                    MessagesManager.sendMessage(player, Component.text("Vous n'avez pas assez d'§dAywenite §f("+aywenite+ " nécessaires)"), Prefix.HOME, MessageType.ERROR, false);
                    return;
                }

                ItemUtils.removeItemsFromInventory(player, matAywenite, aywenite);
                EconomyManager.getInstance().withdrawBalance(player.getUniqueId(), price);
                homesManager.updateHomeLimit(player.getUniqueId());

                int updatedHomesLimit = homesManager.getHomeLimit(player.getUniqueId());


                MessagesManager.sendMessage(player, Component.text("§aVous avez amélioré votre limite de homes à " + updatedHomesLimit + " pour " + nextUpgrade.getPrice() + "$ et à §d" + aywenite + " d'Aywenite"), Prefix.HOME, MessageType.SUCCESS, true);
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
