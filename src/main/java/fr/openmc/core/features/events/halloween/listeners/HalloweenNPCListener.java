package fr.openmc.core.features.events.halloween.listeners;

import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import fr.openmc.core.features.events.halloween.menus.PumpkinDepositMenu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class HalloweenNPCListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNPCInteract(NpcInteractEvent event) {
        String npcID = event.getNpc().getData().getId();
        if (!npcID.equals("halloween_pumpkin_deposit_npc"))
            return;

        new PumpkinDepositMenu(event.getPlayer()).open();
    }
}
