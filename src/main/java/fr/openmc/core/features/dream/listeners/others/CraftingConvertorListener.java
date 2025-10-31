package fr.openmc.core.features.dream.listeners.others;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.models.registry.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
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

public class CraftingConvertorListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onCraft(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();
        if (recipe == null) return;

        if (event.getViewers().isEmpty()) return;
        Player player = (Player) event.getViewers().getFirst();

        if (!DreamUtils.isInDreamWorld(player)) return;

        if (recipe instanceof Keyed keyed) {
            NamespacedKey key = keyed.getKey();
            String keyStr = key.toString();
            if (keyStr.contains("omc_dream")) {
                String namespace = key.getNamespace();
                String rawKey = key.getKey();
                String formatKey = rawKey.replaceAll("_\\d+$", "");
                String formatIaKey = namespace + ":" + formatKey;

                DreamItem dreamItem = DreamItemRegistry.getByName(formatIaKey);

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
