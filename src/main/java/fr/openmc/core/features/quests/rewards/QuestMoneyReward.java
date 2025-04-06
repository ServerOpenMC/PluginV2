package fr.openmc.core.features.quests.rewards;

import fr.openmc.core.features.economy.EconomyManager;
import lombok.Getter;
import org.bukkit.entity.Player;

public class QuestMoneyReward implements QuestReward {

    @Getter private final double amount;

    public QuestMoneyReward(double amount) {
        this.amount = amount;
    }

    @Override
    public void giveReward(Player player) {
        EconomyManager.getInstance().addBalance(player.getUniqueId(), amount);
    }
}
