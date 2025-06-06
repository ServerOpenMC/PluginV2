package fr.openmc.core.features.city.menu.ranks;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.CityRanks;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityRankDetailsMenu extends Menu {
	
	private final CityRanks rank;
	
	public CityRankDetailsMenu(Player owner, CityRanks rank) {
		super(owner);
		this.rank = rank;
	}
	
	public CityRankDetailsMenu(Player owner) {
		super(owner);
		this.rank = null; // For creating a new rank
	}
	
	@Override
	public @NotNull String getName() {
		return rank != null ? "Détails du grade " + rank.name() : "Créer un grade";
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
		return rank != null ? editRank() : createRank();
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
					Component.text("Il sera toujours possible de le modifier plus tard")
			));
		}).setOnClick(inventoryClickEvent -> {
			//TODO Logic to handle priority input
		}));
		
		map.put(4, new ItemBuilder(this, Material.OAK_SIGN, itemMeta -> {
			itemMeta.displayName(Component.text("Insérer le nom du grade"));
			itemMeta.lore(List.of(
					Component.text("Il sera toujours possible de le modifier plus tard"),
					Component.text("Nom actuel : Non défini")
			));
		}).setOnClick(inventoryClickEvent -> {
			//TODO Logic to handle name input
		}));
		
		map.put(8, new ItemBuilder(this, Material.GOLD_BLOCK, itemMeta -> {
			itemMeta.displayName(Component.text("Changer l'icône du grade"));
			itemMeta.lore(List.of(
					Component.text("Cliquez pour changer une icône"),
					Component.text("Il sera toujours possible de la modifier plus tard")
			));
		}).setOnClick(inventoryClickEvent -> {
			//TODO Logic to handle icon selection
		}));
		
		map.put(13, new ItemBuilder(this, Material.WRITABLE_BOOK, itemMeta -> {
			itemMeta.displayName(Component.text("Insérer les permissions du grade"));
			itemMeta.lore(List.of(
					Component.text("Cliquez pour sélectionner les permissions"),
					Component.text("Il sera toujours possible de les modifier plus tard")
			));
		}).setOnClick(inventoryClickEvent -> {
			//TODO Logic to handle permissions selection
		}));
		
		map.put(18, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:cancel_btn").getBest(), itemMeta -> {
			itemMeta.displayName(Component.text("Annuler"));
			itemMeta.lore(List.of(
					Component.text("Cliquez pour annuler la création du grade")
			));
		}).setOnClick(inventoryClickEvent -> {
			getOwner().closeInventory();
		}));
		
		map.put(26, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:accept_btn").getBest(), itemMeta -> {
			itemMeta.displayName(Component.text("Créer le grade"));
			itemMeta.lore(List.of(
					Component.text("Cliquez pour créer le grade avec les paramètres définis")
			));
		}).setOnClick(inventoryClickEvent -> {
			//TODO Logic to create the rank with the defined parameters
		}));
		
		return map;
	}
	
	private Map<Integer, ItemStack> editRank() {
		Map<Integer, ItemStack> map = new HashMap<>();
		
		map.put(0, new ItemBuilder(this, Material.PAPER, itemMeta -> {
			itemMeta.displayName(Component.text("Priorité : " + rank.priority()));
			itemMeta.lore(List.of(
					Component.text("Cliquez pour modifier la priorité du grade"),
					Component.text("Priorité actuelle : " + rank.priority())
			));
		}).setOnClick(inventoryClickEvent -> {
			//TODO Logic to handle priority modification
		}));
		
		map.put(4, new ItemBuilder(this, Material.OAK_SIGN, itemMeta -> {
			itemMeta.displayName(Component.text("Modifier le nom du grade"));
			itemMeta.lore(List.of(
					Component.text("Cliquez pour modifier le nom du grade"),
					Component.text("Nom actuel : " + rank.name())
			));
		}).setOnClick(inventoryClickEvent -> {
			//TODO Logic to handle name modification
		}));
		
		map.put(8, new ItemBuilder(this, rank.icon() != null ? rank.icon() : Material.GOLD_BLOCK, itemMeta -> {
			itemMeta.displayName(Component.text("Changer l'icône du grade"));
			itemMeta.lore(List.of(
					Component.text("Cliquez pour changer l'icône du grade")
			));
		}).setOnClick(inventoryClickEvent -> {
			//TODO Logic to handle icon selection
		}));
		
		map.put(13, new ItemBuilder(this, Material.WRITABLE_BOOK, itemMeta -> {
			itemMeta.displayName(Component.text("Modifier les permissions du grade"));
			itemMeta.lore(List.of(
					Component.text("Cliquez pour modifier les permissions du grade"),
					Component.text("Permissions actuelles : " + rank.permissions().toString())
			));
		}).setOnClick(inventoryClickEvent -> {
			//TODO Logic to handle permissions modification
		}));
		
		map.put(18, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:cancel_btn").getBest(), itemMeta -> {
			itemMeta.displayName(Component.text("Annuler"));
			itemMeta.lore(List.of(
					Component.text("Cliquez pour annuler les modifications")
			));
		}).setOnClick(inventoryClickEvent -> {
			getOwner().closeInventory();
		}));
		
		map.put(21, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:delete_btn").getBest(), itemMeta -> {
			itemMeta.displayName(Component.text("Supprimer le grade"));
			itemMeta.lore(List.of(
					Component.text("Cliquez pour supprimer ce grade"),
					Component.text("Cette action est irréversible")
			));
		}).setOnClick(inventoryClickEvent -> {
			//TODO Logic to delete the rank
		}));
		
		map.put(26, new ItemBuilder(this, CustomItemRegistry.getByName("omc_menus:accept_btn").getBest(), itemMeta -> {
			itemMeta.displayName(Component.text("Enregistrer les modifications"));
			itemMeta.lore(List.of(
					Component.text("Cliquez pour enregistrer les modifications du grade")
			));
		}).setOnClick(inventoryClickEvent -> {
			//TODO Logic to save the modified rank
		}));
		
		return map;
	}
}
