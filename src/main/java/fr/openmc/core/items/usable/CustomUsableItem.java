package fr.openmc.core.items.usable;

import fr.openmc.core.items.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class CustomUsableItem extends CustomItem {

    /**
     * Creates a new CustomUsableItem with the specified name.
     *
     * @param name The namespaced ID of the item, e.g., "omc_items:iron_hammer".
     */
    public CustomUsableItem(String name) {
        super(name);
        CustomUsableItemRegistry.register(this);
    }

    /**
     * Event called when the player right-clicks with this item.
     *
     * @param player
     * @param event
     */
    public void onRightClick(Player player, PlayerInteractEvent event) {}

    /**
     * Event called when the player left-clicks with this item.
     *
     * @param player
     * @param event
     */
    public void onLeftClick(Player player, PlayerInteractEvent event) {}

    /**
     * Event called when the player sneaks and clicks with this item.
     *
     * @param player
     * @param event
     */
    public void onSneakClick(Player player, PlayerInteractEvent event) {}

    public void onBlockBreak(Player player, BlockBreakEvent event) {}

    /**
     * Handles the interaction with the item.
     *
     * @param player The player interacting with the item.
     * @param event  The PlayerInteractEvent containing the interaction details.
     */
    public final void handleInteraction(Player player, PlayerInteractEvent event) {
        Action action = event.getAction();

        if (player.isSneaking()) {
            onSneakClick(player, event);
            return;
        }

        switch (action) {
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                onRightClick(player, event);
                break;
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                onLeftClick(player, event);
                break;
        }
    }

    public final void handleBlockBreak(Player player, BlockBreakEvent event) {
        onBlockBreak(player, event);
    }

}
