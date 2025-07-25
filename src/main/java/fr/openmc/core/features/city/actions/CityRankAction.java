package fr.openmc.core.features.city.actions;

import fr.openmc.api.input.DialogInput;
import fr.openmc.api.menulib.default_menu.ConfirmMenu;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.conditions.CityRankCondition;
import fr.openmc.core.features.city.menu.ranks.CityRankDetailsMenu;
import fr.openmc.core.features.city.menu.ranks.CityRankMemberMenu;
import fr.openmc.core.features.city.models.CityRank;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class CityRankAction {
    private static final int MAX_LENGTH_RANK_NAME = 16;

    public static void beginCreateRank(Player player) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (!CityRankCondition.canCreateRank(city, player)) {
            return;
        }

        DialogInput.send(player, "Entrez le nom de votre grade", MAX_LENGTH_RANK_NAME, input ->
                CityRankAction.afterCreateRank(player, input)
        );
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

    public static void renameRank(Player player, String oldName) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (!CityRankCondition.canRenameRank(city, player, oldName)) {
            return;
        }

        DialogInput.send(player, "Entrez le nouveau nom de votre grade", MAX_LENGTH_RANK_NAME, input -> {
            if (!CityRankCondition.canRenameRank(city, player, oldName)) {
                return;
            }

            CityRank rank = city.getRankByName(oldName);
            if (rank == null) {
                MessagesManager.sendMessage(player, MessagesManager.Message.CITYRANKS_NOTEXIST.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                return;
            }

            city.updateRank(rank, new CityRank(rank.getRankUUID(), city.getUUID(), input, rank.getPriority(), rank.getPermissionsSet(), rank.getIcon()));
            MessagesManager.sendMessage(player, Component.text("Le nom du grade a été mis à jour : " + oldName + " → " + input), Prefix.CITY, MessageType.SUCCESS, false);
        });
    }

    public static void deleteRank(Player player, String rankName) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        if (!CityRankCondition.canDeleteRank(city, player, rankName)) {
            return;
        }

        CityRank rank = city.getRankByName(rankName);
        if (rank == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.CITYRANKS_NOTEXIST.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        new ConfirmMenu(player, () -> {
            try {
                if (!CityRankCondition.canDeleteRank(city, player, rankName)) {
                    return;
                }

                city.deleteRank(rank);
                player.closeInventory();
                MessagesManager.sendMessage(player, Component.text("Grade " + rank.getName() + " supprimé avec succès !"), Prefix.CITY, MessageType.SUCCESS, false);
            } catch (IllegalArgumentException e) {
                MessagesManager.sendMessage(player, Component.text("Impossible de supprimer le grade : " + e.getMessage()), Prefix.CITY, MessageType.ERROR, false);
            }
        }, () -> {
            if (!CityRankCondition.canDeleteRank(city, player, rankName)) {
                return;
            }

            new CityRankDetailsMenu(player, city, rank).open();
        }, List.of(Component.text("§cCette action est irréversible")), List.of()).open();
    }

    public static void assignRank(Player player, String rankName, OfflinePlayer member) {
        City city = CityManager.getPlayerCity(player.getUniqueId());
        if (city == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }
        if (!city.hasPermission(player.getUniqueId(), CPermission.ASSIGN_RANKS)) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOACCESSPERMS.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }
        CityRank rank = city.getRankByName(rankName);
        if (member == null && rank == null) {
            new CityRankMemberMenu(player, city).open();
            return;
        } else if (member == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOTFOUND.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        } else if (rank == null) {
            MessagesManager.sendMessage(player, MessagesManager.Message.CITYRANKS_NOTEXIST.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }

        city.changeRank(player, member.getUniqueId(), rank);
    }
}