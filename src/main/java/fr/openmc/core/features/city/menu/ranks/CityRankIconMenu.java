package fr.openmc.core.features.city.menu.ranks;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.models.CityRank;
import fr.openmc.core.utils.ItemUtils;
import net.kyori.adventure.text.Component;
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
	
	public CityRankIconMenu(Player owner, City city, CityRank rank) {
		super(owner);
		this.rank = rank;
		this.city = city;
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
		return paginableMaterials.size() - 1;
	}

	@Override
	public @NotNull List<Integer> getStaticSlots() {
		return StaticSlots.getBottomSlots(getInventorySize());
	}

	private static final Set<Material> excludedMaterials = Set.of(
			Material.AIR, Material.BARRIER, Material.COMMAND_BLOCK, Material.CHAIN_COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK, Material.STRUCTURE_BLOCK, Material.STRUCTURE_VOID
	);

	private static final List<Material> paginableMaterials;

	static {
		paginableMaterials = Arrays.stream(Material.values())
				.filter(material -> !excludedMaterials.contains(material))
				.filter(material -> !material.name().contains("SPAWN_EGG"))
				.filter(material -> !material.isLegacy())
				.toList();
	}

	@Override
	public @NotNull List<ItemStack> getItems() {
		List<ItemStack> items = new ArrayList<>();

		System.out.println(getPage() * getInventorySize().getSize() - getStaticSlots().size());

		int startIndex = getPage() * (getInventorySize().getSize() - getStaticSlots().size());
		int endIndex = Math.min(startIndex + (getInventorySize().getSize() - getStaticSlots().size()), paginableMaterials.size());

		for (int i = startIndex; i < endIndex; i++) {
			Material material = paginableMaterials.get(i);
			items.add(new ItemBuilder(this, new ItemStack(material), itemMeta -> {
				itemMeta.displayName(ItemUtils.getItemTranslation(material));
				itemMeta.lore(List.of(Component.text("§7Cliquez pour sélectionner cette icône")));
			}));
		}

		return items;
	}
	
	@Override
	public Map<Integer, ItemStack> getButtons() {
		Map<Integer, ItemStack> map = new HashMap<>();
		map.put(48, new ItemBuilder(this, CustomStack.getInstance("_iainternal:icon_back_orange")
				.getItemStack(), itemMeta -> itemMeta.displayName(Component.text("§cPage précédente"))).setPreviousPageButton());
		map.put(50, new ItemBuilder(this, CustomStack.getInstance("_iainternal:icon_next_orange")
				.getItemStack(), itemMeta -> itemMeta.displayName(Component.text("§aPage suivante"))).setNextPageButton());
		return map;
	}
	
	@Override
	public @NotNull String getName() {
		return "Choisir une icône";
	}
	
	@Override
	public void onInventoryClick(InventoryClickEvent e) {
		ItemStack clickedItem = e.getCurrentItem();
		if (clickedItem == null || clickedItem.getType() == Material.AIR || clickedItem.getType() == getBorderMaterial()) {
			return;
		}
		new CityRankDetailsMenu(getOwner(), city, rank.withIcon(clickedItem.getType())).open();
	}
	
	@Override
	public void onClose(InventoryCloseEvent event) {
	
	}
	
	@Override
	public List<Integer> getTakableSlot() {
		return List.of();
	}
}
