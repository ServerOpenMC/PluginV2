package fr.openmc.core.features.city.menu.list;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.features.city.sub.milestone.rewards.MemberLimitRewards;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.bukkit.SkullUtils;
import fr.openmc.core.utils.cache.PlayerNameCache;
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

import java.util.*;

public class CityListMenu extends PaginatedMenu {
	
	// Constants for the menu
	private static final Component SORT_HEADER = TranslationManager.translation("feature.city.menus.list.sort.header");
	private static final Component SELECTED_PREFIX = TranslationManager.translation("feature.city.menus.list.sort.selected_prefix");
	private static final Component UNSELECTED_PREFIX = TranslationManager.translation("feature.city.menus.list.sort.unselected_prefix");
	
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
		return Material.AIR;
	}
	
	@Override
	public @NotNull List<Integer> getStaticSlots() {
		return StaticSlots.getBottomSlots(getInventorySize());
	}
	
	@Override
    public List<ItemStack> getItems() {
		List<ItemStack> items = new ArrayList<>();
		for (City city : cities) {
			UUID ownerUUID = city.getPlayerWithPermission(CityPermission.OWNER);

			if (ownerUUID == null) continue;

			List<Component> cityLore = new ArrayList<>();

			Component ownerComponent = PlayerNameCache.name(ownerUUID).color(NamedTextColor.GRAY);
			Component levelComponent = Component.text(city.getLevel()).color(NamedTextColor.DARK_AQUA);
			Component membersCurrent = Component.text(city.getMembers().size()).color(NamedTextColor.GREEN);
			Component membersLimit = Component.text(MemberLimitRewards.getMemberLimit(city.getLevel())).color(NamedTextColor.GREEN);
			Component membersSuffix = Component.text(city.getMembers().size() > 1 ? "s" : "");
			Component typeComponent = city.getType().getDisplayName();
			Component wealthComponent = Component.text(EconomyManager.getFormattedSimplifiedNumber(city.getBalance())).color(NamedTextColor.GOLD);
			Component wealthIcon = Component.text(EconomyManager.getEconomyIcon()).color(NamedTextColor.GOLD);
			if (MayorManager.phaseMayor == 2 && FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.MAYOR)) {
				Component mayorCity = city.getMayor() == null
						? TranslationManager.translation("messages.menus.none")
						: city.getMayor().getName();
				NamedTextColor mayorColor = (city.getMayor() == null || city.getMayor().getMayorColor() == null) ? NamedTextColor.WHITE : city.getMayor().getMayorColor();
				Component mayorComponent = mayorCity.color(mayorColor).decoration(TextDecoration.ITALIC, false);
				cityLore.addAll(TranslationManager.translationLore(
						"feature.city.menus.list.item.lore.with_mayor",
						ownerComponent,
						mayorComponent,
						levelComponent,
						membersCurrent,
						membersLimit,
						membersSuffix,
						typeComponent,
						wealthComponent,
						wealthIcon
				));
			} else {
				cityLore.addAll(TranslationManager.translationLore(
						"feature.city.menus.list.item.lore",
						ownerComponent,
						levelComponent,
						membersCurrent,
						membersLimit,
						membersSuffix,
						typeComponent,
						wealthComponent,
						wealthIcon
				));
			}


			items.add(new ItemBuilder(this, SkullUtils.getPlayerSkull(ownerUUID), itemMeta -> {
				itemMeta.displayName(TranslationManager.translation(
						"feature.city.menus.list.item.title",
						Component.text(city.getName()).color(NamedTextColor.GREEN)
				));
				itemMeta.lore(cityLore);
			}).setOnClick(inventoryClickEvent ->
					new CityListDetailsMenu(getOwner(), city).open()
			).hide(ItemUtils.getDataComponentType()));
		}
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
			itemMeta.displayName(TranslationManager.translation("feature.city.menus.list.sort.title"));
			itemMeta.lore(generateSortLoreText());
		}).setOnClick(inventoryClickEvent -> {
			changeSortType();
			new CityListMenu(getOwner(), sortType).open();
		}));
		map.put(48, new ItemBuilder(this, CustomStack.getInstance("_iainternal:icon_back_orange")
				.getItemStack(), itemMeta -> itemMeta.displayName(TranslationManager.translation("messages.menus.previous_page"))).setPreviousPageButton());
		map.put(50, new ItemBuilder(this, CustomStack.getInstance("_iainternal:icon_next_orange")
				.getItemStack(), itemMeta -> itemMeta.displayName(TranslationManager.translation("messages.menus.next_page"))).setNextPageButton());
		return map;
	}
	
	@Override
	public @NotNull Component getName() {
		return TranslationManager.translation("feature.city.menus.list.name");
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
				formatSortOption(SortType.NAME, TranslationManager.translation("feature.city.menus.list.sort.name")),
				formatSortOption(SortType.WEALTH, TranslationManager.translation("feature.city.menus.list.sort.wealth")),
				formatSortOption(SortType.POPULATION, TranslationManager.translation("feature.city.menus.list.sort.population")),
				formatSortOption(SortType.PEACE_WAR, TranslationManager.translation("feature.city.menus.list.sort.peace_war"))
		);
	}
	
	/**
	 * Formats the sorting option string.
	 *
	 * @param type  The sorting type.
	 * @param label The label for the sorting option.
	 * @return A formatted string representing the sorting option.
	 */
	private Component formatSortOption(SortType type, Component label) {
		Component prefix = sortType == type ? SELECTED_PREFIX : UNSELECTED_PREFIX;
		return prefix.append(label);
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
