package fr.openmc.core.features.city.menu.list;

import dev.lone.itemsadder.api.CustomStack;
import dev.xernas.menulib.PaginatedMenu;
import dev.xernas.menulib.utils.ItemBuilder;
import dev.xernas.menulib.utils.ItemUtils;
import dev.xernas.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.mascots.MascotUtils;
import fr.openmc.core.features.economy.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CityListMenu extends PaginatedMenu {
	
	private static final String SORT_HEADER = "§7Cliquez pour trier par";
	private static final String SELECTED_PREFIX = "§6➢ ";
	private static final String UNSELECTED_PREFIX = "§b  ";
	
	private final List<City> cities;
	private SortType sortType;
	
	private enum SortType {
		NAME,
		WEALTH,
		POPULATION,
		MASCOT_LEVEL
	}
	
	public CityListMenu(Player owner, List<City> cities) {
		this(owner, cities, SortType.NAME);
	}

	public CityListMenu(Player owner, List<City> cities, SortType sortType) {
		super(owner);
		this.cities = cities;
		setSortType(sortType);
	}
	
	@Override
	public @Nullable Material getBorderMaterial() {
		return Material.GRAY_STAINED_GLASS_PANE;
	}
	
	@Override
	public @NotNull List<Integer> getStaticSlots() {
		return StaticSlots.BOTTOM;
	}
	
	@Override
	public @NotNull List<ItemStack> getItems() {
		List<ItemStack> items = new ArrayList<>();
		cities.forEach(city -> items.add(new ItemBuilder(this, ItemUtils.getPlayerSkull(city.getPlayerWith(CPermission.OWNER)), itemMeta -> {
			itemMeta.setDisplayName("§a" + city.getCityName());
			itemMeta.setLore(List.of(
					"§7Maire : " + Bukkit.getServer().getOfflinePlayer(city.getPlayerWith(CPermission.OWNER)).getName(),
					"§7Population : " + city.getMembers().size(),
					"§7Niveau de la mascotte : " + MascotUtils.getMascotOfCity(city.getUUID()).getLevel(),
					"§7Richesse : " + city.getBalance() + EconomyManager.getEconomyIcon()
			));
		}).setNextMenu(new CityListDetailsMenu(getOwner(), city))));
		return items;
	}
	
	@Override
	public Map<Integer, ItemStack> getButtons() {
		Map<Integer, ItemStack> map = new HashMap<>();
		map.put(49, new ItemBuilder(this, Material.HOPPER, itemMeta -> {
			itemMeta.setDisplayName("Trier");
			itemMeta.setLore(generateSortLoreText());
		}).setOnClick(inventoryClickEvent -> {
			changeSortType();
			new CityListMenu(getOwner(), cities, sortType).open();
		}));
		map.put(48, new ItemBuilder(this, CustomStack.getInstance("_iainternal:icon_back_orange")
				.getItemStack(), itemMeta -> itemMeta.setDisplayName("§cPage précédente")).setPreviousPageButton());
		map.put(50, new ItemBuilder(this, CustomStack.getInstance("_iainternal:icon_next_orange")
				.getItemStack(), itemMeta -> itemMeta.setDisplayName("§aPage suivante")).setNextPageButton());
		return map;
	}
	
	@Override
	public @NotNull String getName() {
		return "Liste des villes";
	}
	
	@Override
	public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
	
	}
	
	private List<String> generateSortLoreText() {
		return List.of(
				SORT_HEADER,
				formatSortOption(SortType.NAME, "Nom"),
				formatSortOption(SortType.WEALTH, "Richesse"),
				formatSortOption(SortType.POPULATION, "Population"),
				formatSortOption(SortType.MASCOT_LEVEL, "Niveau de la mascotte")
		);
	}
	
	private String formatSortOption(SortType type, String label) {
		return (sortType == type ? SELECTED_PREFIX : UNSELECTED_PREFIX) + label;
	}
	
	
	private void setSortType(SortType sortType) {
		this.sortType = sortType;
		switch (this.sortType) {
			case NAME -> sortByName(cities);
			case WEALTH -> sortByWealth(cities);
			case POPULATION -> sortByPopulation(cities);
			case MASCOT_LEVEL -> sortByMascotLevel(cities);
		}
	}
	
	private void changeSortType() {
		sortType = SortType.values()[(sortType.ordinal() + 1) % SortType.values().length];
		
		switch (sortType) {
			case WEALTH -> sortByWealth(cities);
			case POPULATION -> sortByPopulation(cities);
			case MASCOT_LEVEL -> sortByMascotLevel(cities);
			default -> sortByName(cities);
		}
	}
	
	private void sortByName(List<City> cities) {
		cities.sort((o1, o2) -> o1.getCityName().compareToIgnoreCase(o2.getCityName()));
	}
	
	private void sortByWealth(List<City> cities) {
		cities.sort((o1, o2) -> Double.compare(o2.getBalance(), o1.getBalance()));
	}
	
	private void sortByPopulation(List<City> cities) {
		cities.sort((o1, o2) -> Integer.compare(o2.getMembers().size(), o1.getMembers().size()));
	}
	
	private void sortByMascotLevel(List<City> cities) {
		cities.sort((o1, o2) -> Integer.compare(MascotUtils.getMascotOfCity(o2.getUUID()).getLevel(), MascotUtils.getMascotOfCity(o1.getUUID()).getLevel()));
	}
}
