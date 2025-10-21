package fr.openmc.core.features.city.sub.rank.menus;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.api.input.DialogInput;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.models.DBCityRank;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static fr.openmc.api.menulib.utils.ItemUtils.getDataComponentType;
import static fr.openmc.core.utils.InputUtils.MAX_LENGTH;

public class CityRankIconMenu extends PaginatedMenu {
	
	private final DBCityRank newRank;
	private final DBCityRank oldRank;
	private final City city;
	private final int page;
	private static String filter = null;
	
	public CityRankIconMenu(Player owner, City city, int page, DBCityRank oldRank, DBCityRank newRank, String filter) {
		super(owner);
		this.newRank = newRank;
		this.oldRank = oldRank;
		this.city = city;
		this.page = page;
		CityRankIconMenu.filter = filter;
	}
	
	@Override
	public @Nullable Material getBorderMaterial() {
		return Material.AIR;
	}
	
	@Override
	public @NotNull InventorySize getInventorySize() {
		return InventorySize.LARGEST;
	}
	
	@Override
	public int getSizeOfItems() {
		return getFilteredMaterials().size();
	}
	
	@Override
	public @NotNull List<Integer> getStaticSlots() {
		return StaticSlots.getBottomSlots(getInventorySize());
	}
	
	private static final Set<Material> excludedMaterials = Set.of(
			Material.AIR,
			Material.BARRIER,
			Material.COMMAND_BLOCK,
			Material.CHAIN_COMMAND_BLOCK,
			Material.REPEATING_COMMAND_BLOCK,
			Material.STRUCTURE_BLOCK,
			Material.STRUCTURE_VOID,
			Material.DEBUG_STICK
	);
	
	@Override
	public List<ItemStack> getItems() {
		List<ItemStack> items = new ArrayList<>();
		List<Material> filtered = getFilteredMaterials();
		
		int startIndex = page * (getInventorySize().getSize() - getStaticSlots().size());
		int endIndex = Math.min(startIndex + (getInventorySize().getSize() - getStaticSlots().size()), filtered.size());
		
		for (int i = startIndex; i < endIndex; i++) {
			Material material = filtered.get(i);
			items.add(new ItemBuilder(this, material, itemMeta -> {
				if (itemMeta == null) return;
				itemMeta.displayName(ItemUtils.getItemTranslation(material).decoration(TextDecoration.ITALIC, false));
				itemMeta.lore(List.of(Component.text("§7Cliquez pour sélectionner cette icône")));
			}).hide(getDataComponentType())
					.setOnClick(inventoryClickEvent -> new CityRankDetailsMenu(getOwner(), city, oldRank, newRank.withIcon(material)).open()));
		}
		
		return items;
	}
	
	
	@Override
	public Map<Integer, ItemBuilder> getButtons() {
		Map<Integer, ItemBuilder> map = new HashMap<>();
		map.put(45, new ItemBuilder(this, Material.BARRIER
				, itemMeta -> itemMeta.displayName(Component.text("§cRetour")), true));
		
		if (hasPreviousPage())
			map.put(48, new ItemBuilder(this, CustomStack.getInstance("_iainternal:icon_back_orange")
					.getItemStack(), itemMeta -> itemMeta.displayName(Component.text("§cPage précédente"))).setOnClick(inventoryClickEvent -> {
				new CityRankIconMenu(getOwner(), city, page - 1, oldRank, newRank, filter).open();
			}));
		if (hasNextPage())
			map.put(50, new ItemBuilder(this, CustomStack.getInstance("_iainternal:icon_next_orange")
					.getItemStack(), itemMeta -> itemMeta.displayName(Component.text("§aPage suivante"))).setOnClick(inventoryClickEvent -> {
				new CityRankIconMenu(getOwner(), city, page + 1, oldRank, newRank, filter).open();
			}));
		
		map.put(49, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_search").getBest(), itemMeta -> {
			itemMeta.displayName(Component.text("§bRechercher une icône"));
			itemMeta.lore(List.of(Component.text("§7Cliquez pour saisir un mot-clé")));
		}).setOnClick(event -> {
			DialogInput.send(getOwner(), Component.text("Entrez le nom d'un mot clé pour l'icône"), MAX_LENGTH, input -> {
				if (input == null) return;
				new CityRankIconMenu(getOwner(), city, 0, oldRank, newRank, input).open();
			});
		}));
		
		if (filter != null && !filter.isEmpty()) {
			map.put(53, new ItemBuilder(this, Material.PAPER, itemMeta -> {
				itemMeta.displayName(Component.text("§cEffacer le filtre"));
				itemMeta.lore(List.of(Component.text("§e§lCLIQUEZ POUR EFFACER LE FILTRE")));
			}).setOnClick(event -> {
				new CityRankIconMenu(getOwner(), city, 0, oldRank, newRank, null).open();
			}));
		}
		return map;
	}
	
	@Override
	public @NotNull String getName() {
		return "Menu de choix d'une icône - Page " + (page + 1);
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
	
	/**
	 * Get all non-legacy, non-excluded, non-spawn egg materials, filtered by the current filter if set.
	 *
	 * @return List of filtered materials.
	 */
	private List<Material> getFilteredMaterials() {
		World world = getOwner().getWorld();
		return Arrays.stream(Material.values())
				.filter(material -> !material.isLegacy())
				.filter(Material::isItem)
				.filter(material -> !excludedMaterials.contains(material))
				.filter(material -> !material.name().contains("SPAWN_EGG"))
				.filter(material -> filter == null || material.name().toLowerCase().startsWith(filter.toLowerCase()))
				.toList();
	}
	
	public boolean hasNextPage() {
		return this.page < getNumberOfPages();
	}
	
	public boolean hasPreviousPage() {
		return this.page > 0;
	}
}
