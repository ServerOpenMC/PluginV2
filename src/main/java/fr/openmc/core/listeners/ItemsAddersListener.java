package fr.openmc.core.listeners;

import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import fr.openmc.core.OMCPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ItemsAddersListener implements Listener {

    @EventHandler
    public void onItemsRegistry(ItemsAdderLoadDataEvent event) {
        if (event.getCause().equals(ItemsAdderLoadDataEvent.Cause.FIRST_LOAD))
            OMCPlugin.getInstance().loadWithItemsAdder();
    }

}
