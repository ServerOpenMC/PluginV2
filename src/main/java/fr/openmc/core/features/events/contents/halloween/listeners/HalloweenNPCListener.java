package fr.openmc.core.features.events.contents.halloween.listeners;

import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import fr.openmc.core.bootstrap.features.types.LoadIfEnable;
import fr.openmc.core.features.events.contents.halloween.menus.HalloweenPumpkinDepositMenu;
import fr.openmc.core.hooks.FancyNpcsHook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class HalloweenNPCListener implements Listener, LoadIfEnable<FancyNpcsHook> {
    private static final String HALLOWEEN_NPC_ID = "halloween_pumpkin_deposit_npc";

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNPCInteract(NpcInteractEvent event) {
        String npcName = event.getNpc().getData().getName();
        if (!npcName.equals(HALLOWEEN_NPC_ID))
            return;

        new HalloweenPumpkinDepositMenu(event.getPlayer()).open();
    }
}
