package fr.openmc.core.features.city.actions;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.menulib.template.ConfirmMenu;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.conditions.CityManageConditions;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;


public class CityDeleteAction {
    public static void startDeleteCity(Player player) {
        UUID uuid = player.getUniqueId();

        City city = CityManager.getPlayerCity(uuid);

        if (city == null) {
            MessagesManager.sendMessage(player, TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, MessageType.ERROR, true);
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

                    CityManager.deleteCity(city);
                    MessagesManager.sendMessage(player, TranslationManager.translation("feature.city.delete.success"), Prefix.CITY, MessageType.SUCCESS, false);

                    DynamicCooldownManager.use(uuid, "city:big", 60000); // 1 minute
                    player.closeInventory();
                },
                player::closeInventory,
                List.of(
                        TranslationManager.translation(
                                "feature.city.delete.confirm.lore",
                                Component.text(city.getName()).color(NamedTextColor.GRAY)
                        ),
                        TranslationManager.translation("feature.city.delete.confirm.warning")
                ),
                List.of(
                        TranslationManager.translation("feature.city.delete.confirm.deny")
                )
        );
        menu.open();
    }
}
