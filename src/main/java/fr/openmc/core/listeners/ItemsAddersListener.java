package fr.openmc.core.listeners;

import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import fr.openmc.core.features.milestones.MilestonesManager;
import fr.openmc.core.features.quests.QuestsManager;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.items.usable.CustomUsableItemRegistry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ItemsAddersListener implements Listener
{

    @EventHandler
    public void onItemsRegistry(ItemsAdderLoadDataEvent event)
    {
        if (!event.getCause().equals(ItemsAdderLoadDataEvent.Cause.FIRST_LOAD))
            return;

        new CustomItemRegistry();
        new CustomUsableItemRegistry();
        new MilestonesManager();
        new QuestsManager();
    }

}
