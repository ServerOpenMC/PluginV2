package fr.openmc.core.features.city.menu.list;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.economy.EconomyManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityListMenu extends PaginatedMenu { // TODO : Adaptation pour les maires
	
	// Constants for the menu
	private static final Component SORT_HEADER = Component.text("§7Cliquez pour trier par");
	private static final String SELECTED_PREFIX = "§6➢ ";
	private static final String UNSELECTED_PREFIX = "§b  ";
	
	private final List<City> cities;
	private SortType sortType;
	
	/**
	 * Constructor for CityListMenu.
	 *
	 * @param owner  The player who opens the menu.
	 * @param cities The list of cities to display.
	 */
	public CityListMenu(Player owner, List<City> cities) {
		this(owner, cities, SortType.NAME);
	}
	
	/**
	 * Constructor for CityListMenu with a specified sort type.
	 *
	 * @param owner    The player who opens the menu.
	 * @param cities   The list of cities to display.
	 * @param sortType The initial sort type.
	 */
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
			itemMeta.displayName(Component.text("§a" + city.getCityName()));
			itemMeta.lore(List.of(
					Component.text("§7Maire : " + Bukkit.getServer().getOfflinePlayer(city.getPlayerWith(CPermission.OWNER)).getName()),
					Component.text("§bPopulation : " + city.getMembers().size()),
					Component.text("§eType : " + (CityManager.getCityType(city.getUUID()).equals("war") ? "§cGuerre" : "§aPaix")),
					Component.text("§6Richesses : " + EconomyManager.getFormattedSimplifiedNumber(city.getBalance()) + EconomyManager.getEconomyIcon())
			));
		})));
		return items;
	}
	
	@Override
	public Map<Integer, ItemStack> getButtons() {
		Map<Integer, ItemStack> map = new HashMap<>();
		map.put(49, new ItemBuilder(this, Material.HOPPER, itemMeta -> {
			itemMeta.displayName(Component.text("Trier"));
			itemMeta.lore(generateSortLoreText());
		}).setOnClick(inventoryClickEvent -> {
			changeSortType();
			new CityListMenu(getOwner(), cities, sortType).open();
		}));
		map.put(48, new ItemBuilder(this, CustomStack.getInstance("_iainternal:icon_back_orange")
				.getItemStack(), itemMeta -> itemMeta.displayName(Component.text("§cPage précédente"))).setPreviousPageButton());
		map.put(50, new ItemBuilder(this, CustomStack.getInstance("_iainternal:icon_next_orange")
				.getItemStack(), itemMeta -> itemMeta.displayName(Component.text("§aPage suivante"))).setNextPageButton());
		return map;
	}
	
	@Override
	public @NotNull String getName() {
		return "Liste des villes";
	}
	
	@Override
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getSlot() > 44) return;
		if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
		int page = getPage();
		City city = cities.get(e.getSlot() + (45 * page));
		if (city != null) {
			new CityListDetailsMenu(getOwner(), city).open();
		}
	}
	
	/**
	 * Generates the lore text for the sorting options.
	 *
	 * @return A list of strings representing the lore text.
	 */
	private List<Component> generateSortLoreText() {
		return List.of(
				SORT_HEADER,
				formatSortOption(SortType.NAME, "Nom"),
				formatSortOption(SortType.WEALTH, "Richesses"),
				formatSortOption(SortType.POPULATION, "Population"),
				formatSortOption(SortType.PEACE_WAR, "Paix/Guerre")
		);
	}
	
	/**
	 * Formats the sorting option string.
	 *
	 * @param type  The sorting type.
	 * @param label The label for the sorting option.
	 * @return A formatted string representing the sorting option.
	 */
	private Component formatSortOption(SortType type, String label) {
		return Component.text((sortType == type ? SELECTED_PREFIX : UNSELECTED_PREFIX) + label);
	}
	
	/**
	 * Sets the sorting type and sorts the cities accordingly.
	 *
	 * @param sortType The sorting type to set.
	 */
	private void setSortType(SortType sortType) {
		this.sortType = sortType;
		switch (this.sortType) {
			case NAME -> sortByName(cities);
			case WEALTH -> sortByWealth(cities);
			case POPULATION -> sortByPopulation(cities);
			case PEACE_WAR -> sortByPeaceWar(cities);
		}
	}
	
	/**
	 * Changes the sorting type to the next one in the enum and sorts the cities accordingly.
	 */
	private void changeSortType() {
		sortType = SortType.values()[(sortType.ordinal() + 1) % SortType.values().length];
		
		switch (sortType) {
			case WEALTH -> sortByWealth(cities);
			case POPULATION -> sortByPopulation(cities);
			case PEACE_WAR -> sortByPeaceWar(cities);
			default -> sortByName(cities);
		}
	}
	
	/**
	 * Sorts the cities by their names.
	 *
	 * @param cities The list of cities to sort.
	 */
	private void sortByName(List<City> cities) {
		cities.sort((o1, o2) -> o1.getCityName().compareToIgnoreCase(o2.getCityName()));
	}
	
	/**
	 * Sorts the cities by their wealth.
	 *
	 * @param cities The list of cities to sort.
	 */
	private void sortByWealth(List<City> cities) {
		cities.sort((o1, o2) -> Double.compare(o2.getBalance(), o1.getBalance()));
	}
	
	/**
	 * Sorts the cities by their population.
	 *
	 * @param cities The list of cities to sort.
	 */
	private void sortByPopulation(List<City> cities) {
		cities.sort((o1, o2) -> Integer.compare(o2.getMembers().size(), o1.getMembers().size()));
	}
	
	/**
	 * Sorts the cities by their type (peace or war).
	 *
	 * @param cities The list of cities to sort.
	 */
	private void sortByPeaceWar(List<City> cities) {
		cities.sort((o1, o2) -> {
			String type1 = CityManager.getCityType(o1.getUUID());
			String type2 = CityManager.getCityType(o2.getUUID());
			return type1.equals(type2) ? 0 : type1.equals("war") ? - 1 : 1;
		});
	}
	
	/**
	 * Enum representing the sorting types for the city list.
	 */
	private enum SortType {
		NAME,
		WEALTH,
		POPULATION,
		PEACE_WAR
	}
}
