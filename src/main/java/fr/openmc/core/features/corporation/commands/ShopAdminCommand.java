package fr.openmc.core.features.corporation.commands;

import fr.openmc.core.features.corporation.commands.autocomplete.ShopAdminCommandPlayerAutocomplete;
import fr.openmc.core.features.corporation.manager.PlayerShopManager;
import fr.openmc.core.features.corporation.manager.ShopManager;
import fr.openmc.core.features.corporation.models.Shop;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("shopadmin")
@CommandPermission("omc.admins.commands.shop")
public class ShopAdminCommand {
	
	@Subcommand("database query shops")
	public void databaseQueryShops(Player player, @Optional @Named("playerShop") @SuggestWith(ShopAdminCommandPlayerAutocomplete.class) OfflinePlayer playerShop) {
		if (playerShop == null) {
			if (ShopManager.loadShops()) {
				MessagesManager.sendMessage(player, Component.text("§aChargement des shops depuis la base de données réussi"), Prefix.SHOP, MessageType.SUCCESS, false);
			} else {
				MessagesManager.sendMessage(player, Component.text("§cÉchec du chargement des shops depuis la base de données (Error in console)"), Prefix.SHOP, MessageType.ERROR, false);
			}
		} else {
			if (ShopManager.loadShopFor(playerShop)) {
				MessagesManager.sendMessage(player, Component.text("§aChargement du shop du joueur §6" + playerShop.getName() + "§a depuis la base de données réussi"), Prefix.SHOP, MessageType.SUCCESS, false);
			} else {
				MessagesManager.sendMessage(player, Component.text("§cÉchec du chargement du shop du joueur §6" + playerShop.getName() + "§c depuis la base de données (Error in console)"), Prefix.SHOP, MessageType.ERROR, false);
			}
		}
	}
	
	@Subcommand("database query shop_sales")
	public void databaseQueryShopSales(Player player, @Optional @Named("playerShop") @SuggestWith(ShopAdminCommandPlayerAutocomplete.class) OfflinePlayer playerShop) {
	
	}
	
	@Subcommand("database query shop_items")
	public void databaseQueryShopItems(Player player, @Optional @Named("playerShop") @SuggestWith(ShopAdminCommandPlayerAutocomplete.class) OfflinePlayer playerShop) {
	
	}
	
	@Subcommand("database save shops")
	public void databaseSaveShops(Player player, @Optional @Named("playerShop") @SuggestWith(ShopAdminCommandPlayerAutocomplete.class) OfflinePlayer playerShop) {
		if (playerShop == null) {
			if (ShopManager.saveShops()) {
				MessagesManager.sendMessage(player, Component.text("§aSauvegarde des shops dans la base de données réussi"), Prefix.SHOP, MessageType.SUCCESS, false);
			} else {
				MessagesManager.sendMessage(player, Component.text("§cÉchec de la sauvegarde des shops dans la base de données (Error in console)"), Prefix.SHOP, MessageType.ERROR, false);
			}
		} else {
			if (ShopManager.saveShopFor(playerShop)) {
				MessagesManager.sendMessage(player, Component.text("§aSauvegarde du shop du joueur §6" + playerShop.getName() + "§a dans la base de données réussi"), Prefix.SHOP, MessageType.SUCCESS, false);
			} else {
				MessagesManager.sendMessage(player, Component.text("§cÉchec de la sauvegarde du shop du joueur §6" + playerShop.getName() + "§c dans la base de données (Error in console)"), Prefix.SHOP, MessageType.ERROR, false);
			}
		}
	}
	
	@Subcommand("database save shop_sales")
	public void databaseSaveShopSales(Player player, @Optional @Named("playerShop") @SuggestWith(ShopAdminCommandPlayerAutocomplete.class) OfflinePlayer playerShop) {
	
	}
	
	@Subcommand("database save shop_items")
	public void databaseSaveShopItems(Player player, @Optional @Named("playerShop") @SuggestWith(ShopAdminCommandPlayerAutocomplete.class) OfflinePlayer playerShop) {
	
	}
	
	@Subcommand("removeshop")
	public void removeShop(Player player, @Named("playerShop") @SuggestWith(ShopAdminCommandPlayerAutocomplete.class) OfflinePlayer target) {
		if (target == null) {
			MessagesManager.sendMessage(player, Component.text("§cLe joueur spécifié est introuvable"), Prefix.SHOP, MessageType.ERROR, false);
			return;
		}
		
		if (!ShopManager.hasShop(target.getUniqueId())) {
			MessagesManager.sendMessage(player, Component.text("§cLe joueur spécifié n'a pas de shop"), Prefix.SHOP, MessageType.ERROR, false);
			return;
		}
		
		Shop shop = ShopManager.getPlayerShop(target.getUniqueId());
		if (shop == null) {
			MessagesManager.sendMessage(player, Component.text("§cLe shop du joueur spécifié est introuvable"), Prefix.SHOP, MessageType.ERROR, false);
			return;
		}
		
		if (!shop.getItems().isEmpty()) {
			MessagesManager.sendMessage(player, Component.text("§cLe shop du joueur spécifié n'est pas vide"), Prefix.SHOP, MessageType.ERROR, false);
			return;
		}
		
		PlayerShopManager.adminDeleteShop(target, player);
	}
}
