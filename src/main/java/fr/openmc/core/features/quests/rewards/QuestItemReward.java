package fr.openmc.core.features.quests.rewards;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class QuestItemReward implements QuestReward {
    private final ItemStack itemStack;

    /**
     * Create a new QuestItemReward.
     *
     * @param material The material of the item.
     * @param amount   The amount of the item.
     */
    public QuestItemReward(Material material, int amount) {
        this.itemStack = new ItemStack(material, amount);
    }

    /**
     * Create a new QuestItemReward.
     *
     * @param material The material of the item.
     * @param amount   The amount of the item.
     */
    public QuestItemReward(ItemStack material, int amount) {
        this.itemStack = material;
        this.itemStack.setAmount(amount);
    }

    /**
     * Give the reward to the player.
     * <p>
     * If  the player's inventory is full, the item will be dropped on the ground.
     * @param player The player to give the reward to.
     */
    @Override
    public void giveReward(Player player) {
        ItemStack item = itemStack.clone();
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(item);
        } else {
            player.getWorld().dropItem(player.getLocation(), item);
        }
    }
}
