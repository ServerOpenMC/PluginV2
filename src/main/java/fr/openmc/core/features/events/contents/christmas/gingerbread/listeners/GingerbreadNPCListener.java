package fr.openmc.core.features.events.contents.christmas.gingerbread.listeners;

import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import fr.openmc.core.bootstrap.features.types.LoadIfEnable;
import fr.openmc.core.features.events.contents.christmas.gingerbread.menus.GingerbreadDepositMenu;
import fr.openmc.core.hooks.FancyNpcsHook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class GingerbreadNPCListener implements Listener, LoadIfEnable<FancyNpcsHook> {
    private static final String GINGERBREAD_NPC_ID = "gingerbread_pumpkin_deposit_npc";

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNPCInteract(NpcInteractEvent event) {
        String npcName = event.getNpc().getData().getName();
        if (!npcName.equals(GINGERBREAD_NPC_ID))
            return;

        new GingerbreadDepositMenu(event.getPlayer()).open();
    }
}
