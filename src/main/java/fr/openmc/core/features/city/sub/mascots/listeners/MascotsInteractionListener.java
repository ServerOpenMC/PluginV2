package fr.openmc.core.features.city.sub.mascots.listeners;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
import fr.openmc.core.features.city.sub.mascots.menu.MascotMenu;
import fr.openmc.core.features.city.sub.mascots.menu.MascotsDeadMenu;
import fr.openmc.core.features.city.sub.mascots.models.Mascot;
import fr.openmc.core.features.city.sub.mascots.utils.MascotUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class MascotsInteractionListener implements Listener {
    @SneakyThrows
    @EventHandler
    void onInteractWithMascots(PlayerInteractEntityEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;

        Player player = e.getPlayer();
        Entity clickEntity = e.getRightClicked();

        if (!MascotUtils.canBeAMascot(clickEntity)) return;

        PersistentDataContainer data = clickEntity.getPersistentDataContainer();
        String mascotsUUID = data.get(MascotsManager.mascotsKey, PersistentDataType.STRING);
        if (mascotsUUID == null) return;
        String mascotsData = data.get(MascotsManager.mascotsKey, PersistentDataType.STRING);
        if (mascotsData == null) return;
        UUID mascotsUUID = UUID.fromString(mascotsData);

        City city = CityManager.getPlayerCity(player.getUniqueId());

        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (city.isInWar()) return;

        UUID cityUUID = city.getUniqueId();
        if (mascotsUUID.equals(cityUUID)) {
            Mascot mascot = city.getMascot();
            if (mascot == null) {
                MessagesManager.sendMessage(player, Component.text("§cAucune mascotte trouvée - Veuillez contacter le staff"), Prefix.CITY, MessageType.ERROR, false);
                return;
            }
            if (!mascot.isAlive()) {
                new MascotsDeadMenu(player, cityUUID).open();
            } else {
                new MascotMenu(player, mascot).open();
            }
        } else {
            MessagesManager.sendMessage(player, Component.text("§cCette mascotte ne vous appartient pas"), Prefix.CITY, MessageType.ERROR, false);
        }
    }

}
