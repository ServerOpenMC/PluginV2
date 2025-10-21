package fr.openmc.core.features.city.sub.notation.listeners;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.notation.NotationManager;
import fr.openmc.core.features.city.sub.notation.models.CityNotation;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

        if (playerCity == null) return;

        CityNotation notation = playerCity.getNotationOfWeek(DateUtils.getWeekFormat());
        if (notation != null) {
            int rankCity = NotationManager.getSortedNotationForWeek(DateUtils.getWeekFormat()).indexOf(notation) + 1;
            MessagesManager.sendMessage(player,
                    Component.text("§3§lNOTATION ! §7Votre ville a été notée et elle est placée n°" + rankCity + " des meilleures villes !")
                            .clickEvent(ClickEvent.runCommand("/city notation ")).hoverEvent(Component.text("§eCliquez pour voir la notation de votre ville !")),
                    Prefix.CITY, MessageType.INFO, false);
        }
    }
}
