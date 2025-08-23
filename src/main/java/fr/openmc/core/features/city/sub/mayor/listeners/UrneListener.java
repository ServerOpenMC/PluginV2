package fr.openmc.core.features.city.sub.mayor.listeners;

import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import dev.lone.itemsadder.api.Events.FurniturePlacedEvent;
import dev.lone.itemsadder.api.Events.FurniturePrePlaceEvent;
import fr.openmc.api.hooks.FancyNpcsHook;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.sub.mayor.ElectionType;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.managers.NPCManager;
import fr.openmc.core.features.city.sub.mayor.menu.MayorVoteMenu;
import fr.openmc.core.utils.LocationUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

public class UrneListener implements Listener {

    @EventHandler
    public void onUrneInteractEvent(FurnitureInteractEvent event) {
        if (!Objects.equals(event.getNamespacedID(), "omc_blocks:urne")) return;

        Player player = event.getPlayer();
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

        Chunk chunk = event.getFurniture().getEntity().getChunk();
        City city = CityManager.getCityFromChunk(chunk.getX(), chunk.getZ());

        if (playerCity == null) {
            MessagesManager.sendMessage(player, Component.text("§8§o*Mystérieux objet... Cela doit surement servir pour des éléctions...*"), Prefix.MAYOR, MessageType.INFO, false);
            return;
        }

        if (city == null) {
            if (player.getGameMode() != GameMode.CREATIVE) {
                event.getFurniture().remove(false);
            }

            MessagesManager.sendMessage(player, Component.text("§8§oCet objet n'est pas dans une ville"), Prefix.MAYOR, MessageType.ERROR, false);
            return;
        }

        if (!playerCity.equals(city)) {
            MessagesManager.sendMessage(player, Component.text("§8§o*Mhh... Ce n'est pas votre urne*"), Prefix.MAYOR, MessageType.INFO, false);
            return;
        }

        if (playerCity.getElectionType() == ElectionType.OWNER_CHOOSE) {
            MessagesManager.sendMessage(player, Component.text("§8§o*vous devez avoir au moins §6" + MayorManager.MEMBER_REQUEST_ELECTION + " §8membres afin de pouvoir faire une éléction*"), Prefix.MAYOR, MessageType.INFO, false);
            return;
        }

        if (MayorManager.phaseMayor != 1) {
            MessagesManager.sendMessage(player, Component.text("§8§o*Les éléctions ont déjà eu lieu !*"), Prefix.MAYOR, MessageType.INFO, false);
            return;
        }

        if (MayorManager.cityElections.get(playerCity.getUUID()) == null) {
            MessagesManager.sendMessage(player, Component.text("§8§o*personne ne s'est présenté ! Présenter vous ! /city*"), Prefix.MAYOR, MessageType.INFO, true);
            return;
        }

        new MayorVoteMenu(player).open();

        player.playSound(player.getLocation(), Sound.BLOCK_LANTERN_PLACE, 1.0F, 1.7F);
    }

    @EventHandler(ignoreCancelled = true)
    public void onUrnePrePlaceEvent(FurniturePrePlaceEvent event) {
        if (!"omc_blocks:urne".equals(event.getNamespacedID())) return;
        Player player = event.getPlayer();

        if (!player.getWorld().getName().equals("world")) {
            event.setCancelled(true);
            MessagesManager.sendMessage(player, Component.text("Vous devez être dans l'overworld pour poser ceci !"), Prefix.MAYOR, MessageType.WARNING, false);
            return;
        }

        City playerCity = CityManager.getPlayerCity(player.getUniqueId());
        if (playerCity == null) {
            event.setCancelled(true);
            MessagesManager.sendMessage(player, Component.text("Vous devez avoir une ville pour poser ceci !"), Prefix.MAYOR, MessageType.WARNING, false);
            return;
        }

        Chunk placedInChunk = event.getLocation().getChunk();
        City chunkCity = CityManager.getCityFromChunk(placedInChunk.getX(), placedInChunk.getZ());
        if (chunkCity == null) {
            event.setCancelled(true);
            MessagesManager.sendMessage(player, Component.text("Vous devez poser ceci dans votre ville!"), Prefix.MAYOR, MessageType.WARNING, false);
            return;
        }

        if (!playerCity.getPlayerWithPermission(CityPermission.OWNER).equals(player.getUniqueId())) {
            event.setCancelled(true);
            MessagesManager.sendMessage(player, Component.text("Vous n'êtes pas le propriétaire !"), Prefix.MAYOR, MessageType.ERROR, false);
            return;
        }

        if (NPCManager.hasNPCS(playerCity.getUUID())) {
            event.setCancelled(true);
            MessagesManager.sendMessage(player, Component.text("Vous ne pouvez pas poser ceci car vous avez déjà des NPC"), Prefix.MAYOR, MessageType.ERROR, false);
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onUrnePlaceSuccessEvent(FurniturePlacedEvent event) {
        Location urneLocation = event.getFurniture().getEntity().getLocation();
        if (!FancyNpcsHook.hasFancyNpc())
            return;

        if (!"omc_blocks:urne".equals(event.getNamespacedID()))
            return;

        Player player = event.getPlayer();
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());
        Location locationMayor = LocationUtils.getSafeNearbySurface(urneLocation.clone().add(2, 0, 0), 2);
        Location locationOwner = LocationUtils.getSafeNearbySurface(urneLocation.clone().add(-2, 0, 0), 2);

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

        if (!playerCity.getPlayerWithPermission(CityPermission.OWNER).equals(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("Vous ne pouvez pas poser ceci car vous êtes pas le propriétaire"), Prefix.MAYOR, MessageType.ERROR, false);
            event.setCancelled(true);
            return;
        }

        if (!FancyNpcsHook.hasFancyNpc()) return;

        NPCManager.removeNPCS(playerCity.getUUID());
    }
}
