package fr.openmc.core.features.quests.rewards;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class QuestItemReward implements QuestReward {

    private final Material material;
    private final int amount;

    public QuestItemReward(Material material, int amount) {
        this.material = material;
        this.amount = amount;
    }

    @Override
    public void giveReward(Player player) {
        ItemStack item = new ItemStack(material, amount);
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(item);
        } else {
            player.getWorld().dropItem(player.getLocation(), item);
        }
    }
}
