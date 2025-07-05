package fr.openmc.core.features.city.menu.ranks;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.actions.CityRankAction;
import fr.openmc.core.features.city.menu.CityMenu;
import fr.openmc.core.features.city.models.CityRank;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
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

public class CityRanksMenu extends PaginatedMenu {
	
	private final City city;
	
	public CityRanksMenu(Player owner, City city) {
		super(owner);
		this.city = city;
	}
	
	@Override
	public @NotNull String getName() {
		return "Grades de la ville";
	}

	@Override
	public void onInventoryClick(InventoryClickEvent e) {
	}

	@Override
	public void onClose(InventoryCloseEvent event) {
	}

	@Override
	public @NotNull InventorySize getInventorySize() {
		return InventorySize.NORMAL;
	}

	@Override
	public @Nullable Material getBorderMaterial() {
		return null;
	}

	@Override
	public @NotNull List<Integer> getStaticSlots() {
		return StaticSlots.getBottomSlots(getInventorySize());
	}

	@Override
	public @NotNull List<ItemStack> getItems() {
		List<ItemStack> map = new ArrayList<>();
		Player player = getOwner();

		Set<CityRank> cityRanks = city.getRanks();
		if (! cityRanks.isEmpty()) {
			for (CityRank rank : cityRanks) {
				String rankName = rank.getName();
				int priority = rank.getPriority();
				Material icon = rank.getIcon() != null ? rank.getIcon() : Material.PAPER;

				map.add(new ItemBuilder(this, icon,
						itemMeta -> {
							itemMeta.displayName(Component.text(rankName));
							itemMeta.lore(List.of(
									Component.text("Priorité : " + priority).decoration(TextDecoration.ITALIC, false)
							));
						}
				).setOnClick(inventoryClickEvent -> new CityRankDetailsMenu(player, city, rank).open()));
			}
		}
		return map;
	}

	@Override
	public Map<Integer, ItemStack> getButtons() {
		Map<Integer, ItemStack> map = new HashMap<>();
		Player player = getOwner();

		map.put(18, new ItemBuilder(this, Material.ARROW,
				itemMeta -> {
					itemMeta.displayName(Component.text("§cRetour"));
					itemMeta.lore(List.of(Component.text("§7Cliquez pour revenir en arrière")));
				}).setOnClick(inventoryClickEvent -> new CityMenu(player).open()));

		map.put(22, new ItemBuilder(this, Material.FEATHER,
				itemMeta -> {
					itemMeta.displayName(Component.text("§aAssigner des grades"));
					itemMeta.lore(List.of(
							Component.text("§7Cliquez pour assigner les grades de la ville aux membres.")
					));
				}).setOnClick(inventoryClickEvent -> new CityRankMemberMenu(player, city).open()));

		List<Component> loreCreateRank = new ArrayList<>();

		if (city.hasPermission(player.getUniqueId(), CPermission.PERMS)) {
			loreCreateRank.add(Component.text("§fVous pouvez faire un grade, §aun ensemble de permission !"));
			loreCreateRank.add(Component.text(""));
			loreCreateRank.add(Component.text("§e§lCLIQUEZ POUR CREER UN GRADE"));
		}

		map.put(26, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:plus_btn").getBest(),
				itemMeta -> {
					itemMeta.displayName(Component.text("§aAjouter un grade"));
					itemMeta.lore(loreCreateRank);
				}).setOnClick(inventoryClickEvent -> CityRankAction.beginCreateRank(player)));

		return map;
	}
	
	@Override
	public List<Integer> getTakableSlot() {
		return List.of();
	}
}
