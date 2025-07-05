package fr.openmc.core.features.city.actions;

import fr.openmc.api.input.signgui.SignGUI;
import fr.openmc.api.input.signgui.exception.SignGUIVersionException;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.conditions.CityRankCondition;
import fr.openmc.core.features.city.menu.ranks.CityRankDetailsMenu;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;

public class CityRankAction {

    public static void beginCreateRank(Player player) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (!CityRankCondition.canCreateRank(city, player)) {
            return;
        }

        String[] lines = new String[4];
        lines[0] = "";
        lines[1] = " ᐱᐱᐱᐱᐱᐱᐱ ";
        lines[2] = "Entrez votre nom";
        lines[3] = "de ville ci dessus";

        SignGUI gui;
        try {
            gui = SignGUI.builder()
                    .setLines(null, lines[1], lines[2], lines[3])
                    .setType(ItemUtils.getSignType(player))
                    .setHandler((p, result) -> {
                        String input = result.getLine(0);

                        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                            CityRankAction.afterCreateRank(player, input);
                        });

                        return Collections.emptyList();
                    })
                    .build();
        } catch (SignGUIVersionException e) {
            throw new RuntimeException(e);
        }

        gui.open(player);
    }

    public static void afterCreateRank(Player player, String rankName) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (!CityRankCondition.canCreateRank(city, player)) {
            return;
        }

        if (city.isRankExists(rankName)) {
            MessagesManager.sendMessage(player, MessagesManager.Message.CITYRANKS_ALREADYEXIST.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        new CityRankDetailsMenu(player, city, rankName).open();
    }
}