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
import net.kyori.adventure.text.Component;
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
		if (! city.hasPermission(player.getUniqueId(), CPermission.PERMS) && ! city.hasPermission(player.getUniqueId(), CPermission.OWNER)) {
			MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOACCESSPERMS.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		new CityRanksMenu(player, city).open();
	}
	
	public static void swapPermission(Player player, CityRank rank, CPermission permission) {
		City city = CityManager.getPlayerCity(player.getUniqueId());
		if (city == null) {
			MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		if (! city.hasPermission(player.getUniqueId(), CPermission.PERMS) && ! city.hasPermission(player.getUniqueId(), CPermission.OWNER)) {
			MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOACCESSPERMS.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		if (rank == null) {
			MessagesManager.sendMessage(player, MessagesManager.Message.CITYRANKS_NOTEXIST.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		
		rank.swapPermission(permission);
	}
	
	@Subcommand("add")
	@CommandPermission("omc.commands.city.rank.add")
	public void add(Player player, @Named("rank") String rankName) {
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
		if (city.isRankExists(rankName)) {
			MessagesManager.sendMessage(player, MessagesManager.Message.CITYRANKS_ALREADYEXIST.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		
		new CityRankDetailsMenu(player, city, rankName).open();
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
	@AutoComplete("@city_ranks")
	public void delete(Player player, @Named("rank") String rankName) {
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
		
		try {
			city.deleteRank(rank);
			MessagesManager.sendMessage(player, Component.text("Grade " + rankName + " supprimé avec succès !"), Prefix.CITY, MessageType.SUCCESS, false);
		} catch (IllegalArgumentException e) {
			MessagesManager.sendMessage(player, Component.text("Impossible de supprimer le grade : " + e.getMessage()), Prefix.CITY, MessageType.ERROR, false);
		}
	}
	
	@Subcommand("assign")
	@CommandPermission("omc.commands.city.rank.assign")
	@AutoComplete("@city_ranks @city_members")
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
	
	@Subcommand("rename")
	@CommandPermission("omc.commands.city.rank.rename")
	@AutoComplete("@city_ranks")
	public void rename(Player player, @Named("old") String rankName, @Named("new") String newName) {
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
		
		city.updateRank(rank, new CityRank(newName, rank.getPriority(), rank.getPermissions(), rank.getIcon()));
		MessagesManager.sendMessage(player, Component.text("Le nom du grade a été mis à jour : " + rankName + " → " + newName), Prefix.CITY, MessageType.SUCCESS, false);
	}
}
