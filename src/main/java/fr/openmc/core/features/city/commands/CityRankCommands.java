package fr.openmc.core.features.city.commands;

import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.actions.CityRankAction;
import fr.openmc.core.features.city.menu.ranks.CityRankDetailsMenu;
import fr.openmc.core.features.city.menu.ranks.CityRanksMenu;
import fr.openmc.core.features.city.models.CityRank;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"city ranks", "ville grades"})
public class CityRankCommands {
	
	@DefaultFor("~")
	@CommandPermission("omc.commands.city.rank")
	public void rank(Player player) {
		City city = CityManager.getPlayerCity(player.getUniqueId());

		if (city == null) {
			MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}

		new CityRanksMenu(player, city).open();
	}
	
	@Subcommand("add")
	@CommandPermission("omc.commands.city.rank.add")
    public void add(Player player) {
        CityRankAction.beginCreateRank(player);
	}
	
	@Subcommand("edit")
	@CommandPermission("omc.commands.city.rank.edit")
	@AutoComplete("@city_ranks")
	public void edit(Player player, @Named("rank") String rankName) {
		City city = CityManager.getPlayerCity(player.getUniqueId());
		if (city == null) {
			MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		if (!city.hasPermission(player.getUniqueId(), CPermission.MANAGE_RANKS)) {
			MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOACCESSPERMS.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		CityRank rank = city.getRankByName(rankName);
		if (rank == null) {
			MessagesManager.sendMessage(player, MessagesManager.Message.CITYRANKS_NOTEXIST.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		new CityRankDetailsMenu(player, city, rank).open();
	}
	
	/**
	 * Swap a permission for a rank.
	 *
	 * @param player     The player who is swapping the permission.
	 * @param rank       The rank to swap the permission for.
	 * @param permission The permission to swap.
	 */
	public static void swapPermission(Player player, CityRank rank, CPermission permission) {
		City city = CityManager.getPlayerCity(player.getUniqueId());
		if (city == null) {
			MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
        if (!city.hasPermission(player.getUniqueId(), CPermission.PERMS)) {
			MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOACCESSPERMS.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		if (rank == null) {
			MessagesManager.sendMessage(player, MessagesManager.Message.CITYRANKS_NOTEXIST.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		
		rank.swapPermission(permission);
	}
	
	@Subcommand("assign")
	@CommandPermission("omc.commands.city.rank.assign")
	@AutoComplete("@city_ranks @city_members")
	public void assign(Player player, @Optional @Named("rank") String rankName, @Optional @Named("player") OfflinePlayer target) {
		CityRankAction.assignRank(player, rankName, target);
	}
	
	@Subcommand("rename")
	@CommandPermission("omc.commands.city.rank.rename")
	@AutoComplete("@city_ranks")
	public void rename(Player player, @Named("old") String rankName) {
		CityRankAction.renameRank(player, rankName);
	}
	
	@Subcommand("delete")
	@CommandPermission("omc.commands.city.rank.delete")
	@AutoComplete("@city_ranks")
	public void delete(Player player, @Named("rank") String rankName) {
		CityRankAction.deleteRank(player, rankName);
	}
}
