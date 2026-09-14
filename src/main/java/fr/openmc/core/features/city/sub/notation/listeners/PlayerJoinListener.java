package fr.openmc.core.features.city.sub.notation.listeners;

import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.sub.notation.NotationManager;
import fr.openmc.core.features.city.sub.notation.models.CityNotation;
import fr.openmc.core.utils.text.DateUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final CityManager cityManager;
    private final NotationManager notationManager;

    public PlayerJoinListener(CityManager cityManager) {
        this.cityManager = cityManager;
        this.notationManager = cityManager.NOTATION;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        City playerCity = City.ofPlayer(player);
        if (playerCity == null) return;

        CityNotation notation = playerCity.getNotationOfWeek(DateUtils.getWeekFormat());
        if (notation == null) return;

        int rankCity = notationManager.getSortedNotationForWeek(DateUtils.getWeekFormat()).indexOf(notation) + 1;
        MessagesManager.sendMessage(player,
                TranslationManager.translation("feature.city.notation.join.message", Component.text(rankCity))
                        .clickEvent(ClickEvent.runCommand("city notation"))
                        .hoverEvent(TranslationManager.translation("feature.city.notation.join.hover")),
                Prefix.CITY, MessageType.INFO, false);
    }
}
