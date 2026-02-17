package fr.openmc.core.registry.usableitems;

import fr.openmc.core.registry.items.CustomItem;
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
    protected CustomUsableItem(String name) {
        super(name);
    }

    /**
     * Event called when the player right-clicks with this item.
     *
     * @param player The player who performed the right-click.
     * @param event  The {@link PlayerInteractEvent} representing the click.
     */
    public void onRightClick(Player player, PlayerInteractEvent event) {
    }

    /**
     * Event called when the player left-clicks with this item.
     *
     * @param player The player who performed the left-click.
     * @param event  The {@link PlayerInteractEvent} representing the click.
     */
    public void onLeftClick(Player player, PlayerInteractEvent event) {
    }

    /**
     * Event called when the player sneaks and clicks with this item.
     *
     * @param player The player who is sneaking and performed the click.
     * @param event  The {@link PlayerInteractEvent} representing the click.
     */
    public void onSneakClick(Player player, PlayerInteractEvent event) {
    }

    public void onBlockBreak(Player player, BlockBreakEvent event) {
    }

    /**
     * Handles the interaction with the item.
     *
     * @param player The player interacting with the item.
     * @param event  The {@link PlayerInteractEvent} containing the interaction details.
     */
    public final void handleInteraction(Player player, PlayerInteractEvent event) {
        Action action = event.getAction();

        if (player.isSneaking()) onSneakClick(player, event);
        else if (action.isLeftClick()) onLeftClick(player, event);
        else if (action.isRightClick()) onRightClick(player, event);
    }

    public final void handleBlockBreak(Player player, BlockBreakEvent event) {
        onBlockBreak(player, event);
    }

}
