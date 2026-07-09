package fr.openmc.core.features.shops.commands;

import fr.openmc.core.features.shops.manager.ShopManager;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("shopadmin")
@CommandPermission("omc.admins.commands.shop")
public class ShopAdminCommand {
	
	/*@Subcommand("multiblock set")
	public void setShopMultiblock(Player player, @Named("playerShop") @SuggestWith(ShopAdminCommandPlayerAutocomplete.class) OfflinePlayer playerShop, @Named("barrelLoc") Location barrelLoc) {
		if (playerShop == null) return;
		
		Shop shop = ShopManager.getPlayerShop(playerShop.getUniqueId());
		if (shop == null) {
			MessagesManager.sendMessage(player, Component.text("§cCe joueur n'a pas de shop, ou n'a pas été trouvé."), Prefix.SHOP, MessageType.ERROR, false);
			return;
		}
		
		Location cashLoc = barrelLoc.clone().add(0, 1, 0);
		
		Shop.Multiblock multiblock = shop.getMultiblock();
		if (multiblock == null) {
			multiblock = new Shop.Multiblock(barrelLoc, cashLoc);
			if (!shop.setMultiblock(multiblock)) {
				MessagesManager.sendMessage(player, Component.text("§cImpossible de poser le multiblock car un des blocks n'est pas valide."), Prefix.SHOP, MessageType.ERROR, false);
				return;
			}
			MessagesManager.sendMessage(player, Component.text("§aMultiblock associé au shop."), Prefix.SHOP, MessageType.SUCCESS, false);
			return;
		}
		
		Location shopBarrelLoc = multiblock.stockBlockLoc();
		if (shopBarrelLoc != null) {
			if (shopBarrelLoc == barrelLoc) {
				MessagesManager.sendMessage(player, Component.text("§cCe barrel est déjà associé au shop."), Prefix.SHOP, MessageType.WARNING, false);
			} else {
				if (shopBarrelLoc.getBlock().getType() != Material.BARREL) {
					MessagesManager.sendMessage(player, Component.text("§cAucune barrel détecté."), Prefix.SHOP, MessageType.ERROR, false);
					return;
				}
				shopBarrelLoc.set(barrelLoc.x(), barrelLoc.y(), barrelLoc.z());
				MessagesManager.sendMessage(player, Component.text("§aBarrel associé au shop."), Prefix.SHOP, MessageType.SUCCESS, false);
			}
		} else {
			shopBarrelLoc = barrelLoc;
			MessagesManager.sendMessage(player, Component.text("§aBarrel associé au shop."), Prefix.SHOP, MessageType.SUCCESS, false);
		}
		
		Location shopCashLoc = multiblock.cashBlockLoc();
		if (shopCashLoc != null) {
			if (shopCashLoc == cashLoc) {
				MessagesManager.sendMessage(player, Component.text("§cCe cash est déjà associé au shop."), Prefix.SHOP, MessageType.WARNING, false);
			} else {
				if (shopCashLoc.getBlock().getType() != Material.OAK_SIGN && !ShopFurniture.hasFurniture(shopCashLoc.getBlock())) {
					MessagesManager.sendMessage(player, Component.text("§cAucune cash ou panneau détecté."), Prefix.SHOP, MessageType.ERROR, false);
					return;
				}
				shopCashLoc.set(cashLoc.x(), cashLoc.y(), cashLoc.z());
				MessagesManager.sendMessage(player, Component.text("§aCash associé au shop."), Prefix.SHOP, MessageType.SUCCESS, false);
			}
		} else {
			shopCashLoc = cashLoc;
			MessagesManager.sendMessage(player, Component.text("§aCash associé au shop."), Prefix.SHOP, MessageType.SUCCESS, false);
		}
		
		shop.setMultiblock(new Shop.Multiblock(shopBarrelLoc, shopCashLoc));
		MessagesManager.sendMessage(player, Component.text("§aMultiblock associé au shop."), Prefix.SHOP, MessageType.SUCCESS, false);
	}*/
	
	/*@Subcommand("removeshop")
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
		
		if (shop.getItem().getAmount() > 0) {
			MessagesManager.sendMessage(player, Component.text("§cLe shop du joueur spécifié n'est pas vide"), Prefix.SHOP, MessageType.ERROR, false);
			return;
		}
		
		PlayerShopManager.adminDeleteShop(target, player);
	}*/
	
	@Subcommand("bypass")
	public void bypass(Player player) {
		if (!ShopManager.shopBypass.contains(player.getUniqueId())) ShopManager.shopBypass.add(player.getUniqueId());
		else ShopManager.shopBypass.remove(player.getUniqueId());
	}
}
