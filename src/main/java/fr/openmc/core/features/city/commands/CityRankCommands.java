package fr.openmc.core.features.city.commands;

import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityRank;
import fr.openmc.core.features.city.menu.ranks.CityRankDetailsMenu;
import fr.openmc.core.features.city.menu.ranks.CityRankMemberMenu;
import fr.openmc.core.features.city.menu.ranks.CityRanksMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"city rank", "ville rank"})
public class CityRankCommands {
	
	@DefaultFor("~")
	@CommandPermission("omc.commands.city.rank")
	public void rank(Player player) {
		City city = CityManager.getPlayerCity(player.getUniqueId());
		if (city == null) {
			MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		if (! city.hasPermission(player.getUniqueId(), CPermission.PERMS) && ! city.hasPermission(player.getUniqueId(), CPermission.OWNER)) {
			MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOACCESSPERMS.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		new CityRanksMenu(player, city).open();
	}
	
	@Subcommand("add")
	@CommandPermission("omc.commands.city.rank.add")
	public void add(Player player) {
		City city = CityManager.getPlayerCity(player.getUniqueId());
		if (city == null) {
			MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		if (! city.hasPermission(player.getUniqueId(), CPermission.PERMS) && ! city.hasPermission(player.getUniqueId(), CPermission.OWNER)) {
			MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOACCESSPERMS.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		if (city.getRanks().size() >= 18) {
			MessagesManager.sendMessage(player, MessagesManager.Message.CITYRANKS_MAX.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		new CityRankDetailsMenu(player, city).open();
	}
	
	@Subcommand("edit")
	@CommandPermission("omc.commands.city.rank.edit")
	public void edit(Player player, String rankName) {
		City city = CityManager.getPlayerCity(player.getUniqueId());
		if (city == null) {
			MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		if (! city.hasPermission(player.getUniqueId(), CPermission.PERMS) && ! city.hasPermission(player.getUniqueId(), CPermission.OWNER)) {
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
	
	@Subcommand("delete")
	@CommandPermission("omc.commands.city.rank.delete")
	public void delete(Player player, String rankName) {
		City city = CityManager.getPlayerCity(player.getUniqueId());
		if (city == null) {
			MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		if (! city.hasPermission(player.getUniqueId(), CPermission.PERMS) && ! city.hasPermission(player.getUniqueId(), CPermission.OWNER)) {
			MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOACCESSPERMS.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		CityRank rank = city.getRankByName(rankName);
		if (rank == null) {
			MessagesManager.sendMessage(player, MessagesManager.Message.CITYRANKS_NOTEXIST.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		
		//TODO implement delete rank logic
	}
	
	@Subcommand("assign")
	@CommandPermission("omc.commands.city.rank.assign")
	public void assign(Player player, @Optional @Named("rank") String rankName, @Optional @Named("player") Player target) {
		City city = CityManager.getPlayerCity(player.getUniqueId());
		if (city == null) {
			MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		if (! city.hasPermission(player.getUniqueId(), CPermission.PERMS) && ! city.hasPermission(player.getUniqueId(), CPermission.OWNER)) {
			MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOACCESSPERMS.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		if (target == null) {
			MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOTFOUND.getMessage(), Prefix.CITY, MessageType.ERROR, false);
		}
		CityRank rank = city.getRankByName(rankName);
		if (rank == null) {
			MessagesManager.sendMessage(player, MessagesManager.Message.CITYRANKS_NOTEXIST.getMessage(), Prefix.CITY, MessageType.ERROR, false);
		}
		new CityRankMemberMenu(player).open(); //TODO implement assign rank logic
	}
}
