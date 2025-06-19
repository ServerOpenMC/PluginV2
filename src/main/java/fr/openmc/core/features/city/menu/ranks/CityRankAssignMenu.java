package fr.openmc.core.features.city.menu.ranks;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.models.CityRank;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CityRankAssignMenu extends Menu {
	
	private UUID playerUUID;
	private City city;
	
	public CityRankAssignMenu(Player owner, UUID playerUUID, City city) {
		super(owner);
		this.playerUUID = playerUUID;
		this.city = city;
	}
	
	@Override
	public @NotNull String getName() {
		return "Assigner un grade";
	}
	
	@Override
	public @NotNull InventorySize getInventorySize() {
		return InventorySize.SMALL;
	}
	
	@Override
	public void onInventoryClick(InventoryClickEvent e) {
	
	}
	
	@Override
	public void onClose(InventoryCloseEvent event) {
	
	}
	
	@Override
	public @NotNull Map<Integer, ItemStack> getContent() {
		Map<Integer, ItemStack> map = new HashMap<>();
		
		Set<CityRank> availableRanks = city.getRanks();
		for (CityRank rank : availableRanks) {
			map.put(map.size(), new ItemBuilder(this, new ItemStack(rank.getIcon()), itemMeta -> {
				itemMeta.displayName(Component.text(rank.getName()));
				itemMeta.lore(List.of(
						Component.text("§7Permissions : " + (rank.getPermissions().isEmpty() ? "§cAucune" : "§a" + rank.getPermissions().size() + " permission(s)"))
				));
			}).setOnClick(event -> {
				if (event.getWhoClicked() instanceof Player player) {
					if (! city.hasPermission(player.getUniqueId(), CPermission.PERMS) || ! city.hasPermission(playerUUID, CPermission.OWNER)) {
						MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOACCESSPERMS.getMessage(), Prefix.CITY, MessageType.ERROR, false);
						player.closeInventory();
						return;
					}
					city.changeRank(getOwner(), playerUUID, rank);
					player.closeInventory();
				}
			}));
		}
		
		return map;
	}
	
	@Override
	public List<Integer> getTakableSlot() {
		return List.of();
	}
}
