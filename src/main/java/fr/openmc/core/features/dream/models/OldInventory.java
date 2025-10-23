package fr.openmc.core.features.dream.models;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public record OldInventory(ItemStack[] oldContents, ItemStack[] oldArmor, ItemStack[] oldExtra) {

    public void restoreOldInventory(Player player) {
        player.getInventory().setContents(oldContents);
        player.getInventory().setArmorContents(oldArmor);
        player.getInventory().setExtraContents(oldExtra);
        player.updateInventory();
    }
}
