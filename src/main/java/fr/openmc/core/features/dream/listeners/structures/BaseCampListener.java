package fr.openmc.core.features.dream.listeners.structures;

import fr.openmc.core.features.dream.events.PlayerEnterStructureEvent;
import fr.openmc.core.features.dream.events.PlayerExitStructureEvent;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.generator.structure.GeneratedStructure;
import org.bukkit.util.BoundingBox;

public class BaseCampListener implements Listener {
    @EventHandler
    public void onEnter(PlayerEnterStructureEvent event) {
        GeneratedStructure structure = event.getGeneratedStructure();

        BoundingBox box = structure.getBoundingBox();

        World world = event.getPlayer().getWorld();

        for (int x = (int) box.getMinX(); x <= box.getMaxX(); x++) {
            for (int y = (int) box.getMinY(); y <= box.getMaxY(); y++) {
                for (int z = (int) box.getMinZ(); z <= box.getMaxZ(); z++) {

                    Block block = world.getBlockAt(x, y, z);

                    if (block.getType() == Material.CAMPFIRE || block.getType() == Material.SOUL_CAMPFIRE) {
                        if (block.getState() instanceof Campfire campfire) {
                            campfire.setLit(true);
                            block.getState().update();
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerExitStructureEvent event) {
        GeneratedStructure structure = event.getExitedGenerateStructure();
        BoundingBox box = structure.getBoundingBox();

        World world = event.getPlayer().getWorld();

        if (isSomeoneInside(box, world)) return;

        for (int x = (int) box.getMinX(); x <= box.getMaxX(); x++) {
            for (int y = (int) box.getMinY(); y <= box.getMaxY(); y++) {
                for (int z = (int) box.getMinZ(); z <= box.getMaxZ(); z++) {

                    Block block = world.getBlockAt(x, y, z);

                    if (block.getType() == Material.CAMPFIRE || block.getType() == Material.SOUL_CAMPFIRE) {
                        if (block.getState() instanceof Campfire campfire) {
                            campfire.setLit(false);
                            block.getState().update();
                        }
                    }
                }
            }
        }
    }

    private boolean isSomeoneInside(BoundingBox box, World world) {
        for (Player player : world.getPlayers()) {
            if (box.contains(player.getLocation().toVector())) {
                return true;
            }
        }
        return false;
    }
}
