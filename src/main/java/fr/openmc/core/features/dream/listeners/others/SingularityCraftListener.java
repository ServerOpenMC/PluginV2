package fr.openmc.core.features.dream.listeners.others;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.features.mailboxes.MailboxManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class SingularityCraftListener implements Listener {
    @EventHandler
    public void onCraft(CraftItemEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) return;

        DreamItem dreamItem = DreamItemRegistry.getByItemStack(item);
        if (dreamItem == null) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (dreamItem.getName().equals("omc_dream:singularity")) {
            MailboxManager.sendItems(player, player, new ItemStack[] { dreamItem.getBest() });
        }
    }
}
