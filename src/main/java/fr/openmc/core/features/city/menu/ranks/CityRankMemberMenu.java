package fr.openmc.core.features.city.menu.ranks;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.utils.CacheOfflinePlayer;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
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

	private final City city;
	
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
    public List<ItemStack> getItems() {
		List<ItemStack> items = new ArrayList<>();
		Set<UUID> members = city.getMembers();
		for (UUID uuid : members) {
			OfflinePlayer player = CacheOfflinePlayer.getOfflinePlayer(uuid);
			if (player == null || ! player.hasPlayedBefore()) {
				continue;
			}

			String rankName = city.getRankName(uuid);

			List<Component> lore = new ArrayList<>();
			lore.add(Component.text("§7Grade : §e" + rankName).decoration(TextDecoration.ITALIC, false));
			if (!city.hasPermission(player.getUniqueId(), CPermission.OWNER)) {
				lore.add(Component.empty());
				lore.add(Component.text("§e§lCLIQUEZ ICI POUR ASSIGNER UN GRADE"));
			}
			items.add(new ItemBuilder(this, ItemUtils.getPlayerSkull(uuid), itemMeta -> {
				itemMeta.displayName(Component.text(player.getName() != null ? player.getName() : "§c§oJoueur inconnu").decoration(TextDecoration.ITALIC, false));
				itemMeta.lore(lore);
			}).setOnClick(event -> {
				if (city.hasPermission(player.getUniqueId(), CPermission.OWNER)) return;

				if (!city.hasPermission(getOwner().getUniqueId(), CPermission.ASSIGN_RANKS)) {
					MessagesManager.sendMessage(getOwner(), MessagesManager.Message.PLAYER_NO_ACCESS_PERMS.getMessage(), Prefix.CITY, MessageType.ERROR, false);
					getOwner().closeInventory();
					return;
				}

				new CityRankAssignMenu(getOwner(), uuid, city).open();
			}));
		}
		return items;
	}
	
	@Override
    public Map<Integer, ItemBuilder> getButtons() {
        Map<Integer, ItemBuilder> map = new HashMap<>();
		map.put(48, new ItemBuilder(this, CustomStack.getInstance("_iainternal:icon_back_orange")
				.getItemStack(), itemMeta -> itemMeta.displayName(Component.text("§cPage précédente"))).setPreviousPageButton());
		map.put(50, new ItemBuilder(this, CustomStack.getInstance("_iainternal:icon_next_orange")
				.getItemStack(), itemMeta -> itemMeta.displayName(Component.text("§aPage suivante"))).setNextPageButton());
		return map;
	}

	@Override
	public @NotNull InventorySize getInventorySize() {
		return InventorySize.LARGEST;
	}

	@Override
	public int getSizeOfItems() {
		return getItems().size();
	}

	@Override
	public @NotNull String getName() {
		return "Menu du choix des membres - Grades";
	}

	@Override
	public String getTexture() {
		return null;
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
