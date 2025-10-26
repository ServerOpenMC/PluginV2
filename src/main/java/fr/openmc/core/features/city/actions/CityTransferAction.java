package fr.openmc.core.features.city.actions;

import fr.openmc.api.menulib.template.ConfirmMenu;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.conditions.CityManageConditions;
import fr.openmc.core.utils.CacheOfflinePlayer;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class CityTransferAction {
    public static void transfer(Player player, City city, OfflinePlayer playerToTransfer) {
        OfflinePlayer owner = CacheOfflinePlayer.getOfflinePlayer(city.getPlayerWithPermission(CityPermission.OWNER));

        if (owner.isOnline()) {
            if (!CityManageConditions.canCityTransfer(city, owner.getPlayer())) return;
        }

        ConfirmMenu menu = new ConfirmMenu(player,
                () -> {
                    city.changeOwner(playerToTransfer.getUniqueId());
                    MessagesManager.sendMessage(player, Component.text("Le nouveau propriétaire est " + playerToTransfer.getName()), Prefix.CITY, MessageType.SUCCESS, false);

                    if (playerToTransfer.isOnline()) {
                        MessagesManager.sendMessage(playerToTransfer.getPlayer(), Component.text("Vous êtes devenu le propriétaire de la ville"), Prefix.CITY, MessageType.INFO, true);
                    }
                    player.closeInventory();
                },
                player::closeInventory,
                List.of(Component.text("§7Voulez-vous vraiment donner la ville à " + playerToTransfer.getName() + " ?")),
                List.of(Component.text("§7Vous n'allez pas donner la ville à " + playerToTransfer.getName())));
        menu.open();
    }
}
