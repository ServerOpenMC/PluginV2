package fr.openmc.core.features.dream.listeners.armors;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.events.ArmorEquipEvent;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.utils.ArmorType;
import fr.openmc.core.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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

        DreamArmorInfo info = dreamPieces.get(type);
        ItemStack dreamItem = DreamItemRegistry.getByName(info.namespace).getBest();

        boolean wasWearing = ItemUtils.isSimilar(dreamItem, event.getOldArmorPiece());
        boolean isWearing = ItemUtils.isSimilar(dreamItem, event.getNewArmorPiece());

        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            if (isWearing && !player.hasPotionEffect(info.effect.getType())) {
                player.addPotionEffect(info.effect);
            } else if (!isWearing && wasWearing) {
                player.removePotionEffect(info.effect.getType());
            }
        }, 1L);
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

    private ItemStack getEquippedArmor(Player player, ArmorType type) {
        return switch (type) {
            case HELMET -> player.getInventory().getHelmet();
            case CHESTPLATE -> player.getInventory().getChestplate();
            case LEGGINGS -> player.getInventory().getLeggings();
            case BOOTS -> player.getInventory().getBoots();
        };
    }
}
