package fr.openmc.core.features.city.sub.mayor.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.menu.CityMenu;
import fr.openmc.core.features.city.sub.mayor.Mayor;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.utils.CacheOfflinePlayer;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MayorMandateMenu extends Menu {

    public MayorMandateMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        return "Menu des Maires - Mandat";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.NORMAL;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
        //empty
    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> inventory = new HashMap<>();
        Player player = getOwner();
        try {
            City city = CityManager.getPlayerCity(player.getUniqueId());
            Mayor mayor = city.getMayor();

            Perks perk1 = PerkManager.getPerkById(mayor.getIdPerk1());
            Perks perk2 = PerkManager.getPerkById(mayor.getIdPerk2());
            Perks perk3 = PerkManager.getPerkById(mayor.getIdPerk3());

            List<Component> loreMayor =  new ArrayList<>(List.of(
                    Component.text("§8§oMaire de " + city.getName())
            ));
            loreMayor.add(Component.text(""));
            loreMayor.add(Component.text(perk2.getName()));
            loreMayor.addAll(perk2.getLore());
            loreMayor.add(Component.text(""));
            loreMayor.add(Component.text(perk3.getName()));
            loreMayor.addAll(perk3.getLore());


            inventory.put(3, new ItemBuilder(this, ItemUtils.getPlayerSkull(mayor.getUUID()),itemMeta -> {
                itemMeta.displayName(Component.text("Maire " + mayor.getName()).color(mayor.getMayorColor()).decoration(TextDecoration.ITALIC, false));
                itemMeta.lore(loreMayor);
            }));

            // ACCES DES LOIS
            // - PVP ENTRE MEMBRES (activé/désactiver) - Maire
            // - Annonce Ville (genre de broadcast ds ville) - Maire
            // - /city warp (donc le setspawnpoint de la ville en gros) => baton de set warp - Maire
            // - Evenement Déclanchable - Maire

            // si le joueur est maire

            if (player.getUniqueId().equals(mayor.getUUID())) {
                List<Component> loreLaw = List.of(
                        Component.text("§7Vous êtes le ").append(Component.text("Maire").color(mayor.getMayorColor()).decoration(TextDecoration.ITALIC, false).append(Component.text("§7!"))),
                        Component.text(""),
                        Component.text("§7Vous pouvez changer les §1Lois §7et lancer des §6Evenements §7!"),
                        Component.text(""),
                        Component.text("§e§lCLIQUEZ ICI POUR OUVRIR UN MENU")

                );
                inventory.put(4, new ItemBuilder(this, Material.STONE_BUTTON, itemMeta -> {
                    itemMeta.itemName(Component.text("§1Les Lois"));
                    itemMeta.lore(loreLaw);
                }).setOnClick(event -> {
                    new MayorLawMenu(player).open();
                }));
            }

            List<Component> loreOwner =  new ArrayList<>(List.of(
                    Component.text("§8§oPropriétaire de " + city.getName())
            ));
            loreOwner.add(Component.text(""));
            loreOwner.add(Component.text(perk1.getName()));
            loreOwner.addAll(perk1.getLore());

            inventory.put(5, new ItemBuilder(this, ItemUtils.getPlayerSkull(city.getPlayerWith(CPermission.OWNER)),itemMeta -> {
                itemMeta.displayName(Component.text("§ePropriétaire " + CacheOfflinePlayer.getOfflinePlayer(city.getPlayerWith(CPermission.OWNER)).getName()));
                itemMeta.lore(loreOwner);
            }));

            ItemStack iaPerk1 = (perk1 != null) ? perk1.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
            String namePerk1 = (perk1 != null) ? perk1.getName() : "§8Réforme Vide";
            List<Component> lorePerk1 = (perk1 != null) ? new ArrayList<>(perk1.getLore()) : null;
            inventory.put(10, new ItemBuilder(this, iaPerk1, itemMeta -> {
                itemMeta.customName(Component.text(namePerk1));
                itemMeta.lore(lorePerk1);
                itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            }));

            ItemStack iaPerk2 = (perk2 != null) ? perk2.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
            String namePerk2 = (perk2 != null) ? perk2.getName() : "§8Réforme Vide";
            List<Component> lorePerk2 = (perk2 != null) ? new ArrayList<>(perk2.getLore()) : null;
            inventory.put(13, new ItemBuilder(this, iaPerk2, itemMeta -> {
                itemMeta.customName(Component.text(namePerk2));
                itemMeta.lore(lorePerk2);
                itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            }));

            ItemStack iaPerk3 = (perk3 != null) ? perk3.getItemStack() : ItemStack.of(Material.DEAD_BRAIN_CORAL_BLOCK);
            String namePerk3 = (perk3 != null) ? perk3.getName() : "§8Réforme Vide";
            List<Component> lorePerk3 = (perk3 != null) ? new ArrayList<>(perk3.getLore()) : null;
            inventory.put(16, new ItemBuilder(this, iaPerk3, itemMeta -> {
                itemMeta.customName(Component.text(namePerk3));
                itemMeta.lore(lorePerk3);
                itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            }));

            inventory.put(18, new ItemBuilder(this, Material.ARROW, itemMeta -> {
                itemMeta.itemName(Component.text("§aRetour"));
                itemMeta.lore(List.of(
                        Component.text("§7Vous allez retourner au Menu des Villes"),
                        Component.text("§e§lCLIQUEZ ICI POUR CONFIRMER")
                ));
            }).setOnClick(inventoryClickEvent -> {
                new CityMenu(player).open();
            }));

            List<Component> loreInfo = Arrays.asList(
                    Component.text("§7Apprenez en plus sur les Maires !"),
                    Component.text("§7Le déroulement..., Les éléctions, ..."),
                    Component.text("§e§lCLIQUEZ ICI POUR EN VOIR PLUS!")
            );

            inventory.put(26, new ItemBuilder(this, Material.BOOK, itemMeta -> {
                itemMeta.displayName(Component.text("§r§aPlus d'info !"));
                itemMeta.lore(loreInfo);
            }).setNextMenu(new MoreInfoMenu(getOwner())));

            return inventory;
        } catch (Exception e) {
            MessagesManager.sendMessage(player, Component.text("§cUne Erreur est survenue, veuillez contacter le Staff"), Prefix.OPENMC, MessageType.ERROR, false);
            player.closeInventory();
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
