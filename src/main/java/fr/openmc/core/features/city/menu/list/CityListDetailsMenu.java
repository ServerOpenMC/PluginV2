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
import fr.openmc.core.utils.bukkit.SkullUtils;
import fr.openmc.core.utils.cache.PlayerNameCache;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
	public @NotNull Component getName() {
		return TranslationManager.translation("feature.city.menus.list.details.name", Component.text(city.getName()));
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

		map.put(0, new ItemBuilder(this, Material.DIAMOND,
				itemMeta ->
						itemMeta.displayName(TranslationManager.translation("feature.city.menus.list.details.level", Component.text(this.city.getLevel()).color(NamedTextColor.DARK_AQUA)))).hide(ItemUtils.getDataComponentType())
		);

		List<Component> loreOwner = new ArrayList<>();

		if (MayorManager.phaseMayor == 2 && FeaturesRewards.hasUnlockFeature(city, FeaturesRewards.Feature.MAYOR)) {
			Mayor mayor = this.city.getMayor();
			ElectionType electionType = mayor.getElectionType();
			Perks perk1 = PerkManager.getPerkById(mayor.getIdPerk1());
			Perks perk2 = PerkManager.getPerkById(mayor.getIdPerk2());
			Perks perk3 = PerkManager.getPerkById(mayor.getIdPerk3());

			loreOwner.add(Component.empty());
			loreOwner.add(perk1 == null ? TranslationManager.translation("feature.city.menus.common.error") :
					TranslationManager.translation(perk1.getNameKey()));
			loreOwner.addAll(perk1 == null ? List.of() : TranslationManager.translationLore(perk1.getLoreKey()));
			if (electionType == ElectionType.OWNER_CHOOSE) {
				loreOwner.add(Component.empty());
				loreOwner.add(perk2 == null ? TranslationManager.translation("feature.city.menus.common.error") :
						TranslationManager.translation(perk2.getNameKey()));
				loreOwner.addAll(perk2 == null ? List.of() : TranslationManager.translationLore(perk2.getLoreKey()));
				loreOwner.add(Component.empty());
				loreOwner.add(perk3 == null ? TranslationManager.translation("feature.city.menus.common.error") :
						TranslationManager.translation(perk3.getNameKey()));
				loreOwner.addAll(perk3 == null ? List.of() : TranslationManager.translationLore(perk3.getLoreKey()));
			}

			UUID ownerUUID = this.city.getPlayerWithPermission(CityPermission.OWNER);

			map.put(12, new ItemBuilder(this, SkullUtils.getPlayerSkull(ownerUUID),
					itemMeta -> {
						itemMeta.displayName(TranslationManager.translation(
								"feature.city.menus.list.details.owner",
								PlayerNameCache.name(ownerUUID).color(NamedTextColor.GRAY)
						));
						itemMeta.lore(loreOwner);
					}).hide(ItemUtils.getDataComponentType())
			);

			if (electionType == ElectionType.ELECTION) {
				List<Component> loreMayor = new ArrayList<>();
				loreMayor.add(Component.empty());
				loreMayor.add(perk2 == null ? TranslationManager.translation("feature.city.menus.common.error") :
						TranslationManager.translation(perk2.getNameKey()));
				loreMayor.addAll(perk2 == null ? List.of() : TranslationManager.translationLore(perk2.getLoreKey()));
				loreMayor.add(Component.empty());
				loreMayor.add(perk3 == null ? TranslationManager.translation("feature.city.menus.common.error") :
						TranslationManager.translation(perk3.getNameKey()));
				loreMayor.addAll(perk3 == null ? List.of() : TranslationManager.translationLore(perk3.getLoreKey()));

				map.put(14, new ItemBuilder(this, SkullUtils.getPlayerSkull(this.city.getPlayerWithPermission(CityPermission.OWNER)),
								itemMeta -> {
									itemMeta.displayName(
										TranslationManager.translation(
												"feature.city.menus.list.details.mayor",
												mayor.getName().color(this.city.getMayor().getMayorColor()).decoration(TextDecoration.ITALIC, false)
										)
									);
									itemMeta.lore(loreMayor);
								}
						).hide(ItemUtils.getDataComponentType())
				);
			}
		} else {
			map.put(13, new ItemBuilder(this, SkullUtils.getPlayerSkull(this.city.getPlayerWithPermission(CityPermission.OWNER)),
					itemMeta -> itemMeta.displayName(TranslationManager.translation(
							"feature.city.menus.list.details.owner",
							PlayerNameCache.name(this.city.getPlayerWithPermission(CityPermission.OWNER)).color(NamedTextColor.GRAY)
					)))
			);
		}
		
		LivingEntity entity = (LivingEntity) city.getMascot().getEntity();

		map.put(8, new ItemBuilder(this, new ItemStack(entity != null ? city.getMascot().getMascotEgg() : Material.BARRIER),
				itemMeta -> itemMeta.displayName(entity != null
						? TranslationManager.translation("feature.city.menus.list.details.mascot.level", Component.text(city.getMascot().getLevel()).color(NamedTextColor.LIGHT_PURPLE))
						: TranslationManager.translation("feature.city.menus.list.details.mascot.missing"))));

		map.put(9, new ItemBuilder(this, new ItemStack(Material.PAPER),
				itemMeta -> itemMeta.displayName(TranslationManager.translation("feature.city.menus.list.details.size", Component.text(city.getChunks().size()).color(NamedTextColor.AQUA)))));

		map.put(22, new ItemBuilder(this, new ItemStack(Material.DIAMOND),
				itemMeta -> itemMeta.displayName(TranslationManager.translation(
						"feature.city.menus.list.details.wealth",
						Component.text(EconomyManager.getFormattedSimplifiedNumber(city.getBalance())).color(NamedTextColor.GOLD),
						Component.text(EconomyManager.getEconomyIcon()).color(NamedTextColor.GOLD)
				))));

		map.put(4, new ItemBuilder(this, new ItemStack(Material.PLAYER_HEAD),
				itemMeta -> {
					itemMeta.displayName(TranslationManager.translation(
							"feature.city.menus.list.details.population",
							Component.text(city.getMembers().size()).color(NamedTextColor.AQUA),
							Component.text(MemberLimitRewards.getMemberLimit(city.getLevel())).color(NamedTextColor.AQUA),
							Component.text(city.getMembers().size() > 1 ? "s" : "")
					));
					itemMeta.lore(TranslationManager.translationLore("feature.city.menus.list.details.population.lore"));
				}).setOnClick(inventoryClickEvent -> new CityPlayerListMenu(getOwner(), city).open()));

		map.put(26, new ItemBuilder(this, new ItemStack(city.getType().equals(CityType.WAR) ? Material.RED_BANNER : Material.GREEN_BANNER),
				itemMeta -> itemMeta.displayName(TranslationManager.translation("feature.city.menus.list.details.type", city.getType().getDisplayName()))));
		map.put(18, new ItemBuilder(this, CustomStack.getInstance("_iainternal:icon_back_orange").getItemStack(),
				itemMeta -> itemMeta.displayName(TranslationManager.translation("messages.menus.back")), true));
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
