package fr.openmc.core.listeners;

import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.registry.items.options.UsableItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InteractListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.useInteractedBlock() == Event.Result.DENY) return;

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        CustomItem item = CustomItemRegistry.getByItemStack(itemInHand);

        if (item == null) return;

        if (item instanceof UsableItem usable) {
            Action action = event.getAction();

            if (player.isSneaking()) usable.onSneakClick(player, event);
            else if (action.isLeftClick()) usable.onLeftClick(player, event);
            else if (action.isRightClick()) usable.onRightClick(player, event);
        }
    }

}
