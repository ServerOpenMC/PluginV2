package fr.openmc.core.features.city.sub.rank;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.commands.autocomplete.CityMembersAutoComplete;
import fr.openmc.core.features.city.commands.autocomplete.CityRanksAutoComplete;
import fr.openmc.core.features.city.models.DBCityRank;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.features.city.sub.rank.menus.CityRankDetailsMenu;
import fr.openmc.core.features.city.sub.rank.menus.CityRanksMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"city ranks", "ville grades"})
public class CityRankCommands {

	@CommandPlaceholder()
	@CommandPermission("omc.commands.city.rank")
	public void rank(Player player) {
		City city = CityManager.getPlayerCity(player.getUniqueId());

		if (city == null) {
			MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}

		if (!FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.RANK)) {
			MessagesManager.sendMessage(player, Component.text("Vous n'avez pas débloqué cette feature ! Veuillez améliorer votre ville au niveau " + FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.RANK) + " !"), Prefix.CITY, MessageType.ERROR, false);
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
	public void edit(
			Player player,
			@Named("rank") @SuggestWith(CityRanksAutoComplete.class) String rankName
	) {
		City city = CityManager.getPlayerCity(player.getUniqueId());
		if (city == null) {
			MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		DBCityRank rank = city.getRankByName(rankName);
		if (rank == null) {
			MessagesManager.sendMessage(player, MessagesManager.Message.CITY_RANKS_NOT_EXIST.getMessage(), Prefix.CITY, MessageType.ERROR, false);
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
	public static void swapPermission(Player player, DBCityRank rank, CityPermission permission) {
		City city = CityManager.getPlayerCity(player.getUniqueId());
		if (city == null) {
			MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
        if (!city.hasPermission(player.getUniqueId(), CityPermission.PERMS)) {
			MessagesManager.sendMessage(player, MessagesManager.Message.CITY_CANNOT_ACCESS_PERMS.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		if (rank == null) {
			MessagesManager.sendMessage(player, MessagesManager.Message.CITY_RANKS_NOT_EXIST.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		if (!CityRankCondition.canModifyRankPermissions(city, player, rank.getPriority())) {
			return;
		}
		
		rank.swapPermission(permission);
	}
	
	/**
	 * Add all permissions to a rank.
	 *
	 * @param player The player who is adding the permissions.
	 * @param rank   The rank to add the permissions to.
	 */
	public static void addAllPermissions(Player player, DBCityRank rank) {
		City city = CityManager.getPlayerCity(player.getUniqueId());
		if (city == null) {
			MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		if (!city.hasPermission(player.getUniqueId(), CityPermission.PERMS)) {
			MessagesManager.sendMessage(player, MessagesManager.Message.CITY_CANNOT_ACCESS_PERMS.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		if (rank == null) {
			MessagesManager.sendMessage(player, MessagesManager.Message.CITY_RANKS_NOT_EXIST.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		if (!CityRankCondition.canModifyRankPermissions(city, player, rank.getPriority())) {
			return;
		}
		
		rank.addAllPermissions();
	}
	
	/**
	 * Remove all permissions from a rank.
	 *
	 * @param player The player who is removing the permissions.
	 * @param rank   The rank to remove the permissions from.
	 */
	public static void removeAllPermissions(Player player, DBCityRank rank) {
		City city = CityManager.getPlayerCity(player.getUniqueId());
		if (city == null) {
			MessagesManager.sendMessage(player, MessagesManager.Message.PLAYER_NO_CITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		if (!city.hasPermission(player.getUniqueId(), CityPermission.PERMS)) {
			MessagesManager.sendMessage(player, MessagesManager.Message.CITY_CANNOT_ACCESS_PERMS.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		if (rank == null) {
			MessagesManager.sendMessage(player, MessagesManager.Message.CITY_RANKS_NOT_EXIST.getMessage(), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		if (!CityRankCondition.canModifyRankPermissions(city, player, rank.getPriority())) {
			return;
		}
		
		rank.clearPermissions();
	}
	
	@Subcommand("assign")
	@CommandPermission("omc.commands.city.rank.assign")
	public void assign(Player player, @Optional @Named("rank") @SuggestWith(CityRanksAutoComplete.class) String rankName, @Optional @Named("player") @SuggestWith(CityMembersAutoComplete.class) OfflinePlayer target) {
		CityRankAction.assignRank(player, rankName, target);
	}
	
	@Subcommand("rename")
	@CommandPermission("omc.commands.city.rank.rename")
	public void rename(Player player, @Named("old") @SuggestWith(CityRanksAutoComplete.class) String rankName) {
		CityRankAction.renameRank(player, rankName);
	}
	
	@Subcommand("delete")
	@CommandPermission("omc.commands.city.rank.delete")
	public void delete(Player player, @Named("rank") @SuggestWith(CityRanksAutoComplete.class) String rankName) {
		CityRankAction.deleteRank(player, rankName);
	}
}
