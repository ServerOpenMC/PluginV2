package fr.openmc.core.features.city.menu.list;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.CityType;
import fr.openmc.core.features.city.sub.mayor.ElectionType;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.models.Mayor;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.features.city.sub.milestone.rewards.FeaturesRewards;
import fr.openmc.core.features.city.sub.milestone.rewards.MemberLimitRewards;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.CacheOfflinePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
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
		return "Menu de détails de la ville " + city.getName();
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
	public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
	
	}
	
	@Override
	public @NotNull Map<Integer, ItemBuilder> getContent() {
		Map<Integer, ItemBuilder> map = new HashMap<>();

		List<Component> loreOwner = new ArrayList<>();

		if (MayorManager.phaseMayor == 2 && FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.MAYOR)) {
			Mayor mayor = this.city.getMayor();
			ElectionType electionType = mayor.getElectionType();
			Perks perk1 = PerkManager.getPerkById(mayor.getIdPerk1());
			Perks perk2 = PerkManager.getPerkById(mayor.getIdPerk2());
			Perks perk3 = PerkManager.getPerkById(mayor.getIdPerk3());

			loreOwner.add(Component.empty());
			loreOwner.add(Component.text(perk1 == null ? "§cErreur" : perk1.getName()));
			loreOwner.addAll(perk1 == null ? List.of() : perk1.getLore());
			if (electionType == ElectionType.OWNER_CHOOSE) {
				loreOwner.add(Component.empty());
				loreOwner.add(Component.text(perk2 == null ? "§cErreur" : perk2.getName()));
				loreOwner.addAll(perk2 == null ? List.of() : perk2.getLore());
				loreOwner.add(Component.empty());
				loreOwner.add(Component.text(perk3 == null ? "§cErreur" : perk3.getName()));
				loreOwner.addAll(perk3 == null ? List.of() : perk3.getLore());
			}

			map.put(12, new ItemBuilder(this, ItemUtils.getPlayerSkull(this.city.getPlayerWithPermission(CityPermission.OWNER)),
					itemMeta -> {
						itemMeta.displayName(Component.text("§7Propriétaire : " + CacheOfflinePlayer.getOfflinePlayer(this.city.getPlayerWithPermission(CityPermission.OWNER)).getName()));
						itemMeta.lore(loreOwner);
					})
			);

			if (electionType == ElectionType.ELECTION) {
				List<Component> loreMayor = new ArrayList<>();
				loreMayor.add(Component.empty());
				loreMayor.add(Component.text(perk2 == null ? "§cErreur" : perk2.getName()));
				loreMayor.addAll(perk2 == null ? List.of() : perk2.getLore());
				loreMayor.add(Component.empty());
				loreMayor.add(Component.text(perk3 == null ? "§cErreur" : perk3.getName()));
				loreMayor.addAll(perk3 == null ? List.of() : perk3.getLore());

				map.put(14, new ItemBuilder(this, ItemUtils.getPlayerSkull(this.city.getPlayerWithPermission(CityPermission.OWNER)),
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
			map.put(13, new ItemBuilder(this, ItemUtils.getPlayerSkull(this.city.getPlayerWithPermission(CityPermission.OWNER)),
					itemMeta -> itemMeta.displayName(Component.text("§7Propriétaire : " + CacheOfflinePlayer.getOfflinePlayer(this.city.getPlayerWithPermission(CityPermission.OWNER)).getName())))
			);
		}
		
		LivingEntity entity = (LivingEntity) city.getMascot().getEntity();

		map.put(8, new ItemBuilder(this, new ItemStack(entity != null ? city.getMascot().getMascotEgg() : Material.BARRIER),
				itemMeta -> itemMeta.displayName(Component.text(entity != null ? "§dNiveau de la Mascotte : " + city.getMascot().getLevel() : "§cAucune mascotte trouvée (bug)"))));
		
		map.put(9, new ItemBuilder(this, new ItemStack(Material.PAPER),
				itemMeta -> itemMeta.displayName(Component.text("§bTaille : " + city.getChunks().size() + " chunks"))));
		
		map.put(22, new ItemBuilder(this, new ItemStack(Material.DIAMOND),
				itemMeta -> itemMeta.displayName(Component.text("§6Richesses : " + EconomyManager.getFormattedSimplifiedNumber(city.getBalance()) + " " + EconomyManager.getEconomyIcon()))));
		
		map.put(4, new ItemBuilder(this, new ItemStack(Material.PLAYER_HEAD),
				itemMeta -> {
					itemMeta.displayName(Component.text("§bPopulation : " + city.getMembers().size() + "/" + MemberLimitRewards.getMemberLimit(city.getLevel()) + (city.getMembers().size() > 1 ? " joueurs" : " joueur")));
					itemMeta.lore(List.of(
							Component.empty(),
									Component.text("§e§lCLIQUEZ ICI POUR VOIR LES MEMBRES")
							)
					);
				}).setOnClick(inventoryClickEvent -> new CityPlayerListMenu(getOwner(), city).open()));

		map.put(26, new ItemBuilder(this, new ItemStack(city.getType().equals(CityType.WAR) ? Material.RED_BANNER : Material.GREEN_BANNER),
				itemMeta -> itemMeta.displayName(Component.text("§eType : " + city.getType().getName()))));
		map.put(18, new ItemBuilder(this, CustomStack.getInstance("_iainternal:icon_back_orange").getItemStack(),
				itemMeta -> itemMeta.displayName(Component.text("§eRetour")), true));
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
