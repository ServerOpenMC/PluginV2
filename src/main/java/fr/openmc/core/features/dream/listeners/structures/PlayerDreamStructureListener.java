package fr.openmc.core.features.dream.listeners.structures;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.events.PlayerEnterStructureEvent;
import fr.openmc.core.features.dream.events.PlayerExitStructureEvent;
import fr.openmc.core.features.dream.registries.DreamStructure;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.generator.structure.GeneratedStructure;

public class PlayerDreamStructureListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (!DreamUtils.isInDreamWorld(player)) return;

        Location from = e.getFrom();
        Location to = e.getTo();
        if ((from.getBlockX() >> 4) == (to.getBlockX() >> 4)
                && (from.getBlockY() >> 4) == (to.getBlockY() >> 4)
                && (from.getBlockZ() >> 4) == (to.getBlockZ() >> 4)) {
            return;
        }

        DreamStructure oldStructure = DreamStructure.getDreamStructureAt(from);
        DreamStructure newStructure = DreamStructure.getDreamStructureAt(to);
        if (oldStructure == newStructure) return;

        // ** Détéction Sortie/Entrée
        if (oldStructure != null && newStructure == null) {
            // ** Sortie
            GeneratedStructure generatedStructure = from.getWorld().getStructures(
                                    from.getChunk().getX(),
                                    from.getChunk().getZ()
                            ).stream().findFirst().orElse(null);

            MessagesManager.sendMessage(player, Component.text("§7Vous sortez de : ").append(oldStructure.getName()),
                    Prefix.DREAM, MessageType.INFO, true);

            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                Bukkit.getPluginManager().callEvent(new PlayerExitStructureEvent(player, oldStructure, generatedStructure))
            );
        } else if (newStructure != null) {
            // ** Entrée
            GeneratedStructure generatedStructure = to.getWorld().getStructures(
                                    to.getChunk().getX(),
                                    to.getChunk().getZ()
                            ).stream().findFirst().orElse(null);

            MessagesManager.sendMessage(player, Component.text("§7Vous entrez dans : ").append(newStructure.getName()),
                    Prefix.DREAM, MessageType.INFO, true
            );

            Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                    Bukkit.getPluginManager().callEvent(new PlayerEnterStructureEvent(player, newStructure, generatedStructure))
            );
        }
    }
}
