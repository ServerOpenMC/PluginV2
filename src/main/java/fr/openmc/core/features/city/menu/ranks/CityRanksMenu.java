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
import fr.openmc.core.items.CustomItemRegistry;
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
	public int getSizeOfItems() {
		return getItems().size();
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

		boolean canManagerRanks = city.hasPermission(player.getUniqueId(), CPermission.MANAGE_RANKS);

		Set<CityRank> cityRanks = city.getRanks();
		if (! cityRanks.isEmpty()) {
			for (CityRank rank : cityRanks) {
				String rankName = rank.getName();
				int priority = rank.getPriority();
				Material icon = rank.getIcon() != null ? rank.getIcon() : Material.PAPER;

				map.add(new ItemBuilder(this, icon,
						itemMeta -> {
							itemMeta.displayName(Component.text("§eGrade " + rankName).decoration(TextDecoration.ITALIC, false));
							itemMeta.lore(List.of(
									Component.text("§7Priorité : §d" + priority).decoration(TextDecoration.ITALIC, false),
									Component.text("§7Permissions : §b" + rank.getPermissionsSet().size()).decoration(TextDecoration.ITALIC, false),
									Component.empty(),
									Component.text(canManagerRanks ? "§e§lCLIQUEZ POUR MODIFIER LE ROLE" : "§e§lCLIQUEZ POUR S'Y INFORMER")
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

		boolean canAssignRanks = city.hasPermission(player.getUniqueId(), CPermission.ASSIGN_RANKS);

		if (canAssignRanks) {
			List<Component> loreAssignRanks = new ArrayList<>();
			if (city.getRanks().isEmpty()) {
				loreAssignRanks.add(Component.text("§cAucun grade n'a été créé dans cette ville."));
				loreAssignRanks.add(Component.text("§7Créez un grade pour pouvoir l'assigner aux membres."));
			} else {
				loreAssignRanks.add(Component.text("§fVous pouvez assigner des grades aux membres de la ville."));
				loreAssignRanks.add(Component.empty());
				loreAssignRanks.add(Component.text("§e§lCLIQUEZ POUR ASSIGNER UN GRADE"));
			}

			map.put(22, new ItemBuilder(this, Material.FEATHER,
					itemMeta -> {
						itemMeta.displayName(Component.text("§aAssigner des grades"));
						itemMeta.lore(loreAssignRanks);
					}).setOnClick(inventoryClickEvent -> {
						if (city.getRanks().isEmpty()) return;

						new CityRankMemberMenu(player, city).open();
					})
			);
		}

		boolean canManageRanks = city.hasPermission(player.getUniqueId(), CPermission.MANAGE_RANKS);

		if (canManageRanks) {
			List<Component> loreCreateRank = List.of(
					Component.text("§fVous pouvez faire un grade, §aun ensemble de permission !"),
					Component.empty(),
					Component.text("§e§lCLIQUEZ POUR CREER UN GRADE")
			);

			map.put(26, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:plus_btn").getBest(),
					itemMeta -> {
						itemMeta.displayName(Component.text("§aAjouter un grade"));
						itemMeta.lore(loreCreateRank);
					}).setOnClick(inventoryClickEvent -> CityRankAction.beginCreateRank(player))
			);
		}

		return map;
	}
	
	@Override
	public List<Integer> getTakableSlot() {
		return List.of();
	}
}
