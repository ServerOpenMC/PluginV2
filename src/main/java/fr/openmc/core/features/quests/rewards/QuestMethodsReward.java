package fr.openmc.core.features.quests.rewards;

import org.bukkit.entity.Player;

import java.util.function.Consumer;

public record QuestMethodsReward(Consumer<Player> runnable) implements QuestReward {
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
    public void giveReward(Player player) {
        runnable.accept(player);
    }
}
