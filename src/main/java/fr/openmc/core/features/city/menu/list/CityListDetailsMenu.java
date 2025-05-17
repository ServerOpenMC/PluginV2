package fr.openmc.core.features.city.menu.list;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.city.mayor.ElectionType;
import fr.openmc.core.features.city.mayor.Mayor;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.features.city.mayor.managers.PerkManager;
import fr.openmc.core.features.city.mayor.perks.Perks;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.CacheOfflinePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.openmc.core.features.city.mascots.MascotUtils.getEntityByMascotUUID;
import static fr.openmc.core.features.city.mascots.MascotUtils.getMascotOfCity;

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

		Mayor mayor = this.city.getMayor();
		ElectionType electionType = mayor.getElectionType();
		Perks perk1 = PerkManager.getPerkById(mayor.getIdPerk1());
		Perks perk2 = PerkManager.getPerkById(mayor.getIdPerk2());
		Perks perk3 = PerkManager.getPerkById(mayor.getIdPerk3());

		List<Component> loreOwner =  new ArrayList<>();
		loreOwner.add(Component.text(""));
		loreOwner.add(Component.text(perk1.getName()));
		loreOwner.addAll(perk1.getLore());
		if (electionType == ElectionType.OWNER_CHOOSE) {
			loreOwner.add(Component.text(""));
			loreOwner.add(Component.text(perk2.getName()));
			loreOwner.addAll(perk2.getLore());
			loreOwner.add(Component.text(""));
			loreOwner.add(Component.text(perk3.getName()));
			loreOwner.addAll(perk3.getLore());
		}

		map.put(13, new ItemBuilder(this, ItemUtils.getPlayerSkull(this.city.getPlayerWith(CPermission.OWNER)),
				itemMeta -> {
					itemMeta.displayName(Component.text("§7Propriétaire : " + CacheOfflinePlayer.getOfflinePlayer(this.city.getPlayerWith(CPermission.OWNER)).getName()));
					itemMeta.lore(loreOwner);
				})
		);

		if (MayorManager.getInstance().phaseMayor == 2 && electionType == ElectionType.ELECTION) {
			List<Component> loreMayor =  new ArrayList<>();
			loreMayor.add(Component.text(""));
			loreMayor.add(Component.text(perk2.getName()));
			loreMayor.addAll(perk2.getLore());
			loreMayor.add(Component.text(""));
			loreMayor.add(Component.text(perk3.getName()));
			loreMayor.addAll(perk3.getLore());

			map.put(14, new ItemBuilder(this, ItemUtils.getPlayerSkull(this.city.getPlayerWith(CPermission.OWNER)),
							itemMeta -> {
								itemMeta.displayName(
										Component.text("§7Maire : ")
												.append(Component.text(mayor.getName()).color(this.city.getMayor().getMayorColor()).decoration(TextDecoration.ITALIC, false))
								);
								itemMeta.lore(loreMayor);
							}
					)
			);
		}


		map.put(8, new ItemBuilder(this, new ItemStack(Bukkit.getItemFactory().getSpawnEgg(getEntityByMascotUUID(getMascotOfCity(city.getUUID()).getMascotUuid()).getType())),
				itemMeta -> itemMeta.displayName(Component.text("§dNiveau de la Mascotte : " + getMascotOfCity(city.getUUID()).getLevel()))));
		
		map.put(9, new ItemBuilder(this, new ItemStack(Material.PAPER),
				itemMeta -> itemMeta.displayName(Component.text("§bTaille : " + city.getChunks().size() + " chunks"))));
		
		map.put(22, new ItemBuilder(this, new ItemStack(Material.DIAMOND),
				itemMeta -> itemMeta.displayName(Component.text("§6Richesses : " + EconomyManager.getFormattedSimplifiedNumber(city.getBalance()) + " " + EconomyManager.getEconomyIcon()))));
		
		map.put(4, new ItemBuilder(this, new ItemStack(Material.PLAYER_HEAD),
				itemMeta -> itemMeta.displayName(Component.text("§bPopulation : " + city.getMembers().size() + (city.getMembers().size() > 1 ? " joueurs" : " joueur")))));

		map.put(26, new ItemBuilder(this, new ItemStack(city.getType().equals(CityType.WAR) ? Material.RED_BANNER : Material.GREEN_BANNER),
				itemMeta -> itemMeta.displayName(Component.text("§eType : " + (city.getType().equals(CityType.WAR) ? "§cGuerre" : "§aPaix")))));
		return map;
	}
}
