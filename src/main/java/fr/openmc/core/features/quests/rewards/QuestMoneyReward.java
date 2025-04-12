package fr.openmc.core.features.quests.rewards;

import fr.openmc.core.features.economy.EconomyManager;
import lombok.Getter;
import org.bukkit.entity.Player;

/**
 * Class representing a money reward for a quest.
 * <p>
 * This class implements the QuestReward interface and provides functionality to give a specified amount of money to a player.
 */
public class QuestMoneyReward implements QuestReward {

    @Getter private final double amount;

    /**
     * Constructor for the QuestMoneyReward class.
     *
     * @param amount The amount of money to be rewarded.
     */
    public QuestMoneyReward(double amount) {
        this.amount = amount;
    }

    /**
     * Gives the specified amount of money to the player.
     *
     * @param player The player to whom the reward will be given.
     */
    @Override
    public void giveReward(Player player) {
        EconomyManager.getInstance().addBalance(player.getUniqueId(), amount);
    }
}
