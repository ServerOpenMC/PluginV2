package fr.openmc.core.features.quests.rewards;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

@Getter
public class QuestMethodsReward implements QuestReward {
    private final Consumer<Player> runnable;

    /**
     * Create a new QuestItemReward.
     *
     * @param runnable The runnable
     */
    public QuestMethodsReward(Consumer<Player> runnable) {
        this.runnable = runnable;
    }

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
