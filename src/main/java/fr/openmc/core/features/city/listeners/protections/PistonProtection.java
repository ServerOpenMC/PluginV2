package fr.openmc.core.features.city.listeners.protections;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import java.util.Objects;

public class PistonProtection implements Listener {

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        Block piston = event.getBlock();
        Chunk fromChunk = piston.getChunk();
        City fromCity = CityManager.getCityFromChunk(fromChunk.getX(), fromChunk.getZ());

        for (Block moved : event.getBlocks()) {
            Block toBlock = moved.getRelative(event.getDirection());
            Chunk toChunk = toBlock.getChunk();
            City toCity = CityManager.getCityFromChunk(toChunk.getX(), toChunk.getZ());

            if (isIllegalMovement(fromCity, toCity)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if (!event.isSticky()) return;

        Block piston = event.getBlock();
        Chunk fromChunk = piston.getChunk();
        City fromCity = CityManager.getCityFromChunk(fromChunk.getX(), fromChunk.getZ());

        for (Block moved : event.getBlocks()) {
            Block toBlock = moved.getRelative(event.getDirection());
            Chunk toChunk = toBlock.getChunk();
            City toCity = CityManager.getCityFromChunk(toChunk.getX(), toChunk.getZ());

            if (isIllegalMovement(fromCity, toCity)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    private boolean isIllegalMovement(City from, City to) {
        if (from == null && to == null) return false; //wildrness a wildrness
        if (Objects.equals(from, to)) return false;  //city a wildrness
        if (from != null && to == null) return false; //city a wildrness
        return true; //city a city
    }
}