package fr.openmc.core.features.dream.listeners.armors;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.events.ArmorEquipEvent;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.registry.items.CustomItemRegistry;
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

import java.util.Map;

public class DreamArmorImplListener implements Listener {

    private static class DreamArmorInfo {
        final ArmorType type;
        final PotionEffect effect;
        final String namespace;

        DreamArmorInfo(ArmorType type, PotionEffectType effectType, String namespace) {
            this.type = type;
            this.effect = new PotionEffect(effectType, Integer.MAX_VALUE, 0, false, false);
            this.namespace = namespace;
        }
    }

    private final Map<ArmorType, DreamArmorInfo> dreamPieces = Map.of(
            ArmorType.HELMET, new DreamArmorInfo(ArmorType.HELMET, PotionEffectType.NIGHT_VISION, "omc_dream:dream_helmet"),
            ArmorType.CHESTPLATE, new DreamArmorInfo(ArmorType.CHESTPLATE, PotionEffectType.HASTE, "omc_dream:dream_chestplate"),
            ArmorType.LEGGINGS, new DreamArmorInfo(ArmorType.LEGGINGS, PotionEffectType.SPEED, "omc_dream:dream_leggings"),
            ArmorType.BOOTS, new DreamArmorInfo(ArmorType.BOOTS, PotionEffectType.JUMP_BOOST, "omc_dream:dream_boots")
    );

    @EventHandler
    public void onEquip(ArmorEquipEvent event) {
        Player player = event.getPlayer();
        ArmorType type = event.getType();
        if (type == null || !dreamPieces.containsKey(type)) return;

        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            ItemStack pieceNow = getEquippedArmor(player, type);
            DreamArmorInfo info = dreamPieces.get(type);
            ItemStack dreamItem = DreamItemRegistry.getByName(info.namespace).getBest();
            boolean hasPieceNow = ItemUtils.isSimilar(dreamItem, pieceNow);

            if (hasPieceNow && !player.hasPotionEffect(info.effect.getType())) {
                player.addPotionEffect(info.effect);
            } else if (!ItemUtils.isSimilar(CustomItemRegistry.getByName("omc_items:aywen_cap").getBest(), pieceNow)) {
                player.removePotionEffect(info.effect.getType());
            }
        }, 2L);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        for (DreamArmorInfo info : dreamPieces.values()) {
            ItemStack equipped = getEquippedArmor(player, info.type);
            if (equipped != null && ItemUtils.isSimilar(
                    DreamItemRegistry.getByName(info.namespace).getBest(), equipped)) {
                player.addPotionEffect(info.effect);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        dreamPieces.values().forEach(info ->
                player.removePotionEffect(info.effect.getType()));
    }

    @EventHandler
    public void onDreamTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        boolean entering = !DreamUtils.isDreamWorld(event.getFrom()) && DreamUtils.isDreamWorld(event.getTo());
        boolean leaving = DreamUtils.isDreamWorld(event.getFrom()) && !DreamUtils.isDreamWorld(event.getTo());

        if (!entering && !leaving) return;

        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> recalcEffects(player), 20L);
    }

    private void recalcEffects(Player player) {
        for (DreamArmorInfo info : dreamPieces.values()) {
            if (info.type.equals(ArmorType.HELMET) &&
                    ItemUtils.isSimilar(CustomItemRegistry.getByName("omc_items:aywen_cap").getBest(), player.getEquipment().getHelmet()))
                continue;

            ItemStack equipped = getEquippedArmor(player, info.type);
            ItemStack dreamItem = DreamItemRegistry.getByName(info.namespace).getBest();

            if (ItemUtils.isSimilar(dreamItem, equipped)) {
                player.addPotionEffect(info.effect);
            } else {
                player.removePotionEffect(info.effect.getType());
            }
        }
    }

    private ItemStack getEquippedArmor(Player player, ArmorType type) {
        return switch (type) {
            case HELMET -> player.getInventory().getHelmet();
            case CHESTPLATE -> player.getInventory().getChestplate();
            case LEGGINGS -> player.getInventory().getLeggings();
            case BOOTS -> player.getInventory().getBoots();
        };
    }
}
