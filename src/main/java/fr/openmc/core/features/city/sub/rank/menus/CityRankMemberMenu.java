package fr.openmc.core.features.city.sub.rank.menus;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.sub.rank.CityRankCondition;
import fr.openmc.core.utils.bukkit.SkullUtils;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
		return Material.AIR;
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
			if (player == null) {
				continue;
			}

			String rankName = city.getRankName(uuid);

			List<Component> lore = new ArrayList<>();
			lore.add(TranslationManager.translation(
					"feature.city.rank.menu.members.item.rank",
					Component.text(rankName).color(NamedTextColor.YELLOW)
			).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
			if (!city.hasPermission(player.getUniqueId(), CityPermission.OWNER)) {
				lore.add(Component.empty());
				lore.add(TranslationManager.translation("feature.city.rank.menu.members.item.click_assign")
						.color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));
			}
            items.add(new ItemBuilder(this, SkullUtils.getPlayerSkull(uuid), itemMeta -> {
				Component displayName = player.getName() != null
						? Component.text(player.getName())
						: TranslationManager.translation("feature.city.rank.menu.members.item.name_unknown")
								.color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, true);
				itemMeta.displayName(displayName.decoration(TextDecoration.ITALIC, false));
				itemMeta.lore(lore);
			}).setOnClick(event -> {
				if (city.hasPermission(player.getUniqueId(), CityPermission.OWNER)) {
					MessagesManager.sendMessage(getOwner(), TranslationManager.translation("feature.city.player_is_owner"), Prefix.CITY, MessageType.ERROR, false);
					return;
				}

				if (!city.hasPermission(getOwner().getUniqueId(), CityPermission.ASSIGN_RANKS)) {
					MessagesManager.sendMessage(getOwner(), TranslationManager.translation("messages.city.player_no_permission_access"), Prefix.CITY, MessageType.ERROR, false);
					getOwner().closeInventory();
					return;
				}
				
				if (city.getRankOfMember(player.getUniqueId()) != null) {
					if (!CityRankCondition.canModifyRankPermissions(city, getOwner(), city.getRankOfMember(player.getUniqueId()).getPriority())) {
						return;
					}
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
				.getItemStack(), itemMeta -> itemMeta.displayName(TranslationManager.translation("messages.menus.previous_page"))).setPreviousPageButton());
		map.put(50, new ItemBuilder(this, CustomStack.getInstance("_iainternal:icon_next_orange")
				.getItemStack(), itemMeta -> itemMeta.displayName(TranslationManager.translation("messages.menus.next_page"))).setNextPageButton());
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
	public @NotNull Component getName() {
		return TranslationManager.translation("feature.city.rank.menu.members.title");
	}

	@Override
	public String getTexture() {
		return "§r§f:offset_-48::city_template6x9:";
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
