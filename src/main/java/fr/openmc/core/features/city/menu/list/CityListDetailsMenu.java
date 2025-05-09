package fr.openmc.core.features.city.menu.list;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.economy.EconomyManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static fr.openmc.core.features.city.mascots.MascotUtils.getEntityByMascotUUID;
import static fr.openmc.core.features.city.mascots.MascotUtils.getMascotOfCity;

public class CityListDetailsMenu extends Menu { // TODO : Adaptation pour les maires
	
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
				itemMeta -> itemMeta.displayName(Component.text("§7Maire : " + Bukkit.getServer().getOfflinePlayer(this.city.getPlayerWith(CPermission.OWNER)).getName()))));
		
		map.put(8, new ItemBuilder(this, new ItemStack(Bukkit.getItemFactory().getSpawnEgg(getEntityByMascotUUID(getMascotOfCity(city.getUUID()).getMascotUuid()).getType())),
				itemMeta -> itemMeta.displayName(Component.text("§dNiveau de la Mascotte : " + getMascotOfCity(city.getUUID()).getLevel()))));
		
		map.put(9, new ItemBuilder(this, new ItemStack(Material.PAPER),
				itemMeta -> itemMeta.displayName(Component.text("§bTaille : " + city.getChunks().size() + " chunks"))));
		
		map.put(22, new ItemBuilder(this, new ItemStack(Material.DIAMOND),
				itemMeta -> itemMeta.displayName(Component.text("§6Richesses : " + EconomyManager.getFormattedSimplifiedNumber(city.getBalance()) + " " + EconomyManager.getEconomyIcon()))));
		
		map.put(4, new ItemBuilder(this, new ItemStack(Material.PLAYER_HEAD),
				itemMeta -> itemMeta.displayName(Component.text("§bPopulation : " + city.getMembers().size() + (city.getMembers().size() > 1 ? " joueurs" : " joueur")))));
		
		map.put(26, new ItemBuilder(this, new ItemStack(CityManager.getCityType(city.getUUID()).equals("war") ? Material.RED_BANNER : Material.GREEN_BANNER),
				itemMeta -> itemMeta.displayName(Component.text("§eType : " + (CityManager.getCityType(city.getUUID()).equals("war") ? "§cGuerre" : "§aPaix")))));
		return map;
	}
}
