package fr.openmc.core.listeners;

import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import fr.openmc.core.OMCPlugin;
import lombok.Setter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ItemsAddersListener implements Listener {
    @Setter
    public static boolean isLoaded = false;

    @EventHandler
    public void onItemsRegistry(ItemsAdderLoadDataEvent event) {
        if (isLoaded) return;

        OMCPlugin.getInstance().loadAfterItemsAdder();
    }

}
