package fr.openmc.core.features.dream.mecanism.tradernpc;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import fr.openmc.api.hooks.FancyNpcsHook;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;

import java.util.UUID;

public class GlaciteNpcManager implements Listener {
    public static void init() {
        OMCPlugin.registerEvents(
                new GlaciteTraderInteractListener()
        );
        if (DreamDimensionManager.hasSeedChanged()) {
            OMCPlugin.getInstance().getSLF4JLogger().info("[GlaciteNpcManager] Seed changée, reset des trader glacite NPC !");
            // fetch les npcs apres 30 secondes le temps que fancy npc s'initialise.
            Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
                FancyNpcsPlugin.get().getNpcManager().getAllNpcs().forEach(npc -> {
                    if (npc.getData().getName().startsWith("glacite-")) {
                        FancyNpcsPlugin.get().getNpcManager().removeNpc(npc);
                        npc.removeForAll();
                    }
                });
            }, 20L * 30);
        }
    }

    public static void createNPC(Location locationNpc) {
        if (!FancyNpcsHook.isHasFancyNpc()) return;
        UUID npcUUID = UUID.randomUUID();

        NpcData data = new NpcData("glacite-" + npcUUID, null, locationNpc);
        data.setDisplayName("§bVagabond Glacial");
        data.setType(EntityType.ILLUSIONER);
        data.setTurnToPlayerDistance(10);
        data.setTurnToPlayer(true);

        Npc npc = FancyNpcsPlugin.get().getNpcAdapter().apply(data);
        FancyNpcsPlugin.get().getNpcManager().registerNpc(npc);
        npc.create();
        npc.spawnForAll();
    }
}