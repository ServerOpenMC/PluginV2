package fr.openmc.core.features.city.actions;

import fr.openmc.api.menulib.template.ConfirmMenu;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.conditions.CityManageConditions;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class CityTransferAction {
    public static void transfer(Player player, City city, OfflinePlayer playerToTransfer) {
        OfflinePlayer owner = CacheOfflinePlayer.getOfflinePlayer(city.getPlayerWithPermission(CityPermission.OWNER));
        String playerName = playerToTransfer.getName();

        if (owner.isOnline()) {
            if (!CityManageConditions.canCityTransfer(city, owner.getPlayer())) return;
        }

        ConfirmMenu menu = new ConfirmMenu(player,
                () -> {
                    city.changeOwner(playerToTransfer.getUniqueId());
                    MessagesManager.sendMessage(player,
                            TranslationManager.translation("feature.city.transfer.success", Component.text(playerName)),
                            Prefix.CITY,
                            MessageType.SUCCESS,
                            false
                    );

                    if (playerToTransfer.isOnline()) {
                        MessagesManager.sendMessage(playerToTransfer.getPlayer(),
                                TranslationManager.translation("feature.city.transfer.info"),
                                Prefix.CITY,
                                MessageType.INFO,
                                true
                        );
                    }
                    player.closeInventory();
                },
                player::closeInventory,
                List.of(TranslationManager.translation(
                        "feature.city.transfer.confirm.accept",
                        Component.text(playerName).color(NamedTextColor.GRAY)
                )),
                List.of(TranslationManager.translation(
                        "feature.city.transfer.confirm.deny",
                        Component.text(playerName).color(NamedTextColor.GRAY)
                )));
        menu.open();
    }
}
