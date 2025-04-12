package fr.openmc.core.features.quests.rewards;

import org.bukkit.entity.Player;

/**
 * Interface representing a quest reward.
 * Implementations of this interface should define how to give the reward to a player.
 */
public interface QuestReward {
    /**
     * Gives the reward to the specified player.
     *
     * @param player The player to give the reward to.
     */
    void giveReward(Player player);
}
