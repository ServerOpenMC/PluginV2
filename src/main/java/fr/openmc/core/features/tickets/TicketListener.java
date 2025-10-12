package fr.openmc.core.features.tickets;

import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import dev.lone.itemsadder.api.Events.FurniturePlacedEvent;
import fr.openmc.core.features.displays.holograms.Hologram;
import fr.openmc.core.features.displays.holograms.HologramLoader;
import fr.openmc.core.features.tickets.menus.MachineBallsMenu;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class TicketListener implements Listener {

    private final Map<Location, String> machineHolograms = new ConcurrentHashMap<>();

    private int hologramCounter = 0;

    @EventHandler
    public void onMachineBallsInteraction(FurnitureInteractEvent furniture) {
        if (Objects.equals(furniture.getNamespacedID(), "omc_blocks:ball_machine")) {
            furniture.getPlayer().playSound(Sound.sound(Key.key("minecraft", "block.barrel.open"), Sound.Source.BLOCK, 1f, 1f));
            new MachineBallsMenu(furniture.getPlayer()).open();
        }
    }

    @EventHandler
    public void onMachinePlaced(FurniturePlacedEvent event) {
        if (Objects.equals(event.getNamespacedID(), "omc_blocks:ball_machine")) {
            Bukkit.getScheduler().runTaskLater(fr.openmc.core.OMCPlugin.getInstance(), () -> {
                Location machineLocation = event.getBukkitEntity().getLocation();
                createMachineHologram(machineLocation);
            }, 1L);
        }
    }

    @EventHandler
    public void onMachineBreak(FurnitureBreakEvent event) {
        if (Objects.equals(event.getNamespacedID(), "omc_blocks:ball_machine")) {
            Location machineLocation = event.getBukkitEntity().getLocation();
            removeMachineHologram(machineLocation);
        }
    }

    private void createMachineHologram(Location machineLocation) {
        if (machineHolograms.containsKey(machineLocation)) return;

        String hologramName = "ball_machine_" + (++hologramCounter);

        Location hologramLocation = machineLocation.clone().add(0, 2.3, 0);

        Hologram hologram = new Hologram(hologramName);
        hologram.setLocation(hologramLocation.getX(), hologramLocation.getY(), hologramLocation.getZ());
        hologram.setScale(0.7f);
        hologram.setLines(
                "§6§lMachine à boules V1",
                "§eCliquez pour jouer !",
                "§bBonne chance !"
        );

        HologramLoader.registerHolograms(hologram);

        machineHolograms.put(machineLocation, hologramName);
    }

    private void removeMachineHologram(Location machineLocation) {
        String hologramName = machineHolograms.remove(machineLocation);
        var hologramInfo = hologramName != null ? HologramLoader.displays.get(hologramName) : null;
        if (hologramInfo == null) return;

        hologramInfo.display().remove();
        HologramLoader.displays.remove(hologramName);

        if (hologramInfo.file().exists()) {
            hologramInfo.file().delete();
        }
    }
}
