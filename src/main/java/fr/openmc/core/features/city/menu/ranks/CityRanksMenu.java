package fr.openmc.core.features.city.menu.ranks;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityRank;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CityRanksMenu extends Menu {
	
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
	public @NotNull InventorySize getInventorySize() {
		return InventorySize.NORMAL;
	}
	
	@Override
	public void onInventoryClick(InventoryClickEvent e) {
	
	}
	
	@Override
	public void onClose(InventoryCloseEvent event) {
	
	}
	
	@Override
	public @NotNull Map<Integer, ItemStack> getContent() {
		Map<Integer, ItemStack> map = new HashMap<>();
		
		int i = 0;
		List<CityRank> cityRanks = city.getRanks();
		if (! cityRanks.isEmpty()) {
			for (CityRank rank : cityRanks) {
				if (i == 18) break; // Limit to 18 ranks displayed
				
				String rankName = rank.getName();
				Set<CPermission> permissions = rank.getPermissions();
				int priority = rank.getPriority();
				Material icon = rank.getIcon() != null ? rank.getIcon() : Material.PAPER;
				
				map.put(i, new ItemBuilder(this, icon,
						itemMeta -> {
							itemMeta.displayName(Component.text(rankName));
							itemMeta.lore(List.of(
									Component.text("Priorité : " + priority).decoration(TextDecoration.ITALIC, false)
							));
						}
				).setOnClick(inventoryClickEvent -> new CityRankDetailsMenu(getOwner(), city, rank).open()));
				i++;
			}
		}
		
		map.put(26, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:plus_btn").getBest(),
				itemMeta -> {
					itemMeta.displayName(Component.text("§aAjouter un grade"));
					itemMeta.lore(List.of(Component.text("§7Pour ajouter un nouveau grade, faites /city rank add <nom>")));
				}));
		
		return map;
	}
	
	@Override
	public List<Integer> getTakableSlot() {
		return List.of();
	}
}
