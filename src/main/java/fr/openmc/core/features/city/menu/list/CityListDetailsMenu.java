package fr.openmc.core.features.city.menu.list;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import dev.xernas.menulib.utils.ItemUtils;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.economy.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static fr.openmc.core.features.city.mascots.MascotUtils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityListDetailsMenu extends Menu {
	
	private final City city;
	
	/**
	 * Constructor for CityListDetailsMenu.
	 *
	 * @param owner The player who opens the menu.
	 * @param city  The city to display details for.
	 */
	public CityListDetailsMenu(Player owner, City city) {
		super(owner);
		this.city = city;
	}
	
	@Override
	public @NotNull String getName() {
		return "Détails de la ville " + city.getCityName();
	}
	
	@Override
	public @NotNull InventorySize getInventorySize() {
		return InventorySize.NORMAL;
	}
	
	@Override
	public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
	
	}
	
	@Override
	public @NotNull Map<Integer, ItemStack> getContent() {
		Map<Integer, ItemStack> map = new HashMap<>();
		
		map.put(13, new ItemBuilder(this, ItemUtils.getPlayerSkull(this.city.getPlayerWith(CPermission.OWNER)),
				itemMeta -> itemMeta.setDisplayName("§7Maire : " + Bukkit.getServer().getPlayer(this.city.getPlayerWith(CPermission.OWNER)).getName())));
		
		map.put(8, new ItemBuilder(this, new ItemStack(Bukkit.getItemFactory().getSpawnEgg(getEntityByMascotUUID(getMascotOfCity(city.getUUID()).getMascotUuid()).getType())),
				itemMeta -> itemMeta.setDisplayName("§dNiveau de la Mascotte : " + getMascotOfCity(city.getUUID()).getLevel())));
		
		map.put(9, new ItemBuilder(this, new ItemStack(Material.PAPER),
				itemMeta -> itemMeta.setDisplayName("§bTaille : " + city.getChunks().size() + " chunks")));
		
		map.put(22, new ItemBuilder(this, new ItemStack(Material.DIAMOND),
				itemMeta -> itemMeta.setDisplayName("§6Richesse : " + city.getBalance() + " " + EconomyManager.getEconomyIcon())));
		
		map.put(4, new ItemBuilder(this, new ItemStack(Material.PLAYER_HEAD),
				itemMeta -> itemMeta.setDisplayName("§bPopulation : " + city.getMembers().size() + (city.getMembers().size() > 1 ? " joueurs" : " joueur"))));
		
		map.put(18, new ItemBuilder(this, new ItemStack(Material.ARROW),
				itemMeta -> itemMeta.setDisplayName("§4Retour")).setBackButton());
		
		return map;
	}
}
