package fr.openmc.core.items.usable;

import fr.openmc.core.items.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class CustomUsableItem extends CustomItem {

    public CustomUsableItem(String name) {
        super(name);
    }

    public void onRightClick(Player player, PlayerInteractEvent event) {}

    public void onLeftClick(Player player, PlayerInteractEvent event) {}

    public void onSneakClick(Player player, PlayerInteractEvent event) {}

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

}
