package fr.openmc.core.features.city.menu.ranks;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityRank;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class CityRankDetailsMenu extends Menu {
	
	private final CityRank rank;
	private final City city;
	
	public CityRankDetailsMenu(Player owner, City city, CityRank rank) {
		super(owner);
		this.rank = rank;
		this.city = city;
	}
	
	public CityRankDetailsMenu(Player owner, City city, String rankName) {
		this(owner, city, new CityRank(rankName, 0, new HashSet<>(), Material.GOLD_BLOCK));
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
			itemMeta.displayName(Component.text("Insérer la priorité du grade"));
			itemMeta.lore(List.of(
					Component.text("La priorité détermine l'ordre des grades"),
					Component.text("Il sera toujours possible de le modifier plus tard"),
					Component.text("Priorité actuelle : " + this.rank.getPriority())
			));
		}).setOnClick(inventoryClickEvent -> new CityRankDetailsMenu(getOwner(), city, rank.withPriority((rank.getPriority() + 1) % 18)).open()));
		
		map.put(4, new ItemBuilder(this, Material.OAK_SIGN, itemMeta -> {
			itemMeta.displayName(Component.text("Changer le nom du grade"));
			itemMeta.lore(List.of(
					Component.text("Le nom du grade est donné lors de sa création"),
					Component.text("Il ne sera pas possible de le modifier plus tard"),
					Component.text("Nom actuel : " + (this.rank.getName().isEmpty() ? "Non défini" : this.rank.getName()))
			));
		}));
		
		map.put(8, new ItemBuilder(this, this.rank.getIcon(), itemMeta -> {
			itemMeta.displayName(Component.text("Changer l'icône du grade"));
			itemMeta.lore(List.of(
					Component.text("Cliquez pour changer une icône"),
					Component.text("Il sera toujours possible de la modifier plus tard")
			));
		}).setOnClick(inventoryClickEvent -> new CityRankIconMenu(getOwner(), city, rank).open()));
		
		map.put(13, new ItemBuilder(this, Material.WRITABLE_BOOK, itemMeta -> {
			itemMeta.displayName(Component.text("Insérer les permissions du grade"));
			itemMeta.lore(List.of(
					Component.text("Cliquez pour sélectionner les permissions"),
					Component.text("Il sera toujours possible de les modifier plus tard")
			));
		}).setOnClick(inventoryClickEvent -> {
			CityRankPermsMenu.openBook(getOwner(), rank);
		}));
		
		map.put(18, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:refuse_btn").getBest(), itemMeta -> {
			itemMeta.displayName(Component.text("Annuler"));
			itemMeta.lore(List.of(
					Component.text("Cliquez pour annuler la création du grade")
			));
		}).setOnClick(inventoryClickEvent -> getOwner().closeInventory()));
		
		map.put(26, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:accept_btn").getBest(), itemMeta -> {
			itemMeta.displayName(Component.text("Créer le grade"));
			itemMeta.lore(List.of(
					Component.text("Cliquez pour créer le grade avec les paramètres définis"),
					Component.text("Vous devrez entrer un nom pour le grade")
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
			itemMeta.displayName(Component.text("Priorité "));
			itemMeta.lore(List.of(
					Component.text("Cliquez pour modifier la priorité du grade"),
					Component.text("Priorité actuelle : " + this.rank.getPriority())
			));
		}).setOnClick(inventoryClickEvent -> new CityRankDetailsMenu(getOwner(), city, rank.withPriority((rank.getPriority() + 1) % 18)).open()));
		
		map.put(4, new ItemBuilder(this, Material.OAK_SIGN, itemMeta -> {
			itemMeta.displayName(Component.text("Nom du grade"));
			itemMeta.lore(List.of(
					Component.text("Vous ne pouvez plus modifier le nom du grade après sa création"),
					Component.text("Nom actuel : " + this.rank.getName())
			));
		}));
		
		map.put(8, new ItemBuilder(this, this.rank.getIcon(), itemMeta -> {
			itemMeta.displayName(Component.text("Changer l'icône du grade"));
			itemMeta.lore(List.of(
					Component.text("Cliquez pour changer l'icône du grade")
			));
		}).setOnClick(inventoryClickEvent -> new CityRankIconMenu(getOwner(), city, rank).open()));
		
		map.put(13, new ItemBuilder(this, Material.WRITABLE_BOOK, itemMeta -> {
			itemMeta.displayName(Component.text("Modifier les permissions du grade"));
			itemMeta.lore(List.of(
					Component.text("Cliquez pour modifier les permissions du grade"),
					Component.text("Permissions actuelles : " + this.rank.getPermissions().toString())
			));
		}).setOnClick(inventoryClickEvent -> {
			CityRankPermsMenu.openBook(getOwner(), rank);
		}));
		
		map.put(18, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:refuse_btn").getBest(), itemMeta -> {
			itemMeta.displayName(Component.text("Annuler"));
			itemMeta.lore(List.of(
					Component.text("Cliquez pour annuler les modifications"),
					Component.text("Aucune modification ne sera enregistrée")
			));
		}).setOnClick(inventoryClickEvent -> getOwner().closeInventory()));
		
		map.put(22, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:minus_btn").getBest(), itemMeta -> {
			itemMeta.displayName(Component.text("Supprimer le grade"));
			itemMeta.lore(List.of(
					Component.text("Cliquez pour supprimer ce grade"),
					Component.text("Cette action est irréversible")
			));
		}).setOnClick(inventoryClickEvent -> {
			try {
				city.deleteRank(rank);
				getOwner().closeInventory();
				MessagesManager.sendMessage(getOwner(), Component.text("Grade " + this.rank.getName() + " supprimé avec succès !"), Prefix.CITY, MessageType.SUCCESS, false);
			} catch (IllegalArgumentException e) {
				MessagesManager.sendMessage(getOwner(), Component.text("Impossible de supprimer le grade : " + e.getMessage()), Prefix.CITY, MessageType.ERROR, false);
			}
		}));
		
		map.put(26, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:accept_btn").getBest(), itemMeta -> {
			itemMeta.displayName(Component.text("Enregistrer les modifications"));
			itemMeta.lore(List.of(
					Component.text("Cliquez pour enregistrer les modifications du grade"),
					Component.text("Vous pouvez modifier le nom avec un clic droit")
			));
		}).setOnClick(inventoryClickEvent -> {
			city.updateRank(this.rank, rank.validate(getOwner()));
			getOwner().closeInventory();
			MessagesManager.sendMessage(getOwner(), Component.text("Grade " + this.rank.getName() + " modifié avec succès !"), Prefix.CITY, MessageType.SUCCESS, false);
		}));
		
		return map;
	}
}
