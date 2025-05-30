package fr.openmc.core.features.city.actions;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.menulib.default_menu.ConfirmMenu;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.conditions.CityManageConditions;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;


public class CityDeleteAction {
    public static void startDeleteCity(Player player) {
        UUID uuid = player.getUniqueId();

        City city = CityManager.getPlayerCity(uuid);

        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, true);
            player.closeInventory();
            return;
        }

        if (!CityManageConditions.canCityDelete(city, player)) return;

        ConfirmMenu menu = new ConfirmMenu(player,
                () -> {
                    for (UUID townMember : city.getMembers()) {
                        if (Bukkit.getPlayer(townMember) instanceof Player member) {
                            member.clearActivePotionEffects();
                        }
                    }

                    city.delete();
                    MessagesManager.sendMessage(player, Component.text("Votre ville a été supprimée"), Prefix.CITY, MessageType.SUCCESS, false);

                    DynamicCooldownManager.use(uuid.toString(), "city:big", 60000); //1 minute
                    player.closeInventory();
                },
                player::closeInventory,
                List.of(
                        Component.text("§7Voulez vous vraiment dissoudre la ville " + city.getName() + " ?"),
                        Component.text("§cCette action est §4§lIRREVERSIBLE")
                ),
                List.of(
                        Component.text("§7Ne pas supprimer la ville")
                )
        );
        menu.open();
    }
}
