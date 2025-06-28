package fr.openmc.core.features.city.sub.mayor.listeners;

import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import dev.lone.itemsadder.api.Events.FurniturePlaceSuccessEvent;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mayor.ElectionType;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.managers.NPCManager;
import fr.openmc.core.features.city.sub.mayor.menu.MayorVoteMenu;
import fr.openmc.core.utils.api.FancyNpcsApi;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

import static fr.openmc.core.utils.LocationUtils.getSafeNearbySurface;

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

        if (city == null) {
            removeUrne(player, furniture.getFurniture());
            MessagesManager.sendMessage(player, Component.text("§8§oCet objet n'est pas dans une ville"), Prefix.MAYOR, MessageType.ERROR, false);
            return;
        }

        if (playerCity.equals(city)) {
            MessagesManager.sendMessage(player, Component.text("§8§o*Mhh... Ce n'est pas votre urne*"), Prefix.MAYOR, MessageType.INFO, false);
            return;
        }

        if (playerCity.getElectionType().equals(ElectionType.OWNER_CHOOSE)) {
            MessagesManager.sendMessage(player, Component.text("§8§o*vous devez avoir au moins §6" + MayorManager.MEMBER_REQ_ELECTION + " §8membres afin de pouvoir faire une éléction*"), Prefix.MAYOR, MessageType.INFO, false);
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

    @EventHandler
    private void onUrnePlaceSuccessEvent(FurniturePlaceSuccessEvent event) {
        if (!Objects.equals(event.getNamespacedID(), "omc_blocks:urne")) return;

        Player player = event.getPlayer();

        if (!player.getWorld().getName().equals("world")) {
            removeUrne(player, event.getFurniture());
            return;
        }

        City playerCity = CityManager.getPlayerCity(player.getUniqueId());
        if (playerCity == null) {
            removeUrne(player, event.getFurniture());
            MessagesManager.sendMessage(player, Component.text("Vous devez avoir une ville pour poser ceci !"), Prefix.MAYOR, MessageType.WARNING, false);
            return;
        }

        City chunkCity = CityManager.getCityFromChunk(event.getFurniture().getEntity().getChunk().getX(), event.getFurniture().getEntity().getChunk().getZ());

        if (chunkCity == null) {
            removeUrne(player, event.getFurniture());
            MessagesManager.sendMessage(player, Component.text("Vous devez poser ceci dans votre ville!"), Prefix.MAYOR, MessageType.WARNING, false);
            return;
        }

        if (!chunkCity.getUUID().equals(playerCity.getUUID())) {
            removeUrne(player, event.getFurniture());
            MessagesManager.sendMessage(player, Component.text("Vous devez la poser dans votre ville"), Prefix.MAYOR, MessageType.ERROR, false);
            return;
        }

        if (!playerCity.getPlayerWithPermission(CPermission.OWNER).equals(player.getUniqueId())) {
            removeUrne(player, event.getFurniture());
            MessagesManager.sendMessage(player, Component.text("Vous n'êtes pas le propriétaire !"), Prefix.MAYOR, MessageType.ERROR, false);
            return;
        }

        if (NPCManager.hasNPCS(playerCity.getUUID())) {
            removeUrne(player, event.getFurniture());
            MessagesManager.sendMessage(player, Component.text("Vous ne pouvez pas poser ceci car vous avez déjà des NPC"), Prefix.MAYOR, MessageType.ERROR, false);
            return;
        }

        Location urneLocation = event.getFurniture().getEntity().getLocation();

        if (!FancyNpcsApi.hasFancyNpc()) return;

        Location locationMayor = getSafeNearbySurface(urneLocation, 2);

        Location locationOwner = getSafeNearbySurface(urneLocation, 2);

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

        if (!playerCity.getPlayerWithPermission(CPermission.OWNER).equals(player.getUniqueId())) {
            MessagesManager.sendMessage(player, Component.text("Vous ne pouvez pas poser ceci car vous êtes pas le propriétaire"), Prefix.MAYOR, MessageType.ERROR, false);
            event.setCancelled(true);
            return;
        }

        if (!FancyNpcsApi.hasFancyNpc()) return;

        NPCManager.removeNPCS(playerCity.getUUID());
    }

    private void removeUrne(Player player, CustomFurniture furniture) {
        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            if (furniture != null) {
                furniture.remove(!player.getGameMode().equals(GameMode.CREATIVE));
                if (furniture.getEntity() != null && !furniture.getEntity().isDead()) {
                    furniture.getEntity().remove();
                }
            }
        }, 1L);
    }


}
