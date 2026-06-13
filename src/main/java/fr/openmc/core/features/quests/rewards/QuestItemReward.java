package fr.openmc.core.features.quests.rewards;

import fr.openmc.core.registry.items.CustomItem;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@Getter
public class QuestItemReward implements QuestReward {
    private final ItemStack itemStack;
    private final int amount;

    /**
     * Create a new QuestItemReward.
     *
     * @param material The material of the item.
     * @param amount   The amount of the item.
     */
    public QuestItemReward(Material material, int amount) {
        this.itemStack = new ItemStack(material);
        this.amount = amount;
    }

    /**
     * Create a new QuestItemReward.
     *
     * @param itemStack The material of the item.
     * @param amount   The amount of the item.
     */
    public QuestItemReward(ItemStack itemStack, int amount) {
        this.itemStack = itemStack;
        this.amount = amount;
    }

    /**
     * Create a new QuestItemReward.
     *
     * @param customItem The material of the item.
     * @param amount   The amount of the item.
     */
    public QuestItemReward(CustomItem customItem, int amount) {
        this.itemStack = customItem.getBest();
        this.amount = amount;
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
        int remaining = amount;
        while (remaining > 0) {
            int stackAmount = Math.min(remaining, itemStack.getMaxStackSize());

            ItemStack item = itemStack.clone();
            item.setAmount(stackAmount);

            Map<Integer, ItemStack> leftOverItems = player.getInventory().addItem(item);
            leftOverItems.forEach((index, stack) -> player.getWorld().dropItem(player.getLocation(), stack));

            remaining -= stackAmount;
        }
    }
}
