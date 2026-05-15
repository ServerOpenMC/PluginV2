package fr.openmc.core.features.city.sub.rank.menus;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.models.DBCityRank;
import fr.openmc.core.features.city.sub.rank.CityRankCommands;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityRankPermsMenu extends PaginatedMenu {
	
	private final DBCityRank newRank;
	private final DBCityRank oldRank;
	private final boolean canEdit;
	private final City city;
	private final int page;
	
	public CityRankPermsMenu(Player owner, DBCityRank oldRank, DBCityRank newRank, boolean canEdit, int page) {
		super(owner);
		this.oldRank = oldRank;
		this.newRank = newRank;
		this.canEdit = canEdit;
		this.city = CityManager.getPlayerCity(owner.getUniqueId());
		this.page = page;
	}
	
	@Override
	public @Nullable Material getBorderMaterial() {
		return Material.AIR;
	}
	
	@Override
	public @NotNull List<Integer> getStaticSlots() {
		return StaticSlots.getStaticSlots(getInventorySize(), StaticSlots.Type.BOTTOM);
	}
	
	@Override
	public List<ItemStack> getItems() {
		List<ItemStack> items = new ArrayList<>();
		
		for (CityPermission permission : CityPermission.values()) {
			if (permission == CityPermission.OWNER) continue;
			
			boolean hasPerm = this.newRank.getPermissionsSet().contains(permission);
			ItemBuilder itemBuilder = new ItemBuilder(this, permission.getIcon(), itemMeta -> {
				itemMeta.setEnchantmentGlintOverride(hasPerm);
				itemMeta.displayName(TranslationManager.translation(
						hasPerm ? "feature.city.rank.menu.perms.item.remove" : "feature.city.rank.menu.perms.item.add",
						permission.getDisplayName()
				).color(hasPerm ? NamedTextColor.RED : NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));

				List<Component> lore = List.of(
						TranslationManager.translation(
							hasPerm ? "feature.city.rank.menu.perms.item.lore.remove" : "feature.city.rank.menu.perms.item.lore.add"
						).color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)
				);
				itemMeta.lore(lore);
			}).setOnClick(inventoryClickEvent -> {
				if (!canEdit)
					MessagesManager.sendMessage(getOwner(), TranslationManager.translation("feature.city.grade.cannot_modify_sup_role"), Prefix.CITY, MessageType.ERROR, true);
				else {
					CityRankCommands.swapPermission(getOwner(), newRank, permission);
					new CityRankPermsMenu(getOwner(), oldRank, newRank, true, page).open();
				}
			}).hide(ItemUtils.getDataComponentType());
			
			items.add(itemBuilder);
		}
		
		return items;
	}
	
	@Override
	public Map<Integer, ItemBuilder> getButtons() {
		Map<Integer, ItemBuilder> map = new HashMap<>();
		
		map.put(45, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_cancel").getBest(), itemMeta -> {
			itemMeta.displayName(TranslationManager.translation("messages.menus.back"));
			itemMeta.lore(List.of(TranslationManager.translation("messages.menus.back_lore")));
		}).setOnClick(inventoryClickEvent -> new CityRankDetailsMenu(getOwner(), city, oldRank, newRank).open()));
		
		if (hasPreviousPage()) {
			map.put(48, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_back_orange").getBest(), itemMeta -> {
				itemMeta.displayName(TranslationManager.translation("messages.menus.previous_page"));
				itemMeta.lore(List.of(TranslationManager.translation("messages.menus.previous_page_lore")));
			}).setOnClick(inventoryClickEvent -> new CityRankPermsMenu(getOwner(), oldRank, newRank, canEdit, page - 1).open()));
		}
		if (hasNextPage()) {
			map.put(50, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_next_orange").getBest(), itemMeta -> {
				itemMeta.displayName(TranslationManager.translation("messages.menus.next_page"));
				itemMeta.lore(List.of(TranslationManager.translation("messages.menus.next_page_lore")));
			}).setOnClick(inventoryClickEvent -> new CityRankPermsMenu(getOwner(), oldRank, newRank, canEdit, page + 1).open()));
		}
		
		if (canEdit) {
			map.put(53, new ItemBuilder(this, Material.GOLD_BLOCK, itemMeta -> {
				itemMeta.displayName(TranslationManager.translation("feature.city.rank.menu.perms.manage_all.title"));
				itemMeta.lore(TranslationManager.translationLore("feature.city.rank.menu.perms.manage_all.lore"));
			}).setOnClick(inventoryClickEvent -> {
				if (inventoryClickEvent.isLeftClick()) CityRankCommands.removeAllPermissions(getOwner(), newRank);
				else if (inventoryClickEvent.isRightClick()) CityRankCommands.addAllPermissions(getOwner(), newRank);
				
				new CityRankPermsMenu(getOwner(), oldRank, newRank, true, page).open();
			}));
		}
		
		return map;
	}
	
	@Override
	public @NotNull Component getName() {
		return TranslationManager.translation(
				"feature.city.rank.menu.perms.title",
				Component.text(this.newRank.getName()).color(NamedTextColor.YELLOW)
		);
	}
	
	@Override
	public String getTexture() {
		return "§r§f:offset_-48::city_template6x9:";
	}
	
	@Override
	public @NotNull InventorySize getInventorySize() {
		return InventorySize.LARGEST;
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
	
	@Override
	public int getSizeOfItems() {
		return 0;
	}
	
	public boolean hasNextPage() {
		return this.page < getNumberOfPages();
	}
	
	public boolean hasPreviousPage() {
		return this.page > 0;
	}
}
