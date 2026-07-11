package fr.openmc.core.utils.bukkit;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {
	
	public static int removeItemsFromInventory(Inventory inventory, ItemStack item, int amountToRemove) {
		if (item == null || amountToRemove <= 0) return 0;
		
		int removed = 0;
		ItemStack[] contents = inventory.getContents();
		
		for (int i = 0; i < contents.length && removed < amountToRemove; i++) {
			ItemStack stack = contents[i];
			if (stack == null) continue;
			
			if (ItemUtils.isSimilar(stack, item)) {
				int stackAmount = stack.getAmount();
				int toRemove = Math.min(amountToRemove - removed, stackAmount);
				
				removed += toRemove;
				
				if (stackAmount <= toRemove) {
					inventory.setItem(i, null);
				} else {
					stack.setAmount(stackAmount - toRemove);
				}
			}
		}
		
		return removed;
	}
	
}
