package fr.openmc.core.features.city.actions;

import fr.openmc.api.menulib.template.ConfirmMenu;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.conditions.CityManageConditions;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;


public class CityDeleteAction {
    private static final CityManager CITY_MANAGER = OMCRegistry.FEATURES.CITY.get();

    public static void startDeleteCity(OMCPlayer player) {
        City city = player.city().getCity();

        if (city == null) {
            player.message().sendError(TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, true);
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

                    CITY_MANAGER.deleteCity(city);
                    player.message().sendSuccess(TranslationManager.translation("feature.city.delete.success"), Prefix.CITY, false);

                    player.cooldown().use("city:big", 60000); // 1 minute
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
