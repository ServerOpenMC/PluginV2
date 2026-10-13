package fr.openmc.core.features.quests.rewards;

import fr.openmc.api.omcplayer.OMCPlayer;

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
    void giveReward(OMCPlayer player);
}
