package fr.openmc.core.features.city.sub.mayor.menu.create;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.menu.main.CityMenu;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.menu.MayorElectionMenu;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.items.CustomItemRegistry;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MayorCreateMenu extends Menu {
    private final Perks perk1;
    private final Perks perk2;
    private final Perks perk3;
    private final MenuType type;

    public MayorCreateMenu(Player owner, Perks perk1, Perks perk2, Perks perk3, MenuType type) {
        super(owner);
        this.perk1 = perk1;
        this.perk2 = perk2;
        this.perk3 = perk3;
        this.type = type;
    }

    @Override
    public @NotNull String getName() {
	    return "Menu des maires - Creation";
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§r§f:offset_-38::mayor:");
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
        //empty
    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> inventory = new HashMap<>();
        Player player = getOwner();

        boolean canConfirmPerk;

        if (type == MenuType.OWNER_1) {
            canConfirmPerk = perk1 != null;

            ItemStack iaPerk1 = (perk1 != null) ? perk1.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
	        String namePerk1 = (perk1 != null) ? perk1.getName() : "§8Réforme vide";
            List<Component> lorePerk1;
            if (perk1 == null) {
                lorePerk1 = List.of(
		                Component.text("§7Choissiez §3votre réforme §7que vous voulez voir !"),
                        Component.empty(),
                        Component.text("§e§lCLIQUEZ ICI POUR CHOISIR LA REFORME")
                );
            } else {
                lorePerk1 = new ArrayList<>(perk1.getLore());
                lorePerk1.add(Component.empty());
                lorePerk1.add(Component.text("§e§lCLIQUEZ ICI POUR CHANGER LA REFORME"));
            }
            inventory.put(22, new ItemBuilder(this, iaPerk1, itemMeta -> {
                itemMeta.customName(Component.text(namePerk1));
                itemMeta.lore(lorePerk1);
            })
                    .hide((perk1 != null) ? perk1.getToHide() : null)
                    .setOnClick(inventoryClickEvent -> {
                        new PerkChoiceMenu(player, "perk1", perk1, perk2, perk3, type).open();
                    }));

            inventory.put(46, new ItemBuilder(this, Material.ARROW, itemMeta -> {
                itemMeta.itemName(Component.text("§aRetour"));
                itemMeta.lore(List.of(
		                Component.text("§7Vous allez retourner au menu des élections"),
                        Component.text("§e§lCLIQUEZ ICI POUR CONFIRMER")
                ));
            }, true));
        } else if (type == MenuType.CANDIDATE) {
            canConfirmPerk = perk2 != null && perk3 != null;

            ItemStack iaPerk2 = (perk2 != null) ? perk2.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
	        String namePerk2 = (perk2 != null) ? perk2.getName() : "§8Réforme vide";
            List<Component> lorePerk2;
            if (perk2 == null) {
                lorePerk2 = List.of(
		                Component.text("§7Choissiez §3votre réforme §7que vous voulez voir !"),
                        Component.empty(),
                        Component.text("§e§lCLIQUEZ ICI POUR CHOISIR LA REFORME")
                );
            } else {
                lorePerk2 = new ArrayList<>(perk2.getLore());
                lorePerk2.add(Component.empty());
                lorePerk2.add(Component.text("§e§lCLIQUEZ ICI POUR CHANGER LA REFORME"));
            }
            inventory.put(20, new ItemBuilder(this, iaPerk2, itemMeta -> {
                itemMeta.customName(Component.text(namePerk2));
                itemMeta.lore(lorePerk2);
            })
                    .hide((perk2 != null) ? perk2.getToHide() : null)
                    .setOnClick(inventoryClickEvent -> {
                        new PerkChoiceMenu(player, "perk2", perk1, perk2, perk3, type).open();
                    }));

            ItemStack iaPerk3 = (perk3 != null) ? perk3.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
	        String namePerk3 = (perk3 != null) ? perk3.getName() : "§8Réforme vide";
            List<Component> lorePerk3;
            if (perk3 == null) {
                lorePerk3 = List.of(
		                Component.text("§7Choissiez §3votre réforme §7que vous voulez voir !"),
                        Component.empty(),
                        Component.text("§e§lCLIQUEZ ICI POUR CHOISIR LA REFORME")
                );
            } else {
                lorePerk3 = new ArrayList<>(perk3.getLore());
                lorePerk3.add(Component.empty());
                lorePerk3.add(Component.text("§e§lCLIQUEZ ICI POUR CHANGER LA REFORME"));
            }
            inventory.put(24, new ItemBuilder(this, iaPerk3, itemMeta -> {
                itemMeta.customName(Component.text(namePerk3));
                itemMeta.lore(lorePerk3);
            })
                    .hide((perk3 != null) ? perk3.getToHide() : null)
                    .setOnClick(inventoryClickEvent -> {
                        new PerkChoiceMenu(player, "perk3", perk1, perk2, perk3, type).open();
                    }));

            inventory.put(46, new ItemBuilder(this, Material.ARROW, itemMeta -> {
                itemMeta.itemName(Component.text("§aRetour"));
                itemMeta.lore(List.of(
		                Component.text("§7Vous allez retourner au menu des élections"),
                        Component.text("§e§lCLIQUEZ ICI POUR CONFIRMER")
                ));
            }).setOnClick(inventoryClickEvent -> {
                MayorElectionMenu menu = new MayorElectionMenu(player);
                menu.open();
            }));
        } else if (type == MenuType.OWNER) {
            canConfirmPerk = perk1 != null && perk2 != null && perk3 != null;

            ItemStack iaPerk1 = (perk1 != null) ? perk1.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
	        String namePerk1 = (perk1 != null) ? perk1.getName() : "§8Réforme vide";
            List<Component> lorePerk1;
            if (perk1 == null) {
                lorePerk1 = List.of(
		                Component.text("§7Choissiez §3votre réforme §7que vous voulez voir !"),
                        Component.empty(),
                        Component.text("§e§lCLIQUEZ ICI POUR CHOISIR LA REFORME")
                );
            } else {
                lorePerk1 = new ArrayList<>(perk1.getLore());
                lorePerk1.add(Component.empty());
                lorePerk1.add(Component.text("§e§lCLIQUEZ ICI POUR CHANGER LA REFORME"));
            }
            inventory.put(20, new ItemBuilder(this, iaPerk1, itemMeta -> {
                itemMeta.itemName(Component.text(namePerk1));
                itemMeta.lore(lorePerk1);
            })
                    .hide((perk1 != null) ? perk1.getToHide() : null)
                    .setOnClick(inventoryClickEvent -> {
                        new PerkChoiceMenu(player, "perk1", perk1, perk2, perk3, type).open();
                    }));

            ItemStack iaPerk2 = (perk2 != null) ? perk2.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
	        String namePerk2 = (perk2 != null) ? perk2.getName() : "§8Réforme vide";
            List<Component> lorePerk2;
            if (perk2 == null) {
                lorePerk2 = List.of(
		                Component.text("§7Choissiez §3votre réforme §7que vous voulez voir !"),
                        Component.empty(),
                        Component.text("§e§lCLIQUEZ ICI POUR CHOISIR LA REFORME")
                );
            } else {
                lorePerk2 = new ArrayList<>(perk2.getLore());
                lorePerk2.add(Component.empty());
                lorePerk2.add(Component.text("§e§lCLIQUEZ ICI POUR CHANGER LA REFORME"));
            }
            inventory.put(22, new ItemBuilder(this, iaPerk2, itemMeta -> {
                itemMeta.itemName(Component.text(namePerk2));
                itemMeta.lore(lorePerk2);
            })
                    .hide((perk2 != null) ? perk2.getToHide() : null)
                    .setOnClick(inventoryClickEvent -> {
                        new PerkChoiceMenu(player, "perk2", perk1, perk2, perk3, type).open();
                    }));

            ItemStack iaPerk3 = (perk3 != null) ? perk3.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
	        String namePerk3 = (perk3 != null) ? perk3.getName() : "§8Réforme vide";
            List<Component> lorePerk3;
            if (perk3 == null) {
                lorePerk3 = List.of(
		                Component.text("§7Choissiez §3votre réforme §7que vous voulez voir !"),
                        Component.empty(),
                        Component.text("§e§lCLIQUEZ ICI POUR CHOISIR LA REFORME")
                );
            } else {
                lorePerk3 = new ArrayList<>(perk3.getLore());
                lorePerk3.add(Component.empty());
                lorePerk3.add(Component.text("§e§lCLIQUEZ ICI POUR CHANGER LA REFORME"));
            }
            inventory.put(24, new ItemBuilder(this, iaPerk3, itemMeta -> {
                itemMeta.itemName(Component.text(namePerk3));
                itemMeta.lore(lorePerk3);
            })
                    .hide((perk3 != null) ? perk3.getToHide() : null)
                    .setOnClick(inventoryClickEvent -> {
                        new PerkChoiceMenu(player, "perk3", perk1, perk2, perk3, type).open();
                    }));

            inventory.put(46, new ItemBuilder(this, Material.ARROW, itemMeta -> {
                itemMeta.itemName(Component.text("§aRetour"));
                itemMeta.lore(List.of(
		                Component.text("§7Vous allez retourner au menu de votre ville"),
                        Component.text("§e§lCLIQUEZ ICI POUR CONFIRMER")
                ));
            }).setOnClick(inventoryClickEvent -> {
                CityMenu menu = new CityMenu(player);
                menu.open();
            }));
        } else {
            canConfirmPerk = false;
        }

        Material matConfirm;
        String nameConfirm;
        List<Component> loreConfirm;
        if (canConfirmPerk) {
            matConfirm = CustomItemRegistry.getByName("omc_menus:accept_btn").getBest().getType();
            nameConfirm = "§aConfirmer";
            loreConfirm = List.of(
		            Component.text("§7Vous avez choisit toutes §ales réformes §7nécessaires"),
                    Component.empty(),
                    Component.text("§e§lCLIQUEZ ICI POUR CONTINUER LA CANDIDATURE")
            );
        } else {
            matConfirm = CustomItemRegistry.getByName("omc_menus:refuse_btn").getBest().getType();
            nameConfirm = "§cConfirmer";
            loreConfirm = List.of(
		            Component.text("§7Vous n'avez pas choisit toutes §cles réformes §7nécessaires")
            );
        }

        inventory.put(52, new ItemBuilder(this, matConfirm, itemMeta -> {
            itemMeta.itemName(Component.text(nameConfirm));
            itemMeta.lore(loreConfirm);
        }).setOnClick(inventoryClickEvent -> {
            if (canConfirmPerk) {
                if (type == MenuType.OWNER_1) {
                    MayorManager.put1Perk(CityManager.getPlayerCity(player.getUniqueId()), perk1);
                    MessagesManager.sendMessage(player, Component.text("Vous avez ajouté la Réforme : " + perk1.getName()), Prefix.MAYOR, MessageType.SUCCESS, false);
                    player.closeInventory();
                    return;
                }

                new MayorColorMenu(player, perk1, perk2, perk3, "create", type).open();
            }
        }));

        return inventory;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
