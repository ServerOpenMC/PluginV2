package fr.openmc.core.features.shops.commands;

import fr.openmc.core.features.shops.managers.ShopManager;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("shopadmin")
@CommandPermission("omc.admins.commands.shop")
public class ShopAdminCommand {
	
	@Subcommand("bypass")
	public void bypass(Player player) {
		if (!ShopManager.shopBypass.contains(player.getUniqueId())) ShopManager.shopBypass.add(player.getUniqueId());
		else ShopManager.shopBypass.remove(player.getUniqueId());
	}
}
