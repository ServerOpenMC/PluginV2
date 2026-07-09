package fr.openmc.core.features.shops.menus;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.template.ItemMenuTemplate;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.shops.models.Shop;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ShopStatsMenu extends Menu {
	
	private final Shop shop;
	private int totalSoldItems = 0;
	private final Set<UUID> totalUniquePlayers = new HashSet<>();
	
	protected ShopStatsMenu(Player owner, Shop shop) {
		super(owner);
		this.shop = shop;
		if (shop.getItem() == null) owner.closeInventory();
		if (!shop.getSales().isEmpty()) {
			shop.getSales().forEach(s -> {
				totalSoldItems += s.getAmount();
				totalUniquePlayers.add(s.getBuyerUUID());
			});
		}
	}
	
	@Override
	public @NotNull Component getName() {
		return TranslationManager.translation("feature.shop.menu.stats.title");
	}
	
	@Override
	public String getTexture() {
		return "";
	}
	
	@Override
	public @NotNull InventorySize getInventorySize() {
		return InventorySize.NORMAL;
	}
	
	@Override
	public void onInventoryClick(InventoryClickEvent e) {
	
	}
	
	@Override
	public void onClose(InventoryCloseEvent event) {
		this.shop.setMenuOpened(false);
	}
	
	@Override
	public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
		Map<Integer, ItemMenuBuilder> map = new HashMap<>();
		
		map.put(11, new ItemMenuBuilder(this, Material.PAPER, itemMeta ->
				itemMeta.displayName(TranslationManager.translation("feature.shop.menu.stats.sells.title", Component.text(this.shop.getSales().size()).color(NamedTextColor.GREEN)))));
		map.put(12, new ItemMenuBuilder(this, Material.GOLD_INGOT, itemMeta ->
				itemMeta.displayName(TranslationManager.translation("feature.shop.menu.stats.sales.title", Component.text(this.totalSoldItems).color(NamedTextColor.DARK_GREEN)))));
		map.put(13, new ItemMenuBuilder(this, Material.GOLD_BLOCK, itemMeta ->
				itemMeta.displayName(TranslationManager.translation("feature.shop.menu.stats.turnover.title", Component.text(this.shop.getTurnover() + " " + EconomyManager.getEconomyIcon()).color(NamedTextColor.GOLD)))));
		map.put(14, new ItemMenuBuilder(this, Material.PLAYER_HEAD, itemMeta ->
				itemMeta.displayName(TranslationManager.translation("feature.shop.menu.stats.buyers.title", Component.text(this.totalUniquePlayers.size()).color(NamedTextColor.LIGHT_PURPLE)))));
		map.put(15, new ItemMenuBuilder(this, Material.BARREL, itemMeta ->
				itemMeta.displayName(TranslationManager.translation("feature.shop.menu.stats.stocks.title", Component.text(this.shop.getItem().getAmount()).color(NamedTextColor.AQUA)))));
		
		map.put(18, ItemMenuTemplate.BTN_CANCEL.apply(this).setBackButton());
		
		return map;
	}
	
	@Override
	public List<Integer> getTakableSlot() {
		return List.of();
	}
}
