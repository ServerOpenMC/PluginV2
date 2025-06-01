package fr.openmc.core.features.quests.rewards;

import fr.openmc.core.features.adminshop.AdminShopManager;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
     * @param material The material of the item.
     * @param amount   The amount of the item.
     */
    public QuestItemReward(ItemStack material, int amount) {
        this.itemStack = material;
        this.amount = amount;
    }

    /**
     * Give the reward to the player.
     * <p>
     * If  the player's inventory is full, the item will be dropped on the ground.
     * @param player The player to give the reward to.
     */
    @Override
    public void giveReward(Player player) {
        int remaining = amount;
        while (remaining > 0) {
            int stackAmount = Math.min(remaining, itemStack.getMaxStackSize());

            ItemStack item = itemStack.clone();
            item.setAmount(stackAmount);

            if (AdminShopManager.hasEnoughSpace(player, item)) {
                player.getInventory().addItem(item);
            } else {
                player.getWorld().dropItem(player.getLocation(), item);
            }

            remaining -= stackAmount;
        }
    }
}
