package fr.openmc.core.features.events.halloween.listeners;

import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import fr.openmc.core.features.events.halloween.menus.HalloweenPumpkinDepositMenu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class HalloweenNPCListener implements Listener {
    private static final String HALLOWEEN_NPC_ID = "halloween_pumpkin_deposit_npc";

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNPCInteract(NpcInteractEvent event) {
        String npcName = event.getNpc().getData().getName();
        if (!npcName.equals(HALLOWEEN_NPC_ID))
            return;

        new HalloweenPumpkinDepositMenu(event.getPlayer()).open();
    }
}
