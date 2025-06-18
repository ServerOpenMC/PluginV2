package fr.openmc.core.features.city.menu.ranks;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.utils.CacheOfflinePlayer;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CityRankMemberMenu extends PaginatedMenu {
	
	private City city;
	
	public CityRankMemberMenu(Player owner, City city) {
		super(owner);
		this.city = city;
	}
	
	@Override
	public @Nullable Material getBorderMaterial() {
		return Material.GRAY_STAINED_GLASS_PANE;
	}
	
	@Override
	public @NotNull List<Integer> getStaticSlots() {
		return List.of();
	}
	
	@Override
	public @NotNull List<ItemStack> getItems() {
		List<ItemStack> items = new ArrayList<>();
		Set<UUID> members = city.getMembers();
		for (UUID uuid : members) {
			OfflinePlayer player = CacheOfflinePlayer.getOfflinePlayer(uuid);
			if (player == null || ! player.hasPlayedBefore()) {
				continue; // Skip if player data is not available
			}
			items.add(new ItemBuilder(this, ItemUtils.getPlayerSkull(uuid), itemMeta -> {
				itemMeta.displayName(Component.text(player.getName() != null ? player.getName() : "§c§oJoueur inconnu"));
				itemMeta.lore(List.of(
						Component.text("§7Cliquez pour voir les détails"),
						Component.text("§7Grade : §e" + (city.getRankOfMember(uuid) != null ? city.getRankOfMember(uuid).getName() : "§oAucun"))
				));
			}).setOnClick(event -> {
				if (event.getWhoClicked() instanceof Player p) {
					if (! city.hasPermission(getOwner().getUniqueId(), CPermission.PERMS) || ! city.hasPermission(p.getUniqueId(), CPermission.OWNER)) {
						MessagesManager.sendMessage(getOwner(), MessagesManager.Message.PLAYERNOACCESSPERMS.getMessage(), Prefix.CITY, MessageType.ERROR, false);
						getOwner().closeInventory();
						return;
					}
					new CityRankAssignMenu(getOwner(), uuid, city).open();
				}
			}));
		}
		return items;
	}
	
	@Override
	public Map<Integer, ItemStack> getButtons() {
		Map<Integer, ItemStack> map = new HashMap<>();
		map.put(48, new ItemBuilder(this, CustomStack.getInstance("_iainternal:icon_back_orange")
				.getItemStack(), itemMeta -> itemMeta.displayName(Component.text("§cPage précédente"))).setPreviousPageButton());
		map.put(50, new ItemBuilder(this, CustomStack.getInstance("_iainternal:icon_next_orange")
				.getItemStack(), itemMeta -> itemMeta.displayName(Component.text("§aPage suivante"))).setNextPageButton());
		return map;
	}
	
	@Override
	public @NotNull String getName() {
		return "Liste des membres";
	}
	
	@Override
	public void onInventoryClick(InventoryClickEvent e) {
	
	}
	
	@Override
	public void onClose(InventoryCloseEvent event) {
	
	}
	
	@Override
	public List<Integer> getTakableSlot() {
		return List.of();
	}
}
