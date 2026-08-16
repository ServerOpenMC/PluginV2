package fr.openmc.core.listeners;

import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.types.LoadIfEnable;
import fr.openmc.core.hooks.itemsadder.ItemsAdderHook;
import lombok.Setter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ItemsAddersListener implements Listener, LoadIfEnable<ItemsAdderHook> {
    @Setter
    public static boolean isLoaded = false;

    @EventHandler
    public void onItemsRegistry(ItemsAdderLoadDataEvent event) {
        if (isLoaded) return;

        OMCPlugin.getInstance().loadAfterItemsAdder();
    }

}
