package fr.openmc.core.features.quests.rewards;

import fr.openmc.api.omcplayer.OMCPlayer;

import java.util.function.Consumer;

public record QuestMethodsReward(Consumer<OMCPlayer> runnable) implements QuestReward {
    /**
     * Gives the reward to the specified player.
     * <p>
     * The reward is split into stacks no larger than the item's maximum stack size.
     * If the player's inventory has enough space, each stack is added to the inventory.
     * Otherwise, any stack that cannot be fully accommodated is dropped at the player's location.
     *
     * @param player the target player for the reward.
     */
    @Override
    public void giveReward(OMCPlayer player) {
        runnable.accept(player);
    }
}
