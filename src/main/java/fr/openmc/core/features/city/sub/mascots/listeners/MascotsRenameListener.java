package fr.openmc.core.features.city.sub.mascots.listeners;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
import fr.openmc.core.features.city.sub.mascots.models.Mascot;
import fr.openmc.core.features.city.sub.mascots.utils.MascotUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class MascotsRenameListener implements Listener {

    @EventHandler
    public void onRenameWithNameTag(PlayerInteractEntityEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;

        Player player = e.getPlayer();
        Entity entity = e.getRightClicked();

        if (!MascotUtils.canBeAMascot(entity)) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() != Material.NAME_TAG) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        PersistentDataContainer data = entity.getPersistentDataContainer();
        String cityUUID = data.get(MascotsManager.mascotsKey, PersistentDataType.STRING);
        if (cityUUID == null) return;

        e.setCancelled(true);

        City city = CityManager.getCity(UUID.fromString(cityUUID));
        if (city != null) {
            Mascot mascot = city.getMascot();
            if (mascot != null) {
                entity.customName(Component.text(mascot.getEntity().getName()));
                entity.setCustomNameVisible(true);
            }
        }
	    
	    MessagesManager.sendMessage(player, Component.text("§cVous ne pouvez pas renommer cette mascotte"), Prefix.CITY, MessageType.ERROR, false);
    }
}
