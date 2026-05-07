package fr.openmc.core.features.city.sub.rank.menus;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.models.DBCityRank;
import fr.openmc.core.features.city.sub.milestone.rewards.RankLimitRewards;
import fr.openmc.core.features.city.sub.rank.CityRankAction;
import fr.openmc.core.features.city.sub.rank.CityRankCondition;
import fr.openmc.core.features.city.sub.rank.CityRankManager;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.bukkit.ItemUtils;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static fr.openmc.api.menulib.utils.ItemUtils.getDataComponentType;

public class CityRankDetailsMenu extends Menu {
	
	private final DBCityRank oldRank;
	private final DBCityRank newRank;
	private final City city;
	
	public CityRankDetailsMenu(Player owner, City city, DBCityRank rank) {
		this(owner, city, rank, CityRankManager.copy(rank));
	}
	
	public CityRankDetailsMenu(Player owner, City city, DBCityRank oldRank, DBCityRank newRank) {
		super(owner);
		this.city = city;
		this.oldRank = oldRank;
		this.newRank = newRank;
	}
	
	public CityRankDetailsMenu(Player owner, City city, String rankName) {
		this(owner, city, new DBCityRank(UUID.randomUUID(), city.getUniqueId(), rankName, 0, Material.GOLD_BLOCK, new HashSet<>()));
	}
	
	@Override
	public @NotNull Component getName() {
		return city.isRankExists(oldRank)
				? TranslationManager.translation(
						"feature.city.rank.menu.details.title.edit",
						Component.text(oldRank.getName()).color(NamedTextColor.YELLOW)
				)
				: TranslationManager.translation(
						"feature.city.rank.menu.details.title.create",
						Component.text(newRank.getName()).color(NamedTextColor.YELLOW)
				);
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
		return city.isRankExists(oldRank) ? editRank() : createRank();
	}
	
	@Override
	public List<Integer> getTakableSlot() {
		return List.of();
	}
	
	/**
	 * Creates the rank creation menu content.
	 *
	 * @return A map of slot indices to ItemStacks for the rank creation menu.
	 */
	private Map<Integer, ItemBuilder> createRank() {
		Map<Integer, ItemBuilder> map = new HashMap<>();
		
		boolean canManageRanks = city.hasPermission(getOwner().getUniqueId(), CityPermission.MANAGE_RANKS);
		
		map.put(0, new ItemBuilder(this, Material.PAPER, itemMeta -> {
			itemMeta.displayName(TranslationManager.translation("feature.city.rank.menu.details.priority.title.create"));
			itemMeta.lore(TranslationManager.translationLore(
					"feature.city.rank.menu.details.priority.lore.create",
					Component.text(this.newRank.getPriority()).color(NamedTextColor.LIGHT_PURPLE)
			));
		}).setOnClick(inventoryClickEvent -> {
			if (!canManageRanks) return;
			
			if (!CityRankCondition.canModifyRankPermissions(city, getOwner(), newRank.getPriority())) {
				return;
			}
			
			if (inventoryClickEvent.isLeftClick()) {
				new CityRankDetailsMenu(getOwner(), city, newRank.withPriority((newRank.getPriority() + 1) % 18)).open();
			} else if (inventoryClickEvent.isRightClick()) {
				new CityRankDetailsMenu(getOwner(), city, newRank.withPriority((newRank.getPriority() - 1 + 18) % 18)).open();
			}
		}));
		
		map.put(4, new ItemBuilder(this, Material.OAK_SIGN, itemMeta -> {
			itemMeta.displayName(TranslationManager.translation("feature.city.rank.menu.details.name.title"));
			Component nameValue = this.newRank.getName().isEmpty()
					? TranslationManager.translation("feature.city.rank.menu.details.name.undefined")
							.decoration(TextDecoration.ITALIC, true)
					: Component.text(this.newRank.getName()).color(NamedTextColor.DARK_AQUA);
			itemMeta.lore(TranslationManager.translationLore(
					"feature.city.rank.menu.details.name.lore.create",
					nameValue
			));
		}));
		
		map.put(8, new ItemBuilder(this, this.newRank.getIcon(), itemMeta -> {
			itemMeta.displayName(TranslationManager.translation("feature.city.rank.menu.details.icon.title"));
			itemMeta.lore(TranslationManager.translationLore("feature.city.rank.menu.details.icon.lore.create"));
		}).setOnClick(inventoryClickEvent -> new CityRankIconMenu(getOwner(), city, 0, oldRank, newRank, null).open())
				.hide(getDataComponentType()));
		
		map.put(13, new ItemBuilder(this, Material.WRITABLE_BOOK, itemMeta -> {
			itemMeta.displayName(TranslationManager.translation("feature.city.rank.menu.details.perms.title"));
			Component permValue = this.newRank.getPermissionsSet().isEmpty()
					? TranslationManager.translation("feature.city.rank.menu.details.perms.none")
					: Component.text(this.newRank.getPermissionsSet().size());
			itemMeta.lore(TranslationManager.translationLore(
					"feature.city.rank.menu.details.perms.lore.create",
					permValue.color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false)
			));
		}).setOnClick(inventoryClickEvent -> new CityRankPermsMenu(getOwner(), oldRank, newRank, true, 0).open()));
		
