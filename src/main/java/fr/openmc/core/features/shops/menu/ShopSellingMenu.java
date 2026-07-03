package fr.openmc.core.features.shops.menu;

import fr.openmc.api.input.dialog.DialogInput;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.template.ConfirmMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.shops.manager.PlayerShopManager;
import fr.openmc.core.features.shops.models.Shop;
import fr.openmc.core.features.shops.models.ShopItem;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ShopSellingMenu extends PaginatedMenu {
	
	private final Shop shop;
	private final Inventory barrelInventory;
	private double price;
	
	public ShopSellingMenu(Player owner, Shop shop) {
		super(owner);
		this.shop = shop;
		this.barrelInventory = ((Barrel) shop.getMultiblock().stockBlockLoc().getBlock().getState()).getSnapshotInventory();
	}
	
	@Override
	public @NotNull Component getName() {
		return TranslationManager.translation("feature.shop.menu.selling.title");
	}
	
	@Override
	public String getTexture() {
		return "§r§f:offset_-11::large_shop_menu:";
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
	public void onInventoryClick(InventoryClickEvent e) {
	
	}
	
	@Override
	public void onClose(InventoryCloseEvent event) {
		this.shop.setMenuOpened(false);
	}
	
	@Override
	public @Nullable Material getBorderMaterial() {
		return null;
	}
	
	@Override
	public @NotNull List<Integer> getStaticSlots() {
		return StaticSlots.getStandardSlots(getInventorySize());
	}
	
	@Override
	public @NotNull List<ItemStack> getItems() {
		List<ItemStack> list = new ArrayList<>();
		
		List<ItemStack> items = getUniqueItemStacks(barrelInventory.getContents());
		for (ItemStack item : items) {
			list.add(new ItemMenuBuilder(this, item, itemMeta -> {
				if (itemMeta.hasLore()) itemMeta.lore().add(TranslationManager.translation("feature.shop.menu.selling.item_lore"));
				else itemMeta.lore(List.of(TranslationManager.translation("feature.shop.menu.selling.item_lore")));
			}).setOnClick(_ -> DialogInput.send(getOwner(),
					TranslationManager.translation("feature.shop.menu.selling.price_input"),
					Integer.MAX_VALUE,
					s -> {
						double pricePerItem = Double.parseDouble(s);
						if (Double.isNaN(pricePerItem)) return;
						if (pricePerItem <= 0) return;
						shop.setItem(new ShopItem(shop.getShopUUID(), item, pricePerItem));
						new ShopMenu(getOwner(), shop).open();
						MessagesManager.sendMessage(getOwner(), TranslationManager.translation("feature.shop.menu.selling.added_item"), Prefix.SHOP, MessageType.SUCCESS, true);
					})));
		}
		return list;
	}
	
	@Override
	public Map<Integer, ItemMenuBuilder> getButtons() {
		Map<Integer, ItemMenuBuilder> map = new HashMap<>();
		
		map.put(8, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.HOMES_ICON_INFO.getBest(), itemMeta -> {
			itemMeta.displayName(TranslationManager.translation("feature.shop.menu.selling.info.title"));
			itemMeta.lore(List.of(
					TranslationManager.translation("feature.shop.menu.selling.info.lore1"),
					TranslationManager.translation("feature.shop.menu.selling.info.lore2")
			));
		}));
		
		map.put(49, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.HOMES_ICON_BIN_RED.getBest(), itemMeta -> {
			itemMeta.displayName(TranslationManager.translation("feature.shop.menu.main.delete.btn.title"));
			itemMeta.lore(List.of(
					TranslationManager.translation("feature.shop.menu.main.delete.btn.lore2")
			));
		}).setOnClick(_ -> new ConfirmMenu(
				getOwner(),
				() -> {
					getOwner().closeInventory();
					PlayerShopManager.deleteShop(getOwner(), shop);
				},
				() -> new ShopSellingMenu(getOwner(), shop).open(),
				List.of(TranslationManager.translation("feature.shop.menu.main.delete.confirm.accept")),
				List.of(TranslationManager.translation("feature.shop.menu.main.delete.confirm.refuse"))
		).open()));
		
		return map;
	}
	
	@Override
	public List<Integer> getTakableSlot() {
		return List.of();
	}
	
	/**
	 * Filters the provided array of {@code ItemStack} objects to return a list of unique item stacks,
	 * with each stack reduced to a single item version.
	 *
	 * @param items an array of {@code ItemStack} objects to process.
	 * @return a {@code List} of unique {@code ItemStack} objects, each representing a single item stack.
	 */
	private List<ItemStack> getUniqueItemStacks(ItemStack[] items) {
		Set<ItemStack> itemStacks = new HashSet<>();
		for (ItemStack item : items) {
			if (item == null) continue;
			itemStacks.add(item.asOne());
		}
		return itemStacks.stream().toList();
	}
}
