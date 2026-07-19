package fr.openmc.core.listeners;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.items.options.LootboxBlock;
import fr.openmc.core.registry.items.options.UsableItem;
import fr.openmc.core.utils.bukkit.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class InteractListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        Optional<CustomItem> item = OMCRegistry.CUSTOM_ITEMS.get(itemInHand);

        if (item.isEmpty()) return;

        if (item.get() instanceof UsableItem usable) {
            Action action = event.getAction();

            if (player.isSneaking()) usable.onSneakClick(player, event);
            else if (action.isLeftClick()) usable.onLeftClick(player, event);
            else if (action.isRightClick()) usable.onRightClick(player, event);
        } else if (item.get() instanceof LootboxBlock lootbox
                && event.getAction() == Action.RIGHT_CLICK_AIR) {
            ItemUtils.removeItemsFromPlayerInventory(player, item.get().getBest(), 1);
            lootbox.getLootbox().open(player);
        }
    }

}
