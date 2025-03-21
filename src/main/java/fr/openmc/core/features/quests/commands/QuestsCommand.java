package fr.openmc.core.features.quests.commands;

import fr.openmc.core.features.quests.PlayerQuests;
import fr.openmc.core.features.quests.QuestsManager;
import fr.openmc.core.features.quests.menu.QuestsMenu;
import fr.openmc.core.features.quests.menu.QuestsMenuTarget;
import fr.openmc.core.features.quests.qenum.QUESTS;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"quests", "quetes"})
@Description("Commandes liées aux quêtes")
@CommandPermission("omc.command.quests")
public class QuestsCommand {
	
	@DefaultFor("~")
	public void defaultCmd(Player player) {
		QuestsMenu menu = new QuestsMenu(player);
		menu.open();
	}
	
	@Subcommand("menu")
	@Description("Ouvrir le menu des quêtes")
	public void menuCmd(Player player) {
		QuestsMenu menu = new QuestsMenu(player);
		menu.open();
	}
	
	@Subcommand("progress get")
	@Description("Définir la progression d'une quête")
	@CommandPermission("omc.admins.commands.quests")
	public void getProgress(Player player, @Named("Target") OfflinePlayer target, @Named("Quests") @Optional QUESTS quests) {
		PlayerQuests pq = QuestsManager.getPlayerQuests(player.getUniqueId());
		if (quests == null) {
			QuestsMenuTarget menu = new QuestsMenuTarget(player, target.getPlayer());
			menu.open();
			return;
		}
		player.sendMessage("§aProgression de la quête §6" + quests.getName() + "§a : §6" + pq.getProgress(quests));
	}
	
	@Subcommand("progress set")
	@Description("Définir la progression d'une quête")
	@CommandPermission("omc.admins.commands.quests")
	public void setProgress(Player player, @Named("Target") OfflinePlayer target, @Named("Quests") QUESTS quests, @Named("Progress") int progress) {
		PlayerQuests pq = QuestsManager.getPlayerQuests(target.getUniqueId());
		pq.setProgress(quests, progress);
		player.sendMessage("§aProgression de la quête §6" + quests.getName() + "§a définie à §6" + progress);
	}
	
	@Subcommand("progress add")
	@Description("Ajouter de la progression à une quête")
	@CommandPermission("omc.admins.commands.quests")
	public void addProgress(Player player, @Named("Target") OfflinePlayer target, @Named("Quests") QUESTS quests, @Named("Progress") int progress) {
		PlayerQuests pq = QuestsManager.getPlayerQuests(target.getUniqueId());
		pq.addProgress(quests, progress);
		player.sendMessage("§aProgression de la quête §6" + quests.getName() + "§a augmentée de §6" + progress);
	}
	
	@Subcommand("progress remove")
	@Description("Retirer de la progression à une quête")
	@CommandPermission("omc.admins.commands.quests")
	public void removeProgress(Player player, @Named("Target") OfflinePlayer target, @Named("Quests") QUESTS quests, @Named("Progress") int progress) {
		PlayerQuests pq = QuestsManager.getPlayerQuests(target.getUniqueId());
		pq.removeProgress(quests, progress);
		player.sendMessage("§aProgression de la quête §6" + quests.getName() + "§a diminuée de §6" + progress);
	}
	
	@Subcommand("progress reset")
	@Description("Réinitialiser la progression d'une quête")
	@CommandPermission("omc.admins.commands.quests")
	public void resetProgress(Player player, @Named("Target") OfflinePlayer target, @Named("Quests") QUESTS quests) {
		PlayerQuests pq = QuestsManager.getPlayerQuests(target.getUniqueId());
		pq.resetProgress(quests);
		player.sendMessage("§aProgression de la quête §6" + quests.getName() + "§a réinitialisée");
	}
	
}