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
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CityRankPermsMenu {

	/**
	 * Opens a book menu for the given player to manage permissions of a specific city rank.
	 *
	 * @param sender The player who is opening the book.
	 * @param rank   The city rank for which permissions are being managed.
	 */
	public static void openBook(Player sender, CityRank rank, boolean canEdit) {
		City city = CityManager.getPlayerCity(sender.getUniqueId());

		if (city == null) {
			MessagesManager.sendMessage(sender, Component.text("Tu n'habites dans aucune ville"), Prefix.CITY, MessageType.ERROR, false);
			return;
		}

		if (!city.hasPermission(sender.getUniqueId(), CPermission.PERMS)) {
			MessagesManager.sendMessage(sender, Component.text("Tu n'as pas la permission d'ouvrir ce menu"), Prefix.CITY, MessageType.ERROR, false);
			return;
		}

		List<Component> pages = new ArrayList<>();

		Component retourButton = Component.text("⬅ Retour")
				.clickEvent(ClickEvent.callback(plr -> {
					sender.closeInventory();
					new CityRankDetailsMenu(sender, city, rank).open();
				}))
				.color(NamedTextColor.BLACK);

		List<Component> currentLines = new ArrayList<>();
		int currentLineCount = 3;

		for (CPermission permission : CPermission.values()) {
			if (permission == CPermission.OWNER) continue;

			String display = (rank.getPermissionsSet().contains(permission) ? "+ " : "- ") + permission.getDisplayName();
			int estimatedLines = estimateLines(display);

			if (currentLineCount + estimatedLines + 1 > 12) {
				pages.add(buildPage(currentLines, retourButton));
				currentLines.clear();
				currentLineCount = 3;
			}

			Component permComponent = Component.text(display)
					.decoration(TextDecoration.UNDERLINED, false)
					.decoration(TextDecoration.BOLD, false)
					.clickEvent(ClickEvent.callback(plr -> {
						if (!canEdit) return;

						CityRankCommands.swapPermission(sender, rank, permission);
						sender.closeInventory();
						openBook(sender, rank, canEdit);
					}))
					.color(rank.getPermissionsSet().contains(permission) ? NamedTextColor.DARK_GREEN : NamedTextColor.RED)
					.append(Component.newline());

			currentLines.add(permComponent);
			currentLineCount += estimatedLines;
		}

		if (!currentLines.isEmpty()) {
			pages.add(buildPage(currentLines, retourButton));
		}

        sender.openBook(Book.book(Component.empty(), Component.empty(), pages));
	}

	private static Component buildPage(List<Component> lines, Component retourButton) {
        Component page = Component.empty()
				.append(Component.text("Permissions Grades\n\n")
						.decorate(TextDecoration.UNDERLINED)
						.decorate(TextDecoration.BOLD));

		int estimatedLineCount = 3;

		for (Component line : lines) {
			String plain = PlainTextComponentSerializer.plainText().serialize(line);
			int estimatedLines = estimateLines(plain);
			estimatedLineCount += estimatedLines;
			page = page.append(line);
		}

		while (estimatedLineCount < 12) {
			page = page.append(Component.newline());
			estimatedLineCount++;
		}

		page = page.append(retourButton);
		return page;
	}

	private static int estimateLines(String line) {
		return (line.length() / 24) + 1;
	}
}
