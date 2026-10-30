package fr.openmc.core.features.events.contents.halloween.festival.npc;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.data.property.NpcVisibility;
import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.events.contents.halloween.festival.HalloweenFestivalManager;
import fr.openmc.core.features.events.contents.halloween.festival.shop.menu.WitchShopMainMenu;
import fr.openmc.core.hooks.FancyNpcsHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WitchNPCS implements Listener {

    public static List<Npc> witchNPCS = new ArrayList<>();

    private static final Sound WITCH_MENU_OPEN = Sound.ENTITY_WITCH_CELEBRATE;

    public WitchNPCS() {
        // fetch les npcs apres 30 secondes le temps que fancy npc s'initialise.
        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            FancyNpcsPlugin.get().getNpcManager().getAllNpcs().forEach(npc -> {
                if (npc.getData().getName().startsWith("witch-")) {
                    witchNPCS.add(npc);
                }
            });
        }, 20L * 30);
    }

    public static boolean createNPC(Location location) {
        if (!FancyNpcsHook.isEnable()) return false;

        UUID uuid = UUID.randomUUID();

        NpcData dataCorpse = new NpcData("witch-" + uuid, uuid, location);

        dataCorpse.setType(EntityType.WITCH);

        dataCorpse.setDisplayName("<dark_purple><lang:feature.events.halloween.festival.npc.name.0:></dark_purple>");

        dataCorpse.setVisibility(NpcVisibility.ALL);

        Npc npcWitch = FancyNpcsPlugin.get().getNpcAdapter().apply(dataCorpse);

        FancyNpcsPlugin.get().getNpcManager().registerNpc(npcWitch);

        npcWitch.create();

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), npcWitch::spawnForAll);

        witchNPCS.add(npcWitch);

        return true;
    }

    public static void removeNPCS() {
        if (!FancyNpcsHook.isEnable()) return;

        witchNPCS.forEach(npc -> FancyNpcsPlugin.get().getNpcManager().removeNpc(npc));
        witchNPCS.clear();
    }

    @EventHandler
    public void onNPCInteract(NpcInteractEvent event) {
        if (!event.getNpc().getData().getName().startsWith("witch-")) return;

        if (!HalloweenFestivalManager.isActive())
            return;

        Player player = event.getPlayer();

        player.playSound(player, WITCH_MENU_OPEN, 1f, 1f);

        new WitchShopMainMenu(player).open();
    }
}
