package fr.openmc.core.features.city.sub.mayor.listeners;

import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import dev.lone.itemsadder.api.Events.FurniturePlacedEvent;
import dev.lone.itemsadder.api.Events.FurniturePrePlaceEvent;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.sub.mayor.ElectionType;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.managers.NPCManager;
import fr.openmc.core.features.city.sub.mayor.menu.MayorVoteMenu;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.hooks.FancyNpcsHook;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import fr.openmc.core.utils.world.LocationUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.urne.interact.mysterious"), Prefix.MAYOR, MessageType.INFO, false);
            return;
        }

        if (city == null) {
            if (player.getGameMode() != GameMode.CREATIVE) {
                event.getFurniture().remove(false);
            }

            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.error.not_in_city"), Prefix.MAYOR, MessageType.ERROR, false);
            return;
        }

        if (!playerCity.equals(city)) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.urne.interact.not_your_urne"), Prefix.MAYOR, MessageType.INFO, false);
            return;
        }

        if (playerCity.getElectionType() == ElectionType.OWNER_CHOOSE) {
            MessagesManager.sendMessage(player, TranslationManager.translation(
                    "feature.city.mayor.urne.interact.need_members",
                    Component.text(MayorManager.MEMBER_REQUEST_ELECTION).color(NamedTextColor.GOLD)
            ), Prefix.MAYOR, MessageType.INFO, false);
            return;
        }

        if (MayorManager.phaseMayor != 1) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.urne.interact.election_already"), Prefix.MAYOR, MessageType.INFO, false);
            return;
        }

        if (MayorManager.cityElections.get(playerCity.getUniqueId()) == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.urne.interact.no_candidate"), Prefix.MAYOR, MessageType.INFO, true);
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
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.urne.place.must_be_overworld"), Prefix.MAYOR, MessageType.WARNING, false);
            return;
        }

        City playerCity = CityManager.getPlayerCity(player.getUniqueId());
        if (playerCity == null) {
            event.setCancelled(true);
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.urne.place.need_city"), Prefix.MAYOR, MessageType.WARNING, false);
            return;
        }

        Chunk placedInChunk = event.getLocation().getChunk();
        City chunkCity = CityManager.getCityFromChunk(placedInChunk.getX(), placedInChunk.getZ());
        if (chunkCity == null) {
            event.setCancelled(true);
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.urne.place.must_be_in_city"), Prefix.MAYOR, MessageType.WARNING, false);
            return;
        }

        if (!FeaturesRewards.hasUnlockFeature(playerCity, FeaturesRewards.Feature.MAYOR)) {
            event.setCancelled(true);
            MessagesManager.sendMessage(player, TranslationManager.translation(
                    "feature.city.mayor.urne.place.require_level",
                    Component.text(FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.MAYOR)).color(NamedTextColor.GOLD)
            ), Prefix.MAYOR, MessageType.ERROR, false);
            return;
        }

        if (!playerCity.getPlayerWithPermission(CityPermission.OWNER).equals(player.getUniqueId())) {
            event.setCancelled(true);
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.urne.place.not_owner"), Prefix.MAYOR, MessageType.ERROR, false);
            return;
        }

        if (NPCManager.hasNPCS(playerCity.getUniqueId())) {
            event.setCancelled(true);
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.urne.place.already_has_npc"), Prefix.MAYOR, MessageType.ERROR, false);
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onUrnePlaceSuccessEvent(FurniturePlacedEvent event) {
        Location urneLocation = event.getFurniture().getEntity().getLocation();
        if (!FancyNpcsHook.isEnable())
            return;

        if (!"omc_blocks:urne".equals(event.getNamespacedID()))
            return;

        Player player = event.getPlayer();
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());
        Location locationMayor = LocationUtils.getSafeNearbySurface(urneLocation.clone().add(2, 0, 0), 2);
        Location locationOwner = LocationUtils.getSafeNearbySurface(urneLocation.clone().add(-2, 0, 0), 2);

        if (CityManager.getCityFromChunk(locationMayor.getChunk()) == null) {
            locationMayor = urneLocation.clone().add(0, 1, 0);
        }
        if (CityManager.getCityFromChunk(locationOwner.getChunk()) == null) {
            locationOwner = urneLocation.clone().add(0, 1, 0);
        }

        NPCManager.createNPCS(playerCity.getUniqueId(), locationMayor, locationOwner, player.getUniqueId());
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

        if (!playerCity.getPlayerWithPermission(CityPermission.OWNER).equals(player.getUniqueId())) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mayor.urne.break.not_owner"), Prefix.MAYOR, MessageType.ERROR, false);
            event.setCancelled(true);
            return;
        }

        if (!FancyNpcsHook.isEnable()) return;

        NPCManager.removeNPCS(playerCity.getUniqueId());
    }
}