		map.put(18, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:refuse_btn").getBest(), itemMeta -> {
			itemMeta.displayName(TranslationManager.translation("feature.city.rank.menu.details.cancel_create.title"));
			itemMeta.lore(TranslationManager.translationLore("feature.city.rank.menu.details.cancel_create.lore"));
		}).setOnClick(inventoryClickEvent -> getOwner().closeInventory()));
		
		if (canManageRanks) {
			map.put(26, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:accept_btn").getBest(), itemMeta -> {
				itemMeta.displayName(TranslationManager.translation("feature.city.rank.menu.details.create.title"));
				itemMeta.lore(TranslationManager.translationLore("feature.city.rank.menu.details.create.lore"));
			}).setOnClick(inventoryClickEvent -> {
				city.createRank(newRank.validate(getOwner()));
				getOwner().closeInventory();
				MessagesManager.sendMessage(getOwner(), TranslationManager.translation(
						"feature.city.rank.create.success",
						Component.text(this.newRank.getName()).color(NamedTextColor.YELLOW)
				), Prefix.CITY, MessageType.SUCCESS, false);
			}));
		}
		return map;
	}
	
	/**
	 * Creates the rank editing menu content.
	 *
	 * @return A map of slot indices to ItemStacks for the rank editing menu.
	 */
	private @NotNull Map<Integer, ItemBuilder> editRank() {
		Map<Integer, ItemBuilder> map = new HashMap<>();
		Player player = getOwner();
		
		boolean canManageRanks = city.hasPermission(player.getUniqueId(), CityPermission.MANAGE_RANKS)
				&& CityRankCondition.canModifyRankPermissions(city, player, oldRank.getPriority());
		
		List<Component> lorePriority = new ArrayList<>(List.of(
				TranslationManager.translation(
						"feature.city.rank.menu.details.priority.lore.current",
						Component.text(this.newRank.getPriority()).color(NamedTextColor.LIGHT_PURPLE)
				).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
		));
		if (canManageRanks) {
			lorePriority.add(Component.empty());
			lorePriority.add(TranslationManager.translation("feature.city.rank.menu.details.priority.lore.add")
					.color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));
			lorePriority.add(TranslationManager.translation("feature.city.rank.menu.details.priority.lore.remove")
					.color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));
		}
		
		map.put(0, new ItemBuilder(this, Material.PAPER, itemMeta -> {
			itemMeta.displayName(TranslationManager.translation("feature.city.rank.menu.details.priority.title.edit"));
			itemMeta.lore(lorePriority);
		}).setOnClick(inventoryClickEvent -> {
			if (!canManageRanks) return;
			
			if (inventoryClickEvent.isLeftClick()) {
				new CityRankDetailsMenu(getOwner(), city, oldRank, newRank.withPriority((newRank.getPriority() + 1) % RankLimitRewards.getRankLimit(city.getLevel()))).open();
			} else if (inventoryClickEvent.isRightClick()) {
				new CityRankDetailsMenu(getOwner(), city, oldRank, newRank.withPriority((newRank.getPriority() - 1 + RankLimitRewards.getRankLimit(city.getLevel())) % RankLimitRewards.getRankLimit(city.getLevel()))).open();
			}
		}));
		
		List<Component> loreName = new ArrayList<>(
				List.of(
						TranslationManager.translation(
								"feature.city.rank.menu.details.name.lore.current",
								Component.text(this.newRank.getName()).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.DARK_AQUA)
						).color(NamedTextColor.GRAY)
				));
		if (canManageRanks) {
			loreName.add(Component.empty());
			loreName.add(TranslationManager.translation("feature.city.rank.menu.details.name.lore.edit")
					.color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));
		}
		
		map.put(4, new ItemBuilder(this, Material.OAK_SIGN, itemMeta -> {
			itemMeta.displayName(TranslationManager.translation("feature.city.rank.menu.details.name.title"));
			itemMeta.lore(loreName);
		}).setOnClick(inventoryClickEvent -> {
			if (!canManageRanks) return;
			
			CityRankAction.renameRankFromMenu(getOwner(), oldRank, newRank);
		}));
		
		List<Component> loreIcon = new ArrayList<>(
				List.of(
						TranslationManager.translation(
								"feature.city.rank.menu.details.icon.lore.current",
								ItemUtils.getItemTranslation(newRank.getIcon()).color(NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false)
						).color(NamedTextColor.GRAY)
				)
		);
		if (canManageRanks) {
			loreIcon.add(Component.empty());
			loreIcon.add(TranslationManager.translation("feature.city.rank.menu.details.icon.lore.edit")
					.color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));
		}
		
		map.put(8, new ItemBuilder(this, this.newRank.getIcon(), itemMeta -> {
			itemMeta.displayName(TranslationManager.translation("feature.city.rank.menu.details.icon.title"));
			itemMeta.lore(loreIcon);
		}).setOnClick(inventoryClickEvent -> {
			if (!canManageRanks) return;
			
			new CityRankIconMenu(getOwner(), city, 0, oldRank, newRank, null).open();
		}).hide(getDataComponentType()));
		
		Component permValue = this.newRank.getPermissionsSet().isEmpty()
				? TranslationManager.translation("feature.city.rank.menu.details.perms.none")
						.decoration(TextDecoration.ITALIC, true)
				: Component.text(this.newRank.getPermissionsSet().size()).color(NamedTextColor.AQUA);
		List<Component> lorePerm = new ArrayList<>(
				List.of(
						TranslationManager.translation(
								"feature.city.rank.menu.details.perms.lore.current",
								permValue
						).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
				)
		);
		lorePerm.add(Component.empty());
		if (canManageRanks) {
			lorePerm.add(TranslationManager.translation("feature.city.rank.menu.details.perms.lore.edit_manage")
					.color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));
		} else {
			lorePerm.add(TranslationManager.translation("feature.city.rank.menu.details.perms.lore.edit_view")
					.color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));
		}
		
		map.put(13, new ItemBuilder(this, Material.WRITABLE_BOOK, itemMeta -> {
			itemMeta.displayName(TranslationManager.translation("feature.city.rank.menu.details.perms.title"));
			itemMeta.lore(lorePerm);
		}).setOnClick(inventoryClickEvent -> {
			if (!canManageRanks) new CityRankPermsMenu(getOwner(), oldRank, newRank, false, 0).open();
			else new CityRankPermsMenu(getOwner(), oldRank, newRank, true, 0).open();
		}));
		
		map.put(18, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:refuse_btn").getBest(), itemMeta -> {
			itemMeta.displayName(TranslationManager.translation("feature.city.rank.menu.details.cancel_edit.title"));
			itemMeta.lore(TranslationManager.translationLore("feature.city.rank.menu.details.cancel_edit.lore"));
		}).setOnClick(inventoryClickEvent -> {
			new CityRanksMenu(getOwner(), city).open();
			MessagesManager.sendMessage(getOwner(), TranslationManager.translation("feature.city.rank.update.cancelled"), Prefix.CITY, MessageType.SUCCESS, false);
		}));
		
		if (canManageRanks) {
			map.put(22, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:minus_btn").getBest(), itemMeta -> {
				itemMeta.displayName(TranslationManager.translation("feature.city.rank.menu.details.delete.title"));
				itemMeta.lore(TranslationManager.translationLore("feature.city.rank.menu.details.delete.lore"));
			}).setOnClick(inventoryClickEvent ->
					CityRankAction.deleteRank(getOwner(), oldRank.getName())
			));
			
			map.put(26, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:accept_btn").getBest(), itemMeta -> {
				itemMeta.displayName(TranslationManager.translation("feature.city.rank.menu.details.save.title"));
				itemMeta.lore(TranslationManager.translationLore("feature.city.rank.menu.details.save.lore"));
			}).setOnClick(inventoryClickEvent -> {
				city.updateRank(this.oldRank, newRank.validate(getOwner()));
				new CityRanksMenu(getOwner(), city).open();
				MessagesManager.sendMessage(getOwner(), TranslationManager.translation(
						"feature.city.rank.update.success",
						Component.text(this.newRank.getName()).color(NamedTextColor.YELLOW)
				), Prefix.CITY, MessageType.SUCCESS, false);
			}));
		}
		return map;
	}
}
