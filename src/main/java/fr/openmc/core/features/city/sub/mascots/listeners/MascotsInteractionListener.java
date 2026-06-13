package fr.openmc.core.features.city.sub.mascots.listeners;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
import fr.openmc.core.features.city.sub.mascots.menu.MascotMenu;
import fr.openmc.core.features.city.sub.mascots.menu.MascotsDeadMenu;
import fr.openmc.core.features.city.sub.mascots.models.Mascot;
import fr.openmc.core.features.city.sub.mascots.utils.MascotUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.SneakyThrows;
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
        String mascotsData = data.get(MascotsManager.mascotsKey, PersistentDataType.STRING);
        if (mascotsData == null) return;
        UUID mascotsUUID = UUID.fromString(mascotsData);

        City city = CityManager.getPlayerCity(player.getUniqueId());

        if (city == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (city.isInWar()) return;

        UUID cityUUID = city.getUniqueId();
        if (mascotsUUID.equals(cityUUID)) {
            Mascot mascot = city.getMascot();
            if (mascot == null) {
                MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mascots.interaction.error.not_found"), Prefix.CITY, MessageType.ERROR, false);
                return;
            }
            if (!mascot.isAlive()) {
                new MascotsDeadMenu(player, cityUUID).open();
            } else {
                new MascotMenu(player, mascot).open();
            }
        } else {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.mascots.interaction.error.not_owner"), Prefix.CITY, MessageType.ERROR, false);
        }
    }

}
