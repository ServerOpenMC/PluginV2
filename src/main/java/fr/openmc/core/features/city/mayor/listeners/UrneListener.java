package fr.openmc.core.features.city.mayor.listeners;

import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import dev.lone.itemsadder.api.Events.FurniturePlaceSuccessEvent;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mayor.ElectionType;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.features.city.mayor.managers.NPCManager;
import fr.openmc.core.features.city.menu.mayor.MayorVoteMenu;
import fr.openmc.core.utils.api.FancyNpcApi;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

public class UrneListener implements Listener {

    @EventHandler
    private void onUrneInteractEvent(FurnitureInteractEvent furniture) {
        if (!Objects.equals(furniture.getNamespacedID(), "omc_blocks:urne")) return;

        Player player = furniture.getPlayer();
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

        Chunk chunk = furniture.getFurniture().getEntity().getChunk();
        City city = CityManager.getCityFromChunk(chunk.getX(), chunk.getZ());

        if (playerCity == null) {
            MessagesManager.sendMessage(player, Component.text("§8§o*Mystérieux objet... Cela doit surement servir pour des éléctions...*"), Prefix.MAYOR, MessageType.INFO, false);
            return;
        }

        if (playerCity != city) {
            MessagesManager.sendMessage(player, Component.text("§8§o*Mhh... Ce n'est pas votre urne*"), Prefix.MAYOR, MessageType.INFO, false);
            return;
        }

        MayorManager mayorManager = MayorManager.getInstance();

        if (playerCity.getElectionType() == ElectionType.OWNER_CHOOSE) {
            MessagesManager.sendMessage(player, Component.text("§8§o*vous devez avoir au moins §6" + mayorManager.MEMBER_REQ_ELECTION + " §8membres afin de pouvoir faire une éléction*"), Prefix.MAYOR, MessageType.INFO, false);
            return;
        }

        if (mayorManager.phaseMayor != 1) {
            MessagesManager.sendMessage(player, Component.text("§8§o*Les éléctions ont déjà eu lieu !*"), Prefix.MAYOR, MessageType.INFO, false);
            return;
        }

        if (mayorManager.cityElections.get(playerCity) == null) {
            MessagesManager.sendMessage(player, Component.text("§8§o*personne ne s'est présenté ! Présenter vous ! /city*"), Prefix.MAYOR, MessageType.INFO, true);
            return;
        }

        new MayorVoteMenu(player).open();

        player.playSound(player.getLocation(), Sound.BLOCK_LANTERN_PLACE, 1.0F, 1.7F);

    }

    @EventHandler
    private void onUrnePlaceSuccessEvent(FurniturePlaceSuccessEvent event) {
        if (!Objects.equals(event.getNamespacedID(), "omc_blocks:urne")) return;

        Player player = event.getPlayer();

        if (!player.getWorld().getName().equals("world")) {
            removeUrne(event.getFurniture());
            return;
        }

        City playerCity = CityManager.getPlayerCity(player.getUniqueId());
        if (playerCity == null) {
            removeUrne(event.getFurniture());
            MessagesManager.sendMessage(player, Component.text("Vous devez avoir une ville pour poser ceci !"), Prefix.MAYOR, MessageType.WARNING, false);
            return;
        }

        City chunkCity = CityManager.getCityFromChunk(event.getFurniture().getEntity().getChunk().getX(), event.getFurniture().getEntity().getChunk().getZ());

        if (chunkCity == null) {
            removeUrne(event.getFurniture());
            MessagesManager.sendMessage(player, Component.text("Vous devez poser ceci dans votre ville!"), Prefix.MAYOR, MessageType.WARNING, false);
            return;
        }

        if (!chunkCity.getUUID().equals(playerCity.getUUID())) {
            removeUrne(event.getFurniture());
            MessagesManager.sendMessage(player, Component.text("Vous devez la poser dans votre ville"), Prefix.MAYOR, MessageType.ERROR, false);
            return;
        }

        if (!playerCity.getPlayerWith(CPermission.OWNER).equals(player.getUniqueId())) {
            removeUrne(event.getFurniture());
            MessagesManager.sendMessage(player, Component.text("Vous n'êtes pas le propriétaire !"), Prefix.MAYOR, MessageType.ERROR, false);
            return;
        }

        if (NPCManager.hasNPCS(playerCity.getUUID())) {
            removeUrne(event.getFurniture());
            MessagesManager.sendMessage(player, Component.text("Vous ne pouvez pas poser ceci car vous avez déjà des NPC"), Prefix.MAYOR, MessageType.ERROR, false);
            return;
        }

        Location urneLocation = event.getFurniture().getEntity().getLocation();

        if (!FancyNpcApi.hasFancyNpc()) return;

        int baseY = urneLocation.getBlockY();
        World world = urneLocation.getWorld();

        Location locationMayor = new Location(world, urneLocation.getX() + 3, baseY, urneLocation.getZ());
        locationMayor = getSafeNearbySurface(locationMayor);

        Location locationOwner = new Location(world, urneLocation.getX() - 3, baseY, urneLocation.getZ());
        locationOwner = getSafeNearbySurface(locationOwner);

        NPCManager.createNPCS(playerCity.getUUID(), locationMayor, locationOwner, player.getUniqueId());
    }

    @EventHandler
    private void onUrneBreakEvent(FurnitureBreakEvent event) {
        if (!Objects.equals(event.getNamespacedID(), "omc_blocks:urne")) return;

        Player player = event.getPlayer();

        City playerCity = CityManager.getPlayerCity(player.getUniqueId());
        if (playerCity == null) {
            event.setCancelled(true);
            return;
        }

        if (playerCity.getMayor() == null) {
            event.setCancelled(true);
            return;
        }

        if (playerCity.getMayor().getUUID() == null) {
            event.setCancelled(true);
            return;
        }

        if (!playerCity.getPlayerWith(CPermission.OWNER).equals(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("Vous ne pouvez pas poser ceci car vous êtes pas le propriétaire"), Prefix.MAYOR, MessageType.ERROR, false);
            event.setCancelled(true);
            return;
        }

        if (!FancyNpcApi.hasFancyNpc()) return;

        NPCManager.removeNPCS(playerCity.getUUID());
    }

    private void removeUrne(CustomFurniture furniture) {
        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            if (furniture != null) {
                furniture.remove(true);
                if (furniture.getEntity() != null && !furniture.getEntity().isDead()) {
                    furniture.getEntity().remove();
                }
            }
        }, 1L);
    }

    private Location getSafeNearbySurface(Location urneLoc) {
        World world = urneLoc.getWorld();
        int baseY = urneLoc.getBlockY();

        int radius = 2;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                Location candidate = new Location(
                        world,
                        urneLoc.getX() + dx,
                        baseY,
                        urneLoc.getZ() + dz
                );

                Block under = world.getBlockAt(candidate.getBlockX(), baseY - 1, candidate.getBlockZ());
                Block feet = world.getBlockAt(candidate.getBlockX(), baseY, candidate.getBlockZ());
                Block head = world.getBlockAt(candidate.getBlockX(), baseY + 1, candidate.getBlockZ());

                if (!under.isPassable() && feet.isPassable() && head.isPassable()) {
                    return new Location(
                            world,
                            candidate.getBlockX() + 0.5,
                            baseY,
                            candidate.getBlockZ() + 0.5
                    );
                }
            }
        }

        return new Location(
                world,
                urneLoc.getBlockX() + 0.5,
                baseY,
                urneLoc.getBlockZ() + 0.5
        );
    }
}
