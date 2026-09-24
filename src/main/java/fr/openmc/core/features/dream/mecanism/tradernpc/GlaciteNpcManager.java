package fr.openmc.core.features.dream.mecanism.tradernpc;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.DreamDimensionManager;
import fr.openmc.core.hooks.FancyNpcsHook;
import fr.openmc.core.lifecycle.integration.OMCLogger;
import fr.openmc.core.lifecycle.interfaces.HasListeners;
import fr.openmc.core.lifecycle.listeners.ListenerFactory;
import fr.openmc.core.registry.features.Feature;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.Set;
import java.util.UUID;

public class GlaciteNpcManager extends Feature implements HasListeners {

    @Override
    public void init() {
        DreamDimensionManager dreamDimensionManager = OMCRegistry.DREAM_FEATURES.DREAM_DIMENSION;

        if (dreamDimensionManager.hasSeedChanged()) {
            OMCLogger.info("[GlaciteNpcManager] Seed changée, reset des trader glacite NPC !");
            // fetch les npcs apres 30 secondes le temps que fancy npc s'initialise.
            Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
                FancyNpcsPlugin.get().getNpcManager().getAllNpcs().forEach(npc -> {
                    if (npc.getData().getName().startsWith("glacite-")) {
                        FancyNpcsPlugin.get().getNpcManager().removeNpc(npc);
                        npc.removeForAll();
                    }
                });
            }, FancyNpcsHook.FANCY_INIT_DELAY);
        }
    }

    @Override
    public Set<ListenerFactory> getListeners() {
        return Set.of(
                GlaciteTraderInteractListener::new
        );
    }

    public void createNPC(Location locationNpc) {
        if (!OMCRegistry.HOOKS.FANCY_NPCS.isEnable()) return;
        UUID npcUUID = UUID.randomUUID();

        NpcData data = new NpcData("glacite-" + npcUUID, null, locationNpc);
        data.setDisplayName("<lang:feature.dream.trader.npc.name>");
        data.setType(EntityType.ILLUSIONER);
        data.setTurnToPlayerDistance(10);
        data.setTurnToPlayer(true);

        Npc npc = FancyNpcsPlugin.get().getNpcAdapter().apply(data);
        FancyNpcsPlugin.get().getNpcManager().registerNpc(npc);
        npc.create();
        npc.spawnForAll();
    }
}
