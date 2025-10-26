package fr.openmc.core.features.city.sub.rank.menus;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.models.DBCityRank;
import fr.openmc.core.features.city.sub.rank.CityRankAction;
import fr.openmc.core.utils.CacheOfflinePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CityRankAssignMenu extends Menu {

	private final UUID playerUUID;
	private final City city;
	
	public CityRankAssignMenu(Player owner, UUID playerUUID, City city) {
		super(owner);
		this.playerUUID = playerUUID;
		this.city = city;
	}
	
	@Override
	public @NotNull String getName() {
		return "Menu des Villes - Assigner un grade";
	}

	@Override
	public String getTexture() {
		return "§r§f:offset_-48::city_template3x9:";
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
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> map = new HashMap<>();
		
		Set<DBCityRank> availableRanks = city.getRanks();
		for (DBCityRank rank : availableRanks) {
			map.put(map.size(), new ItemBuilder(this, new ItemStack(rank.getIcon()), itemMeta -> {
				itemMeta.displayName(Component.text(rank.getName()));
				itemMeta.lore(List.of(
						Component.text("§7Permissions : " + (rank.getPermissionsSet().isEmpty() ? "§cAucune" : "§a" + rank.getPermissionsSet().size() + " permission(s)"))
				));
			}).setOnClick(event -> {
				if (!city.hasPermission(getOwner().getUniqueId(), CityPermission.ASSIGN_RANKS)) return;

				CityRankAction.assignRank(getOwner(), rank.getName(), CacheOfflinePlayer.getOfflinePlayer(playerUUID));
				getOwner().closeInventory();
			}));
		}
		
		return map;
	}
	
	@Override
	public List<Integer> getTakableSlot() {
		return List.of();
	}
}
