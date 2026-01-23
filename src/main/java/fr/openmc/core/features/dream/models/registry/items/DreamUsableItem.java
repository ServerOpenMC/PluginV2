package fr.openmc.core.features.dream.models.registry.items;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class DreamUsableItem extends DreamItem {

    /**
     * Creates a new DreamUsableItem with the specified name.
     *
     * @param name The namespaced ID of the item, e.g., "omc_dream:meteo_wand".
     */
    protected DreamUsableItem(String name) {
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

    /**
     * Handles the interaction with the item.
     *
     * @param player The player interacting with the item.
     * @param event  The {@link PlayerInteractEvent} containing the interaction details.
     */
    public final void handleInteraction(Player player, PlayerInteractEvent event) {
        Action action = event.getAction();

        if (player.isSneaking()) {
            onSneakClick(player, event);
        } else if (action.isLeftClick()) {
            onLeftClick(player, event);
        } else if (action.isRightClick()) {
            onRightClick(player, event);
        }
    }

}
