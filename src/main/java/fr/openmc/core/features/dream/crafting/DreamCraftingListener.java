package fr.openmc.core.features.dream.crafting;

import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import fr.openmc.core.features.dream.items.DreamItem;
import fr.openmc.core.features.dream.items.DreamItemRegister;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class DreamCraftingListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onCraft(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();
        if (recipe == null) return;

        if (event.getViewers().isEmpty()) return;
        Player player = (Player) event.getViewers().getFirst();

        if (!player.getWorld().getName().equals(DreamDimensionManager.DIMENSION_NAME)) return;

        if (recipe instanceof Keyed keyed) {
            NamespacedKey key = keyed.getKey();
            String keyStr = key.toString();
            if (keyStr.contains("omc_dream")) {
                String namespace = key.getNamespace();
                String rawKey = key.getKey();
                String formatKey = rawKey.replaceAll("_\\d+$", "");
                String formatIaKey = namespace + ":" + formatKey;

                DreamItem dreamItem = DreamItemRegister.getByName(formatIaKey);

                if (dreamItem != null) {
                    event.getInventory().setResult(dreamItem.getBest());
                } else {
                    event.getInventory().setResult(null);
                }
                return;
            }
        }

        event.getInventory().setResult(new ItemStack(Material.AIR));
    }
}
