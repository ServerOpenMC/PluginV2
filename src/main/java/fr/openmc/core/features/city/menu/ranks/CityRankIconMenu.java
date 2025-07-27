package fr.openmc.core.features.city.menu.ranks;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.api.input.DialogInput;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.models.CityRank;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CityRankIconMenu extends PaginatedMenu {
	
	private final CityRank rank;
	private final City city;
	private final int page;
	private static String filter = null;

	public CityRankIconMenu(Player owner, City city, int page, CityRank rank, String filter) {
		super(owner);
		this.rank = rank;
		this.city = city;
		this.page = page;
		CityRankIconMenu.filter = filter;
	}
	
	@Override
	public @Nullable Material getBorderMaterial() {
		return Material.WHITE_STAINED_GLASS_PANE;
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

	private static final List<Material> paginableMaterials = List.of();

	@Override
	public @NotNull List<ItemStack> getItems() {
		List<ItemStack> items = new ArrayList<>();
		List<Material> filtered = getFilteredMaterials();

		int startIndex = page * (getInventorySize().getSize() - getStaticSlots().size());
		int endIndex = Math.min(startIndex + (getInventorySize().getSize() - getStaticSlots().size()), filtered.size());

		for (int i = startIndex; i < endIndex; i++) {
			Material material = filtered.get(i);
			items.add(new ItemBuilder(this, material, itemMeta -> {
				itemMeta.displayName(ItemUtils.getItemTranslation(material).decoration(TextDecoration.ITALIC, false));
				itemMeta.lore(List.of(Component.text("§7Cliquez pour sélectionner cette icône")));
			}).setOnClick(inventoryClickEvent -> {
				new CityRankDetailsMenu(getOwner(), city, rank.withIcon(material)).open();
			}));
		}

		return items;
	}

	
	@Override
	public Map<Integer, ItemStack> getButtons() {
		Map<Integer, ItemStack> map = new HashMap<>();
		map.put(45, new ItemBuilder(this, Material.BARRIER
				, itemMeta -> itemMeta.displayName(Component.text("§cRetour"))).setOnClick(inventoryClickEvent -> {
			new CityRankDetailsMenu(getOwner(), city, rank).open();
		}));

		if (hasPreviousPage())
			map.put(48, new ItemBuilder(this, CustomStack.getInstance("_iainternal:icon_back_orange")
					.getItemStack(), itemMeta -> itemMeta.displayName(Component.text("§cPage précédente"))).setOnClick(inventoryClickEvent -> {
				new CityRankIconMenu(getOwner(), city, page - 1, rank, filter).open();
			}));
		if (hasNextPage())
			map.put(50, new ItemBuilder(this, CustomStack.getInstance("_iainternal:icon_next_orange")
					.getItemStack(), itemMeta -> itemMeta.displayName(Component.text("§aPage suivante"))).setOnClick(inventoryClickEvent -> {
				new CityRankIconMenu(getOwner(), city, page + 1, rank, filter).open();
			}));
		
		map.put(49, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_search").getBest(), itemMeta -> {
			itemMeta.displayName(Component.text("§bRechercher une icône"));
			itemMeta.lore(List.of(Component.text("§7Cliquez pour saisir un mot-clé")));
		}).setOnClick(event -> {
			DialogInput.send(getOwner(), Component.text("Entrez le nom d'un mot clé pour l'icône"), 100, input -> {
				new CityRankIconMenu(getOwner(), city, 0, rank, input).open();
			});
		}));

		if (filter != null && !filter.isEmpty()) {
			map.put(53, new ItemBuilder(this, Material.PAPER, itemMeta -> {
				itemMeta.displayName(Component.text("§cEffacer le filtre"));
				itemMeta.lore(List.of(Component.text("§e§lCLIQUEZ POURE EFFACER LE FILTRE")));
			}).setOnClick(event -> {
				new CityRankIconMenu(getOwner(), city, 0, rank, null).open();
			}));
		}
		return map;
	}
	
	@Override
	public @NotNull String getName() {
		return "Choisir une icône - Page " + (page + 1);
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

	private List<Material> getFilteredMaterials() {
		return Arrays.stream(Material.values())
				.filter(Material::isItem)
				.filter(material -> !excludedMaterials.contains(material))
				.filter(material -> !material.name().contains("SPAWN_EGG"))
				.filter(material -> !material.isLegacy())
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
