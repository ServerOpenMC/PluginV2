package fr.openmc.core.features.city.menu.list;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.PlayerNameCache;
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

import java.util.*;

public class CityListMenu extends PaginatedMenu {
	
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
	 */
	public CityListMenu(Player owner) {
		this(owner, SortType.NAME);
	}
	
	/**
	 * Constructor for CityListMenu with a specified sort type.
	 *
	 * @param owner    The player who opens the menu.
	 * @param sortType The initial sort type.
	 */
	public CityListMenu(Player owner, SortType sortType) {
		super(owner);
		this.cities = new ArrayList<>(CityManager.getCities());
		setSortType(sortType);
	}
	
	@Override
	public @Nullable Material getBorderMaterial() {
		return Material.GRAY_STAINED_GLASS_PANE;
	}
	
	@Override
	public @NotNull List<Integer> getStaticSlots() {
		return StaticSlots.getBottomSlots(getInventorySize());
	}
	
	@Override
    public List<ItemStack> getItems() {
		List<ItemStack> items = new ArrayList<>();
		cities.forEach(city -> {
			UUID ownerUUID = city.getPlayerWithPermission(CPermission.OWNER);
			String ownerName = PlayerNameCache.getName(ownerUUID);

			List<Component> cityLore = new ArrayList<>();

			cityLore.add(Component.text("§7Propriétaire : " + ownerName));
			if (MayorManager.phaseMayor == 2) {
				String mayorCity = city.getMayor() == null ? "§7Aucun" : city.getMayor().getName();
				NamedTextColor mayorColor = (city.getMayor() == null || city.getMayor().getMayorColor() == null) ? NamedTextColor.WHITE : city.getMayor().getMayorColor();
				cityLore.add(Component.text("§7Maire : ").append(Component.text(mayorCity).color(mayorColor).decoration(TextDecoration.ITALIC, false)));
			}
			cityLore.add(Component.text("§eType : " + (city.getType().equals(CityType.WAR) ? "§cGuerre" : "§aPaix")));
			cityLore.add(Component.text("§6Richesses : " + EconomyManager.getFormattedSimplifiedNumber(city.getBalance()) + EconomyManager.getEconomyIcon()));


			items.add(new ItemBuilder(this, ItemUtils.getPlayerSkull(ownerUUID), itemMeta -> {
				itemMeta.displayName(Component.text("§a" + city.getName()));
				itemMeta.lore(cityLore);
			}));
		});
		return items;
	}

	@Override
	public List<Integer> getTakableSlot() {
		return List.of();
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
    public Map<Integer, ItemBuilder> getButtons() {
        Map<Integer, ItemBuilder> map = new HashMap<>();
		map.put(49, new ItemBuilder(this, Material.HOPPER, itemMeta -> {
			itemMeta.displayName(Component.text("Trier"));
			itemMeta.lore(generateSortLoreText());
		}).setOnClick(inventoryClickEvent -> {
			changeSortType();
			new CityListMenu(getOwner(), sortType).open();
		}));
		map.put(48, new ItemBuilder(this, CustomStack.getInstance("_iainternal:icon_back_orange")
				.getItemStack(), itemMeta -> itemMeta.displayName(Component.text("§cPage précédente"))).setPreviousPageButton());
		map.put(50, new ItemBuilder(this, CustomStack.getInstance("_iainternal:icon_next_orange")
				.getItemStack(), itemMeta -> itemMeta.displayName(Component.text("§aPage suivante"))).setNextPageButton());
		return map;
	}
	
	@Override
	public @NotNull String getName() {
		return "Menu des listes des Villes";
	}

	@Override
	public String getTexture() {
		return null;
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

	@Override
	public void onClose(InventoryCloseEvent event) {
		//empty
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
		if (cities.size() <= 1) return;
		cities.sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
	}
	
	/**
	 * Sorts the cities by their wealth.
	 *
	 * @param cities The list of cities to sort.
	 */
	private void sortByWealth(List<City> cities) {
		if (cities.size() <= 1) return;
		cities.sort((o1, o2) -> Double.compare(o2.getBalance(), o1.getBalance()));
	}
	
	/**
	 * Sorts the cities by their population.
	 *
	 * @param cities The list of cities to sort.
	 */
	private void sortByPopulation(List<City> cities) {
		if (cities.size() <= 1) return;
		cities.sort((o1, o2) -> Integer.compare(o2.getMembers().size(), o1.getMembers().size()));
	}
	
	/**
	 * Sorts the cities by their type (peace or war).
	 *
	 * @param cities The list of cities to sort.
	 */
	private void sortByPeaceWar(List<City> cities) {
		if (cities.size() <= 1) return;
		cities.sort((o1, o2) -> {
			CityType type1 = o1.getType();
			CityType type2 = o2.getType();
			return type1.equals(type2) ? 0 : type1.equals(CityType.WAR) ? -1 : 1;
		});
	}
	
	/**
	 * Enum representing the sorting types for the city list.
	 */
	public enum SortType {
		NAME,
		WEALTH,
		POPULATION,
		PEACE_WAR
	}
}
