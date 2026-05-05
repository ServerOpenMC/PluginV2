package fr.openmc.core.features.city.sub.rank.menus;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.menu.main.CityMenu;
import fr.openmc.core.features.city.models.DBCityRank;
import fr.openmc.core.features.city.sub.rank.CityRankAction;
import fr.openmc.core.features.city.sub.rank.CityRankCondition;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fr.openmc.api.menulib.utils.ItemUtils.getDataComponentType;

public class CityRanksMenu extends Menu {
	
	private final City city;
	
	public CityRanksMenu(Player owner, City city) {
		super(owner);
		this.city = city;
	}
	
	@Override
	public @NotNull Component getName() {
		return TranslationManager.translation("feature.city.rank.menu.list.title");
	}
	
	@Override
	public String getTexture() {
		return "§r§f:offset_-48::city_template3x9:";
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
		Player player = getOwner();
		
		boolean canManageRanks = city.hasPermission(player.getUniqueId(), CityPermission.MANAGE_RANKS);
		boolean canAssignRanks = city.hasPermission(player.getUniqueId(), CityPermission.ASSIGN_RANKS);
		
		Set<DBCityRank> cityRanks = city.getRanks();
		if (!cityRanks.isEmpty()) {
			int i = 0;
			for (DBCityRank rank : cityRanks) {
				String rankName = rank.getName();
				int priority = rank.getPriority();
				Material icon = rank.getIcon() != null ? rank.getIcon() : Material.PAPER;
				
				map.put(i, new ItemBuilder(this, icon,
						itemMeta -> {
							itemMeta.displayName(TranslationManager.translation(
									"feature.city.rank.menu.list.rank.title",
									Component.text(rankName).color(NamedTextColor.YELLOW)
							).color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
							itemMeta.lore(List.of(
									TranslationManager.translation(
											"feature.city.rank.menu.list.rank.lore.priority",
											Component.text(priority).color(NamedTextColor.LIGHT_PURPLE)
									).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
									TranslationManager.translation(
											"feature.city.rank.menu.list.rank.lore.permissions",
											Component.text(rank.getPermissionsSet().size()).color(NamedTextColor.AQUA)
									).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
									Component.empty(),
									TranslationManager.translation(
											canManageRanks && CityRankCondition.canModifyRankPermissions(city, getOwner(), priority)
													? "feature.city.rank.menu.list.rank.lore.click_edit"
													: "feature.city.rank.menu.list.rank.lore.click_info"
									).color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)
							));
						}
				).setOnClick(inventoryClickEvent -> new CityRankDetailsMenu(player, city, rank).open())
						.hide(getDataComponentType()));
				if (i >= 17) break;
				i++;
			}
		}
		
		map.put(18, new ItemBuilder(this, Material.ARROW,
				itemMeta -> {
					itemMeta.displayName(TranslationManager.translation("messages.menus.back"));
					itemMeta.lore(List.of(TranslationManager.translation("messages.menus.back_lore")));
				}).setOnClick(inventoryClickEvent -> new CityMenu(getOwner()).open()));
		
		
		if (canAssignRanks) {
			List<Component> loreAssignRanks = city.getRanks().isEmpty()
					? TranslationManager.translationLore("feature.city.rank.menu.list.assign.lore.empty")
					: TranslationManager.translationLore("feature.city.rank.menu.list.assign.lore.available");

			map.put(22, new ItemBuilder(this, Material.FEATHER,
							itemMeta -> {
								itemMeta.displayName(TranslationManager.translation("feature.city.rank.menu.list.assign.title"));
								itemMeta.lore(loreAssignRanks);
							}).setOnClick(inventoryClickEvent -> {
						if (city.getRanks().isEmpty()) return;
						
						new CityRankMemberMenu(player, city).open();
					})
			);
		}
		
		if (canManageRanks) {
			List<Component> loreCreateRank = TranslationManager.translationLore("feature.city.rank.menu.list.create.lore");

			map.put(26, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:plus_btn").getBest(),
					itemMeta -> {
						itemMeta.displayName(TranslationManager.translation("feature.city.rank.menu.list.create.title"));
						itemMeta.lore(loreCreateRank);
					}).setOnClick(inventoryClickEvent -> CityRankAction.beginCreateRank(player))
			);
		}
		return map;
	}
	
	@Override
	public @NotNull InventorySize getInventorySize() {
		return InventorySize.NORMAL;
	}
	
	@Override
	public List<Integer> getTakableSlot() {
		return List.of();
	}
}
