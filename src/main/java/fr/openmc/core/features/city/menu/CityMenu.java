package fr.openmc.core.features.city.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.commands.CityCommands;
import fr.openmc.core.features.city.conditions.CityLeaveCondition;
import fr.openmc.core.features.city.conditions.CityTypeConditions;
import fr.openmc.core.features.city.mascots.Mascot;
import fr.openmc.core.features.city.mascots.MascotUtils;
import fr.openmc.core.features.city.menu.bank.CityBankMenu;
import fr.openmc.core.features.city.menu.mascots.MascotMenu;
import fr.openmc.core.features.city.menu.mascots.MascotsDeadMenu;
import fr.openmc.core.features.city.menu.playerlist.CityPlayerListMenu;
import fr.openmc.core.utils.menu.ConfirmMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.openmc.core.features.city.CityManager.getCityType;

public class CityMenu extends Menu {
	
	public CityMenu(Player owner) {
		super(owner);
	}
	
	@Override
	public @NotNull String getName() {
		return "Menu des villes";
	}
	
	@Override
	public @NotNull InventorySize getInventorySize() {
		return InventorySize.LARGER;
	}
	
	@Override
	public void onInventoryClick(InventoryClickEvent click) {
	}
	
	@Override
	public @NotNull Map<Integer, ItemStack> getContent() {
		Map<Integer, ItemStack> inventory = new HashMap<>();
		Player player = getOwner();
		
		City city = CityManager.getPlayerCity(player.getUniqueId());
		assert city != null;
		
		boolean hasPermissionRenameCity = city.hasPermission(player.getUniqueId(), CPermission.RENAME);
		boolean hasPermissionChest = city.hasPermission(player.getUniqueId(), CPermission.CHEST);
		boolean hasPermissionOwner = city.hasPermission(player.getUniqueId(), CPermission.OWNER);
		boolean hasPermissionChunkSee = city.hasPermission(player.getUniqueId(), CPermission.SEE_CHUNKS);
		
		List<Component> loreModifyCity;
		
		if (hasPermissionRenameCity || hasPermissionOwner) {
			loreModifyCity = List.of(
					Component.text("§7Maire de la Ville : " + Bukkit.getOfflinePlayer(city.getPlayerWith(CPermission.OWNER)).getName()),
					Component.text("§7Membre(s) : " + city.getMembers().size()),
					Component.text(""),
					Component.text("§e§lCLIQUEZ ICI POUR MODIFIER LA VILLE")
			);
		} else {
			loreModifyCity = List.of(
					Component.text("§7Maire de la Ville : " + Bukkit.getOfflinePlayer(city.getPlayerWith(CPermission.OWNER)).getName()),
					Component.text("§7Membre(s) : " + city.getMembers().size())
			);
		}
		
		inventory.put(4, new ItemBuilder(this, Material.BOOKSHELF, itemMeta -> {
			itemMeta.itemName(Component.text("§d" + city.getCityName()));
			itemMeta.lore(loreModifyCity);
		}).setOnClick(inventoryClickEvent -> {
			City cityCheck = CityManager.getPlayerCity(player.getUniqueId());
			if (cityCheck == null) {
				MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
				return;
			}
			
			if (hasPermissionOwner) {
				CityModifyMenu menu = new CityModifyMenu(player);
				menu.open();
			}
		}));
		
		Mascot mascot = MascotUtils.getMascotOfCity(city.getUUID());
		LivingEntity mob;
		List<Component> loreMascots;
		
		if (mascot != null) {
			mob = MascotUtils.loadMascot(mascot);
			
			if (! MascotUtils.getMascotState(city.getUUID())) {
				loreMascots = List.of(
						Component.text("§7Vie : §c" + mob.getHealth() + "§4/§c" + mob.getMaxHealth()),
						Component.text("§7Status : §cEn Attente de Soins"),
						Component.text("§7Niveau : §c" + mascot.getLevel()),
						Component.text(""),
						Component.text("§e§lCLIQUEZ ICI POUR INTERAGIR AVEC")
				);
			} else {
				loreMascots = List.of(
						Component.text("§7Vie : §c" + mob.getHealth() + "§4/§c" + mob.getMaxHealth()),
						Component.text("§7Status : §aEn Vie"),
						Component.text("§7Niveau : §c" + mascot.getLevel()),
						Component.text(""),
						Component.text("§e§lCLIQUEZ ICI POUR INTERAGIR AVEC")
				);
			}
		} else {
			mob = null;
			loreMascots = List.of(
					Component.text("§cMascotte Inexistante")
			);
		}
		
		if (mob != null) {
			inventory.put(8, new ItemBuilder(this, MascotMenu.getSpawnEgg(mob), itemMeta -> {
				itemMeta.itemName(Component.text("§cVotre Mascotte"));
				itemMeta.lore(loreMascots);
			}).setOnClick(inventoryClickEvent -> {
				if (! MascotUtils.getMascotState(city.getUUID())) {
					MascotsDeadMenu menu = new MascotsDeadMenu(player, city.getUUID());
					menu.open();
					return;
				}
				
				MascotMenu menu = new MascotMenu(player, mob);
				menu.open();
			}));
		} else {
			inventory.put(8, new ItemBuilder(this, Material.ZOMBIE_SPAWN_EGG, itemMeta -> {
				itemMeta.itemName(Component.text("§cVotre Mascotte"));
				itemMeta.lore(loreMascots);
			}));
		}
		
		List<Component> loreChunkCity;
		
		if (hasPermissionChunkSee) {
			loreChunkCity = List.of(
					Component.text("§7Votre ville a une superficie de §6" + city.getChunks().size()),
					Component.text(""),
					Component.text("§e§lCLIQUEZ ICI POUR ACCEDER A LA CARTE")
			);
		} else {
			loreChunkCity = List.of(
					Component.text("§7Votre ville a une superficie de §6" + city.getChunks().size())
			);
		}
		
		inventory.put(19, new ItemBuilder(this, Material.OAK_FENCE, itemMeta -> {
			itemMeta.itemName(Component.text("§6Taille de votre Ville"));
			itemMeta.lore(loreChunkCity);
		}).setOnClick(inventoryClickEvent -> {
			if (! hasPermissionChunkSee) {
				MessagesManager.sendMessage(player, Component.text("Vous n'avez pas les permissions de voir les claims"), Prefix.CITY, MessageType.ERROR, false);
				return;
			}
			
			CityChunkMenu menu = new CityChunkMenu(player);
			menu.open();
		}));
		
		ItemStack playerHead = ItemUtils.getPlayerSkull(player.getUniqueId());
		
		inventory.put(22, new ItemBuilder(this, playerHead, itemMeta -> {
			itemMeta.displayName(Component.text("§dListe des Membres"));
			itemMeta.lore(List.of(
					Component.text("§7Il y a actuellement §d" + city.getMembers().size() + "§7 membre(s) dans votre ville"),
					Component.text(""),
					Component.text("§e§lCLIQUEZ ICI POUR VOIR LA LISTE DES JOUEURS")
			));
		}).setOnClick(inventoryClickEvent -> {
			CityPlayerListMenu menu = new CityPlayerListMenu(player);
			menu.open();
		}));
		
		String type = CityManager.getCityType(city.getUUID());
		if (type.equals("war")) {
			type = "guerre";
		} else if (type.equals("peace")) {
			type = "paix";
		} else {
			type = "inconnu";
		}
		String finalType = type;
		
		List<Component> loreType;
		
		if (CityTypeConditions.canCityChangeType(city, player, false)) {
			loreType = List.of(
					Component.text("§7Votre ville est en " + finalType),
					Component.text(""),
					Component.text("§e§lCLIQUEZ ICI POUR INVERSER LE TYPE")
			);
		} else {
			loreType = List.of(
					Component.text("§7Votre ville est en " + finalType)
			);
		}
		
		inventory.put(25, new ItemBuilder(this, Material.NETHERITE_SWORD, itemMeta -> {
			itemMeta.itemName(Component.text("§5Le Statut de votre Ville"));
			itemMeta.lore(loreType);
		}).setOnClick(inventoryClickEvent -> {
			String cityTypeActuel = getCityType(city.getUUID());
			String cityTypeAfter = "";
			if (cityTypeActuel != null) {
				boolean war = cityTypeActuel.equals("war");
				cityTypeActuel = war ? "§cen guerre§7" : "§aen paix§7";
				cityTypeAfter = war ? "§aen paix§7" : "§cen guerre§7";
			}
			
			ConfirmMenu menu = new ConfirmMenu(player,
					() -> {
						CityCommands.changeConfirm(player);
						player.closeInventory();
					},
					() -> {
						player.closeInventory();
					},
					List.of(
							Component.text("§cEs-tu sûr de vouloir changer le type de ta §dville §7?"),
							Component.text("§7Vous allez passez d'une §dville " + cityTypeActuel + " à une §dville " + cityTypeAfter),
							Component.text("§cSi tu fais cela ta mascotte §4§lPERDERA 2 NIVEAUX")
					),
					List.of(
							Component.text("§7Ne pas changer le type de ta §dville")
					)
			);
			menu.open();
		}));
		
		List<Component> loreChestCity;
		
		if (hasPermissionChest) {
			loreChestCity = List.of(
					Component.text("§7Acceder au Coffre de votre Ville pour"),
					Component.text("§7stocker des items en commun"),
					Component.text(""),
					Component.text("§e§lCLIQUEZ ICI POUR ACCEDER AU COFFRE")
			);
		} else {
			loreChestCity = List.of(
					Component.text("§7Vous n'avez pas le §cdroit de visionner le coffre !")
			);
		}
		
		inventory.put(36, new ItemBuilder(this, Material.CHEST, itemMeta -> {
			itemMeta.itemName(Component.text("§aLe Coffre de la Ville"));
			itemMeta.lore(loreChestCity);
		}).setOnClick(inventoryClickEvent -> {
			City cityCheck = CityManager.getPlayerCity(player.getUniqueId());
			if (cityCheck == null) {
				MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
				return;
			}
			
			if (! hasPermissionChest) {
				MessagesManager.sendMessage(player, Component.text("Vous n'avez pas les permissions de voir le coffre"), Prefix.CITY, MessageType.ERROR, false);
				return;
			}
			
			if (city.getChestWatcher() != null) {
				MessagesManager.sendMessage(player, Component.text("Le coffre est déjà ouvert"), Prefix.CITY, MessageType.ERROR, false);
				return;
			}
			
			new ChestMenu(city, 1).open(player);
		}));
		
		inventory.put(40, new ItemBuilder(this, Material.GOLD_BLOCK, itemMeta -> {
			itemMeta.itemName(Component.text("§6La Banque"));
			itemMeta.lore(List.of(
					Component.text("§7Stocker votre argent et celle de votre ville"),
					Component.text("§7Contribuer au développement de votre ville"),
					Component.text(""),
					Component.text("§e§lCLIQUEZ ICI POUR ACCEDER AUX COMPTES")
			));
		}).setOnClick(inventoryClickEvent -> {
			City cityCheck = CityManager.getPlayerCity(player.getUniqueId());
			if (cityCheck == null) {
				MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOCITY.getMessage(), Prefix.CITY, MessageType.ERROR, false);
				return;
			}
			
			new CityBankMenu(player).open();
		}));
		
		
		if (! hasPermissionOwner) {
			inventory.put(44, new ItemBuilder(this, Material.OAK_DOOR, itemMeta -> {
				itemMeta.itemName(Component.text("§cPartir de la Ville"));
				itemMeta.lore(List.of(
						Component.text("§7Vous allez §cquitter §7" + city.getCityName()),
						Component.text(""),
						Component.text("§e§lCLIQUEZ ICI POUR PARTIR")
				));
			}).setOnClick(inventoryClickEvent -> {
				City cityCheck = CityManager.getPlayerCity(player.getUniqueId());
				if (! CityLeaveCondition.canCityLeave(cityCheck, player)) return;
				
				ConfirmMenu menu = new ConfirmMenu(player,
						() -> {
							CityCommands.leaveCity(player);
							player.closeInventory();
						},
						() -> {
							player.closeInventory();
						},
						List.of(Component.text("§7Voulez vous vraiment partir de " + city.getCityName() + " ?")),
						List.of(Component.text("§7Rester dans la ville " + city.getCityName()))
				);
				menu.open();
			}));
		}
		
		return inventory;
	}
}
