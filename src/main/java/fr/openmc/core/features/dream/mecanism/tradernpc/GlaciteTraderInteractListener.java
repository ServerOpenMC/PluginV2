package fr.openmc.core.features.dream.mecanism.tradernpc;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import fr.openmc.api.hooks.FancyNpcsHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GlaciteTraderInteractListener implements Listener {
    @EventHandler
    public void onInteract(NpcInteractEvent event) {
        if (!FancyNpcsHook.isHasFancyNpc()) return;

        Player player = event.getPlayer();

        Npc npc = event.getNpc();

        if (!npc.getData().getName().startsWith("glacite-")) return;

        new GlaciteTradeMenu(player).open();
    }
}
