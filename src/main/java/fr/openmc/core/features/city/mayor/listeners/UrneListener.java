package fr.openmc.core.features.city.mayor.listeners;

import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import dev.lone.itemsadder.api.Events.FurniturePlaceSuccessEvent;
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
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Sound;
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
            // Supprime directement le furniture car c’est trop tard pour cancel
            Objects.requireNonNull(event.getFurniture()).remove(true);
            return;
        }

        City playerCity = CityManager.getPlayerCity(player.getUniqueId());
        if (playerCity == null || playerCity.getMayor().getUUID() == null) {
            Objects.requireNonNull(event.getFurniture()).remove(true);
            MessagesManager.sendMessage(player, Component.text("Vous devez avoir une ville pour poser ceci!"), Prefix.MAYOR, MessageType.WARNING, false);
            return;
        }

        City chunkCity = CityManager.getCityFromChunk(event.getFurniture().getEntity().getChunk().getX(), event.getFurniture().getEntity().getChunk().getZ());

        if (chunkCity == null) {
            Objects.requireNonNull(event.getFurniture()).remove(true);
            MessagesManager.sendMessage(player, Component.text("Vous devez poser ceci dans votre ville!"), Prefix.MAYOR, MessageType.WARNING, false);
            return;
        }

        if (!chunkCity.getUUID().equals(playerCity.getUUID())) {
            Objects.requireNonNull(event.getFurniture()).remove(true);
            MessagesManager.sendMessage(player, Component.text("Vous devez la poser dans votre ville"), Prefix.MAYOR, MessageType.ERROR, false);
            return;
        }

        if (!playerCity.getMayor().getUUID().equals(player.getUniqueId())) {
            Objects.requireNonNull(event.getFurniture()).remove(true);
            MessagesManager.sendMessage(player, Component.text("Vous n'êtes pas le maire !"), Prefix.MAYOR, MessageType.ERROR, false);
            return;
        }

        if (NPCManager.hasNPCS(playerCity.getUUID())) {
            Objects.requireNonNull(event.getFurniture()).remove(true);
            MessagesManager.sendMessage(player, Component.text("Vous ne pouvez pas poser ceci car vous avez déjà des NPC"), Prefix.MAYOR, MessageType.ERROR, false);
            return;
        }

        Location urneLocation = event.getFurniture().getEntity().getLocation();

        if (!FancyNpcApi.hasFancyNpc()) return;

        Location locationMayor = urneLocation.clone().add(3, 0, 0);
        locationMayor = urneLocation.getWorld().getHighestBlockAt(locationMayor).getLocation().add(0, 1, 0);

        Location locationOwner = urneLocation.clone().add(-3, 0, 0);
        locationOwner = urneLocation.getWorld().getHighestBlockAt(locationOwner).getLocation().add(0, 1, 0);

        NPCManager.createNPCS(playerCity.getUUID(), locationMayor, locationOwner, player.getUniqueId());
    }

    @EventHandler
    private void onUrneBreakEvent(FurnitureBreakEvent event) {
        if (!Objects.equals(event.getNamespacedID(), "omc_blocks:urne")) return;

        Player player = event.getPlayer();

        City playerCity = CityManager.getPlayerCity(player.getUniqueId());
        if (playerCity == null) return;

        if (playerCity.getMayor().getUUID() == null) return;

        if (!playerCity.getMayor().getUUID().equals(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("Vous ne pouvez pas poser ceci car vous êtes pas le maire"), Prefix.MAYOR, MessageType.ERROR, false);
            event.setCancelled(true);
            return;
        }

        if (!FancyNpcApi.hasFancyNpc()) return;

        NPCManager.removeNPCS(playerCity.getUUID());
    }
}
