package fr.openmc.core.features.city.menu.ranks;

import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.commands.CityRankCommands;
import fr.openmc.core.features.city.models.CityRank;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CityRankPermsMenu {
	
	public static void openBook(Player sender, CityRank rank) {
		City city = CityManager.getPlayerCity(sender.getUniqueId());
		
		if (city == null) {
			MessagesManager.sendMessage(sender, Component.text("Tu n'habites dans aucune ville"), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		
		if (! city.hasPermission(sender.getUniqueId(), CPermission.PERMS)) {
			MessagesManager.sendMessage(sender, Component.text("Tu n'as pas la permission d'ouvrir ce menu"), Prefix.CITY, MessageType.ERROR, false);
			return;
		}
		
		ArrayList<Component> pages = new ArrayList<>();
		
		Component firstPage = Component.text("  Permissions grade").append(
						Component.text("\n\n")
								.decorate(TextDecoration.UNDERLINED)
								.decorate(TextDecoration.BOLD));
		
		ArrayList<Component> perms = new ArrayList<>();
		
		for (CPermission permission : CPermission.values()) {
			if (permission == CPermission.OWNER) continue;
			
			perms.add(Component.text((rank.getPermissions().contains(permission) ? "+ " : "- ") + permission.getDisplayName())
					.decoration(TextDecoration.UNDERLINED, false)
					.decoration(TextDecoration.BOLD, false)
					.clickEvent(ClickEvent.callback((plr1) -> {
						CityRankCommands.swapPermission(sender, rank, permission);
						sender.closeInventory();
						openBook(sender, rank);
					}))
					.color(rank.getPermissions().contains(permission) ? NamedTextColor.DARK_GREEN : NamedTextColor.RED)
					.append(Component.newline()));
		}
		
		for (int i = 0; i < 9 && ! perms.isEmpty(); i++) {
			firstPage = firstPage.append(perms.removeFirst());
		}
		
		firstPage = firstPage.append(Component.text("⬅ Retour")
				.clickEvent(ClickEvent.callback((plr1) -> {
					sender.closeInventory();
					new CityRankDetailsMenu(sender, city, rank).open();
				}))
				.color(NamedTextColor.BLACK));
		
		pages.add(firstPage);
		
		while (! perms.isEmpty()) {
			Component page = Component.text("");
			
			for (int i = 0; i < 9 && ! perms.isEmpty(); i++) {
				page = page.append(perms.removeFirst());
			}
			
			pages.add(page);
		}
		
		sender.openBook(Book.book(Component.text(""), Component.text(""), pages));
	}
}
