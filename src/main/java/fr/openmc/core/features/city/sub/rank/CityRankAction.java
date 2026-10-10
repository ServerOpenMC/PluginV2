package fr.openmc.core.features.city.sub.rank;

import fr.openmc.api.menulib.template.ConfirmMenu;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.core.features.city.models.CityPermission;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.models.db.DBCityRank;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.features.city.sub.rank.menus.CityRankDetailsMenu;
import fr.openmc.core.features.city.sub.rank.menus.CityRankMemberMenu;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class CityRankAction {
	private static final int MAX_LENGTH_RANK_NAME = 16;
	
	/**
	 * Begin the process to create a new rank.
	 *
	 * @param player The player who wants to create a rank.
	 */
	public static void beginCreateRank(OMCPlayer player) {
		City city = City.ofPlayer(player.getUniqueId());
		if (!CityRankCondition.canCreateRank(city, player)) return;

		player.inputs().sendStringDialogInput(TranslationManager.translation("feature.city.rank.prompt.create"), MAX_LENGTH_RANK_NAME, input -> {
					if (input == null) return;
					
					CityRankAction.afterCreateRank(player, input);
				}
		);
	}
	
	/**
	 * After the player has entered the rank name, open the rank details menu.
	 *
	 * @param player   The player who wants to create a rank.
	 * @param rankName The name of the rank to create.
	 */
	public static void afterCreateRank(OMCPlayer player, String rankName) {
		City city = City.ofPlayer(player.getUniqueId());
		if (!CityRankCondition.canCreateRank(city, player)) return;
		
		if (city.isRankExists(rankName)) {
			player.message().sendError(TranslationManager.translation("feature.city.grade.already_exist"), Prefix.CITY, false);
			return;
		}
		
		new CityRankDetailsMenu(player, city, rankName).open();
	}
	
	/**
	 * Rename a rank from the menu.
	 *
	 * @param player  The player who wants to rename a rank.
	 * @param oldRank The old rank to pass.
	 * @param newRank The new rank to rename.
	 */
	public static void renameRankFromMenu(OMCPlayer player, DBCityRank oldRank, DBCityRank newRank) {
		City city = City.ofPlayer(player.getUniqueId());
		if (city == null) {
			player.message().sendError(TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, false);
			return;
		}
		if (oldRank == null || newRank == null) {
			player.message().sendError(TranslationManager.translation("feature.city.grade.cannot_exist"), Prefix.CITY, false);
			return;
		}
		if (!CityRankCondition.canRenameRank(city, player, newRank.getName())) return;
		player.inputs().sendStringDialogInput(TranslationManager.translation("feature.city.rank.prompt.rename"), MAX_LENGTH_RANK_NAME, input -> {
			if (input == null) return;
			
			if (!CityRankCondition.canRenameRank(city, player, newRank.getName())) return;
			
			new CityRankDetailsMenu(player, city, oldRank, new DBCityRank(newRank.getRankUUID(), newRank.getCityUUID(), newRank.getPriority(), input, newRank.getIcon(), newRank.getPermissionsSet(), newRank.getMembersSet())).open();
		});
	}
	
	/**
	 * Rename a rank.
	 *
	 * @param player  The player who wants to rename a rank.
	 * @param oldName The old name of the rank to rename.
	 */
	public static void renameRank(OMCPlayer player, String oldName) {
		City city = City.ofPlayer(player.getUniqueId());
		if (!CityRankCondition.canRenameRank(city, player, oldName)) {
			return;
		}
		
		player.inputs().sendStringDialogInput(TranslationManager.translation("feature.city.rank.prompt.rename"), MAX_LENGTH_RANK_NAME, input -> {
			if (input == null) return;
			
			if (!CityRankCondition.canRenameRank(city, player, oldName)) {
				return;
			}
			
			DBCityRank rank = city.getRankByName(oldName);
			if (rank == null) {
				player.message().sendError(TranslationManager.translation("feature.city.grade.cannot_exist"), Prefix.CITY, false);
				return;
			}
			
			city.updateRank(rank, new DBCityRank(rank.getRankUUID(), city.getUniqueId(), rank.getPriority(), input, rank.getIcon(), rank.getPermissionsSet(), rank.getMembersSet()));
			player.message().sendSuccess(TranslationManager.translation(
					"feature.city.rank.rename.success",
					Component.text(oldName).color(NamedTextColor.YELLOW),
					Component.text(input).color(NamedTextColor.YELLOW)
			), Prefix.CITY, false);
		});
	}
	
	/**
	 * Delete a rank.
	 *
	 * @param player   The player who wants to delete a rank.
	 * @param rankName The name of the rank to delete.
	 */
	public static void deleteRank(OMCPlayer player, String rankName) {
		City city = City.ofPlayer(player.getUniqueId());
		if (city == null) {
			player.message().sendError(TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, false);
			return;
		}
		
		if (CityRankCondition.canDeleteRank(city, player, rankName)) return;
		
		DBCityRank rank = city.getRankByName(rankName);
		if (rank == null) {
			player.message().sendError(TranslationManager.translation("feature.city.grade.cannot_exist"), Prefix.CITY, false);
			return;
		}
		
		new ConfirmMenu(player, () -> {
			try {
				if (!CityRankCondition.canDeleteRank(city, player, rankName)) {
					return;
				}
				
				city.deleteRank(rank);
				player.closeInventory();
				player.message().sendSuccess(TranslationManager.translation(
						"feature.city.rank.delete.success",
						Component.text(rank.getName()).color(NamedTextColor.YELLOW)
				), Prefix.CITY, false);
			} catch (IllegalArgumentException e) {
				player.message().sendError(TranslationManager.translation(
						"feature.city.rank.delete.error",
						Component.text(e.getMessage()).color(NamedTextColor.RED)
				), Prefix.CITY, false);
			}
		}, () -> {
			if (!CityRankCondition.canDeleteRank(city, player, rankName)) return;
			
			new CityRankDetailsMenu(player, city, rank).open();
		}, List.of(TranslationManager.translation("feature.city.rank.delete.confirm.lore")), List.of()).open();
	}
	
	/**
	 * Assign a rank to a member.
	 *
	 * @param player   The player who is assigning the rank.
	 * @param rankName The name of the rank to assign.
	 * @param member   The member to assign the rank to.
	 */
	public static void assignRank(OMCPlayer player, String rankName, OfflinePlayer member) {
		City city = City.ofPlayer(player.getUniqueId());
		if (city == null) {
			player.message().sendError(TranslationManager.translation("messages.city.player_no_in_city"), Prefix.CITY, false);
			return;
		}
		
		if (!FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.RANK)) {
			player.message().sendError(TranslationManager.translation(
					"messages.city.havent_unlocked_feature",
					Component.text(FeaturesRewards.getFeatureUnlockLevel(FeaturesRewards.Feature.RANK))
			), Prefix.CITY, false);
			return;
		}
		
		if (!city.hasPermission(player.getUniqueId(), CityPermission.ASSIGN_RANKS)) {
			player.message().sendError(TranslationManager.translation("messages.city.player_no_permission_access"), Prefix.CITY, false);
			return;
		}
		DBCityRank rank = city.getRankByName(rankName);
		if (member == null && rank == null) {
			new CityRankMemberMenu(player, city).open();
			return;
		} else if (member == null) {
			player.message().sendError(TranslationManager.translation("messages.global.player_not_found"), Prefix.CITY, false);
			return;
		} else if (rank == null) {
			player.message().sendError(TranslationManager.translation("feature.city.grade.cannot_exist"), Prefix.CITY, false);
			return;
		}

		if (!CityRankCondition.canModifyRankPermissions(city, player, rank.getPriority())) return;
		
		city.changeRank(player, member.getUniqueId(), rank);
	}
}