package fr.openmc.core.features.dream.listeners.registry;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.events.ArmorEquipEvent;
import fr.openmc.core.features.dream.DreamManager;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.models.db.DreamPlayer;
import fr.openmc.core.features.dream.models.registry.items.DreamEquipableItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class DreamItemEquipListener implements Listener {

    @EventHandler
    public void onArmorEquip(ArmorEquipEvent event) {
        Player player = event.getPlayer();
        if (!DreamUtils.isInDream(player)) return;

        Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), () -> {
            DreamPlayer dreamPlayer = DreamManager.getDreamPlayer(player);
            if (dreamPlayer == null) return;

            long base = DreamManager.BASE_DREAM_TIME;
            long bonus = 0;

            for (ItemStack armor : player.getInventory().getArmorContents()) {

                DreamItem item = DreamItemRegistry.getByItemStack(armor);
                if (item instanceof DreamEquipableItem equipable) {
                    bonus += equipable.getAdditionalMaxTime();
                }
            }

            long newMax = base + bonus;
            DreamManager.setMaxTime(player, newMax);
        }, 1L);
    }
}