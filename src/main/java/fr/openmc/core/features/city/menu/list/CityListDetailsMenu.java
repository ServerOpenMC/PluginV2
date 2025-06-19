package fr.openmc.core.features.city.menu.list;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.city.sub.mayor.ElectionType;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.models.Mayor;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.CacheOfflinePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
		return "Détails de la ville " + city.getName();
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

		List<Component> loreOwner = new ArrayList<>();

		if (MayorManager.phaseMayor == 2) {
			Mayor mayor = this.city.getMayor();
			ElectionType electionType = mayor.getElectionType();
			Perks perk1 = PerkManager.getPerkById(mayor.getIdPerk1());
			Perks perk2 = PerkManager.getPerkById(mayor.getIdPerk2());
			Perks perk3 = PerkManager.getPerkById(mayor.getIdPerk3());

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

			map.put(12, new ItemBuilder(this, ItemUtils.getPlayerSkull(this.city.getPlayerWithPermission(CPermission.OWNER)),
					itemMeta -> {
						itemMeta.displayName(Component.text("§7Propriétaire : " + CacheOfflinePlayer.getOfflinePlayer(this.city.getPlayerWithPermission(CPermission.OWNER)).getName()));
						itemMeta.lore(loreOwner);
					})
			);

			if (electionType == ElectionType.ELECTION) {
				List<Component> loreMayor = new ArrayList<>();
				loreMayor.add(Component.text(""));
				loreMayor.add(Component.text(perk2.getName()));
				loreMayor.addAll(perk2.getLore());
				loreMayor.add(Component.text(""));
				loreMayor.add(Component.text(perk3.getName()));
				loreMayor.addAll(perk3.getLore());

				map.put(14, new ItemBuilder(this, ItemUtils.getPlayerSkull(this.city.getPlayerWithPermission(CPermission.OWNER)),
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
		} else {
			map.put(13, new ItemBuilder(this, ItemUtils.getPlayerSkull(this.city.getPlayerWithPermission(CPermission.OWNER)),
					itemMeta -> {
						itemMeta.displayName(Component.text("§7Propriétaire : " + CacheOfflinePlayer.getOfflinePlayer(this.city.getPlayerWithPermission(CPermission.OWNER)).getName()));
					})
			);
		}
		
		Entity entity = getEntityByMascotUUID(city.getMascot().getMascotUUID());
		
		map.put(8, new ItemBuilder(this, new ItemStack(entity != null ? Bukkit.getItemFactory().getSpawnEgg(entity.getType()) : Material.BARRIER),
				itemMeta -> itemMeta.displayName(Component.text(entity != null ? "§dNiveau de la Mascotte : " + city.getMascot().getLevel() : "§cAucune mascotte trouvée (bug)"))));
		
		map.put(9, new ItemBuilder(this, new ItemStack(Material.PAPER),
				itemMeta -> itemMeta.displayName(Component.text("§bTaille : " + city.getChunks().size() + " chunks"))));
		
		map.put(22, new ItemBuilder(this, new ItemStack(Material.DIAMOND),
				itemMeta -> itemMeta.displayName(Component.text("§6Richesses : " + EconomyManager.getFormattedSimplifiedNumber(city.getBalance()) + " " + EconomyManager.getEconomyIcon()))));
		
		map.put(4, new ItemBuilder(this, new ItemStack(Material.PLAYER_HEAD),
				itemMeta -> itemMeta.displayName(Component.text("§bPopulation : " + city.getMembers().size() + (city.getMembers().size() > 1 ? " joueurs" : " joueur")))));

		map.put(26, new ItemBuilder(this, new ItemStack(city.getType().equals(CityType.WAR) ? Material.RED_BANNER : Material.GREEN_BANNER),
				itemMeta -> itemMeta.displayName(Component.text("§eType : " + (city.getType().equals(CityType.WAR) ? "§cGuerre" : "§aPaix")))));
		map.put(18, new ItemBuilder(this, CustomStack.getInstance("_iainternal:icon_back_orange").getItemStack(),
				itemMeta -> itemMeta.displayName(Component.text("§eRetour")))
				.setOnClick(inventoryClickEvent -> new CityListMenu(getOwner()).open()));
		return map;
	}

	@Override
	public void onClose(InventoryCloseEvent event) {
		//empty
	}

	@Override
	public List<Integer> getTakableSlot() {
		return List.of();
	}
}
