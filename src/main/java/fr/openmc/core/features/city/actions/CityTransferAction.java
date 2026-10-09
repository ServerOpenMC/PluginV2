package fr.openmc.core.features.city.actions;

import fr.openmc.api.menulib.template.ConfirmMenu;
import fr.openmc.api.omcplayer.OMCOfflinePlayer;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.models.CityPermission;
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
    public static void transfer(OMCPlayer player, City city, OMCOfflinePlayer playerToTransfer) {
        OMCOfflinePlayer owner = OMCOfflinePlayer.of(city.getPlayerWithPermission(CityPermission.OWNER));

        if (!CityManageConditions.canCityTransfer(city, owner)) return;

        ConfirmMenu menu = new ConfirmMenu(player,
                () -> {
                    city.changeOwner(playerToTransfer.getUniqueId());
                    player.message().sendSuccess(TranslationManager.translation("feature.city.transfer.success",
                           playerToTransfer.getNameWithHead()), Prefix.CITY, false);

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
                        playerToTransfer.getNameWithHead().color(NamedTextColor.GRAY)
                )),
                List.of(TranslationManager.translation(
                        "feature.city.transfer.confirm.deny",
                        playerToTransfer.getNameWithHead().color(NamedTextColor.GRAY)
                )));
        menu.open();
    }
}
