package fr.openmc.core.features.events.contents.halloween.festival.listeners;

import fr.openmc.core.commands.utils.SpawnManager;
import fr.openmc.core.features.events.contents.halloween.festival.HalloweenFestivalManager;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.DimensionGenerator;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.TerrorHouseManager;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.floor.Floor;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.floor.Floors;
import fr.openmc.core.registry.regions.CustomRegion;
import fr.openmc.core.registry.regions.events.EntityCustomRegionEnterEvent;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;

public class TerrorHouseListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.getWorld().equals(DimensionGenerator.HALLOWEEN_WORLD)) return;

        if (!HalloweenFestivalManager.isActive())
            player.teleport(SpawnManager.getSpawnLocation());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        if (HalloweenFestivalManager.getHalloweenDB().isActive()) {
            if (event.getFrom().equals(DimensionGenerator.HALLOWEEN_WORLD))
                TerrorHouseManager.getWitchHouseAmbient().reset(player);

            if (player.getWorld().equals(DimensionGenerator.HALLOWEEN_WORLD))
                TerrorHouseManager.getWitchHouseAmbient().apply(player);
        } else {
            if (player.isOp()) return;

            if (player.getWorld().equals(DimensionGenerator.HALLOWEEN_WORLD)) {
                player.teleport(SpawnManager.getSpawnLocation());
                MessagesManager.sendMessage(player,
                        TranslationManager.translation("feature.events.halloween.festival.player_not_allow"),
                        Prefix.HALLOWEEN,
                        MessageType.INFO,
                        true
                );
            }
        }
    }

    @EventHandler
    public void onHouseRegionEnter(EntityCustomRegionEnterEvent event) {
        CustomRegion region = event.getRegion();

        if (!region.getWorld().equals(DimensionGenerator.HALLOWEEN_WORLD))
            return;

        Entity entity = event.getEntity();

        Floors floors = TerrorHouseManager.enabledFloors.stream()
                .filter(floor -> floor.getFloor().getRegion().equals(region))
                .findAny().orElse(null);

        if (floors == null) {
            checkAndRepulse(region, entity);
            return;
        }

        Floor floor = floors.getFloor();

        if (floor == null) {
            checkAndRepulse(region, entity);
            return;
        }

        if (TerrorHouseManager.limitPassed()) return;

        if (floor.canSpawn) {
            floor.spawnMob();
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Material item = event.getMaterial();

        if (item == Material.ENDER_PEARL || item == Material.CHORUS_FRUIT) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerToggleGlide(EntityToggleGlideEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        if (event.isGliding()) {
            event.setCancelled(true);
        }
    }

    private void checkAndRepulse(CustomRegion region, Entity entity) {
        if (TerrorHouseManager.useRegions.contains(region))
            if (entity instanceof Player player) {
                TerrorHouseManager.repulsePlayer(region, player, 0.3);
                MessagesManager.sendMessage(player,
                        TranslationManager.translation("feature.events.halloween.festival.floor_lock"),
                        Prefix.HALLOWEEN,
                        MessageType.INFO,
                        true
                );
            }
    }
}
