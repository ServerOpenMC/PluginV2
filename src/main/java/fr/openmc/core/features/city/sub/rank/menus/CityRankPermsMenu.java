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
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
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
				itemMeta.displayName(Component.text((hasPerm ? "§cRetirer " : "§aAjouter ") + permission.getDisplayName()));
				
				List<Component> lore = List.of(
						Component.text("§e§lCLIQUEZ POUR " + (hasPerm ? "RETIRER" : "AJOUTER") + " CETTE PERMISSION")
				);
				itemMeta.lore(lore);
			}).setOnClick(inventoryClickEvent -> {
				if (!canEdit)
					MessagesManager.sendMessage(getOwner(), Component.text("§cVous n'avez pas la permission de modifier ce grade"), Prefix.CITY, MessageType.ERROR, true);
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
			itemMeta.displayName(Component.text("§aRetour"));
			itemMeta.lore(List.of(Component.text("§7Cliquez pour retourner au menu précédent")));
		}).setOnClick(inventoryClickEvent -> new CityRankDetailsMenu(getOwner(), city, oldRank, newRank).open()));
		
		if (hasPreviousPage()) {
			map.put(48, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_back_orange").getBest(), itemMeta -> {
				itemMeta.displayName(Component.text("§aPage précédente"));
				itemMeta.lore(List.of(Component.text("§7Cliquez pour aller à la page précédente")));
			}).setOnClick(inventoryClickEvent -> new CityRankPermsMenu(getOwner(), oldRank, newRank, canEdit, page - 1).open()));
		}
		if (hasNextPage()) {
			map.put(50, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_next_orange").getBest(), itemMeta -> {
				itemMeta.displayName(Component.text("§aPage suivante"));
				itemMeta.lore(List.of(Component.text("§7Cliquez pour aller à la page suivante")));
			}).setOnClick(inventoryClickEvent -> new CityRankPermsMenu(getOwner(), oldRank, newRank, canEdit, page + 1).open()));
		}
		
		if (canEdit) {
			map.put(53, new ItemBuilder(this, Material.GOLD_BLOCK, itemMeta -> {
				itemMeta.displayName(Component.text("§6Gérer toutes les permissions du grade"));
				itemMeta.lore(List.of(
						Component.text("§cClique-gauche pour tout retirer"),
						Component.text("§aClique-droit pour tout ajouter")
				));
			}).setOnClick(inventoryClickEvent -> {
				if (inventoryClickEvent.isLeftClick()) CityRankCommands.removeAllPermissions(getOwner(), newRank);
				else if (inventoryClickEvent.isRightClick()) CityRankCommands.addAllPermissions(getOwner(), newRank);
				
				new CityRankPermsMenu(getOwner(), oldRank, newRank, true, page).open();
			}));
		}
		
		return map;
	}
	
	@Override
	public @NotNull String getName() {
		return "Permissions du grade " + this.newRank.getName();
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
