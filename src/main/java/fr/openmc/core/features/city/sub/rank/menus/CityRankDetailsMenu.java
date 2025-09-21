package fr.openmc.core.features.city.sub.rank.menus;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.models.DBCityRank;
import fr.openmc.core.features.city.sub.milestone.rewards.RankLimitRewards;
import fr.openmc.core.features.city.sub.rank.CityRankAction;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CityRankDetailsMenu extends Menu {
	
	private final DBCityRank oldRank;
	private final DBCityRank newRank;
	private final City city;
	
	public CityRankDetailsMenu(Player owner, City city, DBCityRank rank) {
		super(owner);
		this.oldRank = rank;
		this.newRank = rank;
		this.city = city;
	}
	
	public CityRankDetailsMenu(Player owner, City city, String rankName) {
		this(owner, city, new DBCityRank(UUID.randomUUID(), city.getUniqueId(), rankName, 0, new HashSet<>(), Material.GOLD_BLOCK));
	}
	
	@Override
	public @NotNull String getName() {
		return city.isRankExists(oldRank) ? "Menu des détails du grade " + newRank.getName() : "Menu de création du grade  " + newRank.getName();
	}

	@Override
	public String getTexture() {
		return null;
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
			itemMeta.displayName(Component.text("§dInsérer la priorité du grade"));
			itemMeta.lore(List.of(
					Component.text("§7La priorité détermine l'ordre des grades"),
					Component.text("§6§lUne priorité plus basse signifie un grade plus élevé"),
					Component.text("§7Modifiable plus tard"),
					Component.text("§7Priorité actuelle : §d" + this.newRank.getPriority())
			));
		}).setOnClick(inventoryClickEvent -> {
			if (inventoryClickEvent.isLeftClick()) {
				new CityRankDetailsMenu(getOwner(), city, newRank.withPriority((newRank.getPriority() + 1) % 18)).open();
			} else if (inventoryClickEvent.isRightClick()) {
				new CityRankDetailsMenu(getOwner(), city, newRank.withPriority((newRank.getPriority() - 1 + 18) % 18)).open();
			}
		}));
		
		map.put(4, new ItemBuilder(this, Material.OAK_SIGN, itemMeta -> {
			itemMeta.displayName(Component.text("§3Changer le nom du grade"));
			itemMeta.lore(List.of(
					Component.text("§7Le nom du grade est donné lors de sa création"),
					Component.text("§7Modifiable plus tard"),
					Component.text("§7Nom actuel : §3" + (this.newRank.getName().isEmpty() ? "§oNon défini" : this.newRank.getName()))
			));
		}));
	    
	    map.put(8, new ItemBuilder(this, this.newRank.getIcon(), itemMeta -> {
			itemMeta.displayName(Component.text("§9Changer l'icône du grade"));
			itemMeta.lore(List.of(
					Component.text("§7Cliquez pour changer une icône"),
					Component.text("§7Modifiable plus tard")
			));
	    }).setOnClick(inventoryClickEvent -> new CityRankIconMenu(getOwner(), city, 0, newRank, null).open()));
		
		map.put(13, new ItemBuilder(this, Material.WRITABLE_BOOK, itemMeta -> {
			itemMeta.displayName(Component.text("§bInsérer les permissions du grade"));
			itemMeta.lore(List.of(
					Component.text("§7Cliquez pour sélectionner les permissions"),
					Component.text("§7Modifiables plus tard"),
					Component.text("§7Permissions actuelles : §b" + (this.newRank.getPermissionsSet().isEmpty() ? "Aucune" : this.newRank.getPermissionsSet().size()))
			));
		}).setOnClick(inventoryClickEvent -> CityRankPermsMenu.openBook(getOwner(), newRank, true)));
		
		map.put(18, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:refuse_btn").getBest(), itemMeta -> {
			itemMeta.displayName(Component.text("§cAnnuler et supprimer"));
			itemMeta.lore(List.of(
					Component.text("§7Cliquez pour annuler la création du grade")
			));
		}).setOnClick(inventoryClickEvent -> getOwner().closeInventory()));

		if (canManageRanks) {
			map.put(26, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:accept_btn").getBest(), itemMeta -> {
				itemMeta.displayName(Component.text("§aCréer le grade"));
				itemMeta.lore(List.of(
						Component.text("§7Cliquez pour créer le grade avec les paramètres définis")
				));
			}).setOnClick(inventoryClickEvent -> {
				city.createRank(newRank.validate(getOwner()));
				getOwner().closeInventory();
				MessagesManager.sendMessage(getOwner(), Component.text("Grade " + this.newRank.getName() + " créé avec succès !"), Prefix.CITY, MessageType.SUCCESS, false);
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


		boolean canManageRanks = city.hasPermission(player.getUniqueId(), CityPermission.MANAGE_RANKS);
	    
	    List<Component> lorePriority = new ArrayList<>(List.of(Component.text("§7Priorité actuelle : §d" + this.newRank.getPriority())));
		if (canManageRanks) {
			lorePriority.add(Component.empty());
			lorePriority.add(Component.text("§e§lCLIQUEZ GAUCHE POUR AJOUTER 1"));
			lorePriority.add(Component.text("§e§lCLIQUEZ DROIT POUR RETIRER 1"));
		}

		map.put(0, new ItemBuilder(this, Material.PAPER, itemMeta -> {
			itemMeta.displayName(Component.text("§dPriorité"));
			itemMeta.lore(lorePriority);
		}).setOnClick(inventoryClickEvent -> {
			if (!canManageRanks) return;
			
			if (inventoryClickEvent.isLeftClick()) {
				new CityRankDetailsMenu(getOwner(), city, newRank.withPriority((newRank.getPriority() + 1) % RankLimitRewards.getRankLimit(city.getLevel()))).open();
			} else if (inventoryClickEvent.isRightClick()) {
				new CityRankDetailsMenu(getOwner(), city, newRank.withPriority((newRank.getPriority() - 1 + RankLimitRewards.getRankLimit(city.getLevel())) % RankLimitRewards.getRankLimit(city.getLevel()))).open();
			}
		}));

		List<Component> loreName = new ArrayList<>(
				List.of(
						Component.text("§7Nom actuel : §3" + this.newRank.getName()
						)
				));
		if (canManageRanks) {
			loreName.add(Component.empty());
			loreName.add(Component.text("§e§lCLIQUEZ POUR MODIFIER LE NOM"));
		}

		map.put(4, new ItemBuilder(this, Material.OAK_SIGN, itemMeta -> {
			itemMeta.displayName(Component.text("§3Nom du grade"));
			itemMeta.lore(loreName);
		}).setOnClick(inventoryClickEvent -> {
			if (!canManageRanks) return;
			
			CityRankAction.renameRank(getOwner(), newRank.getName());
		}));

		List<Component> loreIcon = new ArrayList<>(
				List.of(
						Component.text("§7Voici votre icone actuelle : §9").append(ItemUtils.getItemTranslation(newRank.getIcon()).color(NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false))
				)
		);
		if (canManageRanks) {
			loreIcon.add(Component.empty());
			loreIcon.add(Component.text("§e§lCLIQUEZ POUR CHANGER l'ICONE"));
		}
	    
	    map.put(8, new ItemBuilder(this, this.newRank.getIcon(), itemMeta -> {
			itemMeta.displayName(Component.text("§9Icône du grade"));
			itemMeta.lore(loreIcon);
		}).setOnClick(inventoryClickEvent -> {
			if (!canManageRanks) return;
		    
		    new CityRankIconMenu(getOwner(), city, 0, newRank, null).open();
		}));

		List<Component> lorePerm = new ArrayList<>(
				List.of(
						Component.text("§7Permissions actuelles : §b" + (this.newRank.getPermissionsSet().isEmpty() ? "§oAucune" : this.newRank.getPermissionsSet().size())).decoration(TextDecoration.ITALIC, false)
				)
		);
		lorePerm.add(Component.empty());
	    if (canManageRanks) {
		    lorePerm.add(Component.text("§e§lCLIQUEZ POUR CHANGER l'ICONE"));
	    } else {
		    lorePerm.add(Component.text("§e§lCLIQUEZ POUR VOIR LES PERMISSIONS"));
	    }
		
		map.put(13, new ItemBuilder(this, Material.WRITABLE_BOOK, itemMeta -> {
			itemMeta.displayName(Component.text("§bLes permissions du grade"));
			itemMeta.lore(lorePerm);
		}).setOnClick(inventoryClickEvent ->
				CityRankPermsMenu.openBook(getOwner(), newRank, canManageRanks)
		));
		
		map.put(18, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:refuse_btn").getBest(), itemMeta -> {
			itemMeta.displayName(Component.text("§cAnnuler"));
			itemMeta.lore(List.of(
					Component.text("§7Cliquez pour annuler les modifications"),
					Component.text("§4Aucune modification ne sera enregistrée")
			));
		}).setOnClick(inventoryClickEvent -> new CityRanksMenu(getOwner(), city).open()));

		if (canManageRanks) {
			map.put(22, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:minus_btn").getBest(), itemMeta -> {
				itemMeta.displayName(Component.text("§cSupprimer le grade"));
				itemMeta.lore(List.of(
						Component.text("§7Cliquez pour supprimer ce grade"),
						Component.text("§4Cette action est irréversible")
				));
			}).setOnClick(inventoryClickEvent ->
					CityRankAction.deleteRank(getOwner(), oldRank.getName())
			));

			map.put(26, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:accept_btn").getBest(), itemMeta -> {
				itemMeta.displayName(Component.text("§aEnregistrer les modifications"));
				itemMeta.lore(List.of(
						Component.text("§7Cliquez pour enregistrer les modifications du grade")
				));
			}).setOnClick(inventoryClickEvent -> {
				city.updateRank(this.oldRank, newRank.validate(getOwner()));
				new CityRanksMenu(getOwner(), city).open();
				MessagesManager.sendMessage(getOwner(), Component.text("Grade " + this.newRank.getName() + " modifié avec succès !"), Prefix.CITY, MessageType.SUCCESS, false);
			}));
		}
		
		return map;
	}
}
