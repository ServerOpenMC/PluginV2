package fr.openmc.core.listeners;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.events.ArmorEquipEvent;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.registry.items.options.EquipableItem;
import fr.openmc.core.utils.ArmorType;
import fr.openmc.core.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class EquipableItemListener implements Listener {

    @EventHandler
    public void onEquip(ArmorEquipEvent event) {
        if (event.getType() == null) return;

        Player player = event.getPlayer();

        ItemStack oldPiece = event.getOldArmorPiece();

        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            recalc(player, oldPiece);
        }, 1L);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            recalc(event.getPlayer(), null);
        }, 20L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        for (ItemStack piece : player.getInventory().getArmorContents()) {
            if (piece == null || piece.getType().isAir()) continue;

            CustomItem customItem = CustomItemRegistry.getByItemStack(piece);
            if (customItem instanceof EquipableItem equipable) {
                equipable.removeEffects(player);
            }
        }
    }

    @EventHandler
    public void onDreamTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        boolean entering = !DreamUtils.isDreamWorld(event.getFrom()) && DreamUtils.isDreamWorld(event.getTo());
        boolean leaving = DreamUtils.isDreamWorld(event.getFrom()) && !DreamUtils.isDreamWorld(event.getTo());

        if (!entering && !leaving) return;

        ItemStack oldPiece = null;

        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            recalc(player, oldPiece);
        }, 20L);
    }

    private void recalc(Player player, ItemStack oldPiece) {
        if (oldPiece != null && !oldPiece.getType().isAir()) {

            CustomItem customItem = CustomItemRegistry.getByItemStack(oldPiece);
            if (customItem instanceof EquipableItem equipable) {
                equipable.removeEffects(player);
            }
        }

        Map<PotionEffectType, Integer> effects = new HashMap<>();

        for (ItemStack piece : player.getInventory().getArmorContents()) {

            if (piece == null || piece.getType().isAir()) continue;

            CustomItem customItem = CustomItemRegistry.getByItemStack(piece);
            if (!(customItem instanceof EquipableItem equipable)) continue;

            for (var entry : equipable.getEffects().entrySet()) {
                PotionEffectType type = entry.getKey();
                int amplifier = entry.getValue();

                effects.merge(type, amplifier, Math::max);
            }
        }

        for (var entry : effects.entrySet()) {
            PotionEffectType type = entry.getKey();
            int amplifier = entry.getValue();

            player.addPotionEffect(
                    new PotionEffect(
                            type,
                            Integer.MAX_VALUE,
                            amplifier,
                            false,
                            false,
                            true
                    )
            );
        }
    }
}