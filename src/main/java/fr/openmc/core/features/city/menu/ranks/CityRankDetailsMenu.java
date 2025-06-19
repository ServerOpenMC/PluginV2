package fr.openmc.core.features.city.menu.ranks;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.default_menu.ConfirmMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.models.CityRank;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CityRankDetailsMenu extends Menu {
	
	private final CityRank rank;
	private final City city;
	
	public CityRankDetailsMenu(Player owner, City city, CityRank rank) {
		super(owner);
		this.rank = rank;
		this.city = city;
	}
	
	public CityRankDetailsMenu(Player owner, City city, String rankName) {
		this(owner, city, new CityRank(UUID.randomUUID(), city.getUUID(), rankName, 0, new HashSet<>(), Material.GOLD_BLOCK));
	}
	
	@Override
	public @NotNull String getName() {
		return city.isRankExists(rank) ? "Créer le grade  " + rank.getName() : "Détails du grade " + rank.getName();
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
	public @NotNull Map<Integer, ItemStack> getContent() {
		return city.isRankExists(rank) ? editRank() : createRank();
	}
	
	@Override
	public List<Integer> getTakableSlot() {
		return List.of();
	}
	
	private Map<Integer, ItemStack> createRank() {
		Map<Integer, ItemStack> map = new HashMap<>();
		
		map.put(0, new ItemBuilder(this, Material.PAPER, itemMeta -> {
			itemMeta.displayName(Component.text("§dInsérer la priorité du grade"));
			itemMeta.lore(List.of(
					Component.text("§7La priorité détermine l'ordre des grades"),
					Component.text("§6§lUne priorité plus basse signifie un grade plus élevé"),
					Component.text("§7Modifiable plus tard"),
					Component.text("§7Priorité actuelle : §d" + this.rank.getPriority())
			));
		}).setOnClick(inventoryClickEvent -> {
			if (inventoryClickEvent.isLeftClick()) {
				new CityRankDetailsMenu(getOwner(), city, rank.withPriority((rank.getPriority() + 1) % 18)).open();
			} else if (inventoryClickEvent.isRightClick()) {
				new CityRankDetailsMenu(getOwner(), city, rank.withPriority((rank.getPriority() - 1 + 18) % 18)).open();
			}
		}));
		
		map.put(4, new ItemBuilder(this, Material.OAK_SIGN, itemMeta -> {
			itemMeta.displayName(Component.text("§3Changer le nom du grade"));
			itemMeta.lore(List.of(
					Component.text("§7Le nom du grade est donné lors de sa création"),
					Component.text("§7Modifiable plus tard"),
					Component.text("§7Nom actuel : §3" + (this.rank.getName().isEmpty() ? "§oNon défini" : this.rank.getName()))
			));
		}));
		
		map.put(8, new ItemBuilder(this, this.rank.getIcon(), itemMeta -> {
			itemMeta.displayName(Component.text("§9Changer l'icône du grade"));
			itemMeta.lore(List.of(
					Component.text("§7Cliquez pour changer une icône"),
					Component.text("§7Modifiable plus tard")
			));
		}).setOnClick(inventoryClickEvent -> new CityRankIconMenu(getOwner(), city, rank).open()));
		
		map.put(13, new ItemBuilder(this, Material.WRITABLE_BOOK, itemMeta -> {
			itemMeta.displayName(Component.text("§bInsérer les permissions du grade"));
			itemMeta.lore(List.of(
					Component.text("§7Cliquez pour sélectionner les permissions"),
					Component.text("§7Modifiables plus tard"),
					Component.text("§7Permissions actuelles : §b" + (this.rank.getPermissionsSet().isEmpty() ? "§oAucune" : this.rank.getPermissionsSet().size()))
			));
		}).setOnClick(inventoryClickEvent -> CityRankPermsMenu.openBook(getOwner(), rank)));
		
		map.put(18, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:refuse_btn").getBest(), itemMeta -> {
			itemMeta.displayName(Component.text("§cAnnuler et supprimer"));
			itemMeta.lore(List.of(
					Component.text("§7Cliquez pour annuler la création du grade")
			));
		}).setOnClick(inventoryClickEvent -> getOwner().closeInventory()));
		
		map.put(26, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:accept_btn").getBest(), itemMeta -> {
			itemMeta.displayName(Component.text("§aCréer le grade"));
			itemMeta.lore(List.of(
					Component.text("§7Cliquez pour créer le grade avec les paramètres définis")
			));
		}).setOnClick(inventoryClickEvent -> {
			city.createRank(rank.validate(getOwner()));
			getOwner().closeInventory();
			MessagesManager.sendMessage(getOwner(), Component.text("Grade " + this.rank.getName() + " créé avec succès !"), Prefix.CITY, MessageType.SUCCESS, false);
		}));
		
		return map;
	}
	
	private @NotNull Map<Integer, ItemStack> editRank() {
		Map<Integer, ItemStack> map = new HashMap<>();
		
		map.put(0, new ItemBuilder(this, Material.PAPER, itemMeta -> {
			itemMeta.displayName(Component.text("§dPriorité"));
			itemMeta.lore(List.of(
					Component.text("§7Cliquez pour modifier la priorité du grade"),
					Component.text("§7Priorité actuelle : §d" + this.rank.getPriority())
			));
		}).setOnClick(inventoryClickEvent -> {
			if (inventoryClickEvent.isLeftClick()) {
				new CityRankDetailsMenu(getOwner(), city, rank.withPriority((rank.getPriority() + 1) % 18)).open();
			} else if (inventoryClickEvent.isRightClick()) {
				new CityRankDetailsMenu(getOwner(), city, rank.withPriority((rank.getPriority() - 1 + 18) % 18)).open();
			}
		}));
		
		map.put(4, new ItemBuilder(this, Material.OAK_SIGN, itemMeta -> {
			itemMeta.displayName(Component.text("§3Nom du grade"));
			itemMeta.lore(List.of(
					Component.text("§7Vous pouvez modifier le nom du grade avec"),
					Component.text("§6/city rank rename <ancien nom> <nouveau nom>"),
					Component.text("§7Nom actuel : §3" + this.rank.getName())
			));
		}));
		
		map.put(8, new ItemBuilder(this, this.rank.getIcon(), itemMeta -> {
			itemMeta.displayName(Component.text("§9Changer l'icône du grade"));
			itemMeta.lore(List.of(
					Component.text("§7Cliquez pour changer l'icône du grade")
			));
		}).setOnClick(inventoryClickEvent -> new CityRankIconMenu(getOwner(), city, rank).open()));
		
		map.put(13, new ItemBuilder(this, Material.WRITABLE_BOOK, itemMeta -> {
			itemMeta.displayName(Component.text("§bModifier les permissions du grade"));
			itemMeta.lore(List.of(
					Component.text("§7Cliquez pour modifier les permissions du grade"),
					Component.text("§7Permissions actuelles : §b" + (this.rank.getPermissionsSet().isEmpty() ? "§oAucune" : this.rank.getPermissionsSet().size()))
			));
		}).setOnClick(inventoryClickEvent -> {
			CityRankPermsMenu.openBook(getOwner(), rank);
		}));
		
		map.put(18, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:refuse_btn").getBest(), itemMeta -> {
			itemMeta.displayName(Component.text("§cAnnuler"));
			itemMeta.lore(List.of(
					Component.text("§7Cliquez pour annuler les modifications"),
					Component.text("§4Aucune modification ne sera enregistrée")
			));
		}).setOnClick(inventoryClickEvent -> new CityRanksMenu(getOwner(), city).open()));
		
		map.put(22, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:minus_btn").getBest(), itemMeta -> {
			itemMeta.displayName(Component.text("§cSupprimer le grade"));
			itemMeta.lore(List.of(
					Component.text("§7Cliquez pour supprimer ce grade"),
					Component.text("§4Cette action est irréversible")
			));
		}).setOnClick(inventoryClickEvent -> new ConfirmMenu(getOwner(), () -> {
			try {
				city.deleteRank(rank);
				getOwner().closeInventory();
				MessagesManager.sendMessage(getOwner(), Component.text("Grade " + this.rank.getName() + " supprimé avec succès !"), Prefix.CITY, MessageType.SUCCESS, false);
			} catch (IllegalArgumentException e) {
				MessagesManager.sendMessage(getOwner(), Component.text("Impossible de supprimer le grade : " + e.getMessage()), Prefix.CITY, MessageType.ERROR, false);
			}
		}, () -> new CityRankDetailsMenu(getOwner(), city, rank).open(),
				List.of(Component.text("§cCette action est irréversible")), List.of()).open()));
		
		map.put(26, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:accept_btn").getBest(), itemMeta -> {
			itemMeta.displayName(Component.text("§aEnregistrer les modifications"));
			itemMeta.lore(List.of(
					Component.text("§7Cliquez pour enregistrer les modifications du grade")
			));
		}).setOnClick(inventoryClickEvent -> {
			city.updateRank(this.rank, rank.validate(getOwner()));
			getOwner().closeInventory();
			MessagesManager.sendMessage(getOwner(), Component.text("Grade " + this.rank.getName() + " modifié avec succès !"), Prefix.CITY, MessageType.SUCCESS, false);
		}));
		
		return map;
	}
}
