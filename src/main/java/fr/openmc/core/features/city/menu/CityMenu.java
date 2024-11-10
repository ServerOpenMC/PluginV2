package fr.openmc.core.features.city.menu;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.commands.CityCommands;
import fr.openmc.core.utils.menu.ConfirmMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityMenu extends Menu {

    public CityMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        return "Menu des Villes";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGE;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
    }

    @Override
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> inventory = new HashMap<>();
        Player player = getOwner();

        List<Component> lore_create = new ArrayList<>();
        lore_create.add(Component.text("§7Vous pouvez aussi créer §dvotre Ville"));
        lore_create.add(Component.text("§7Faite §d/city create <name>"));

        Component name_notif;
        List<Component> lore_notif = new ArrayList<>();
        if (!CityCommands.invitations.containsKey(player)) {
            name_notif = Component.text("§7Vous n'avez aucune §6invitation");
            lore_notif.add(Component.text("§7Le Maire d'une ville doit vous §6inviter"));
            lore_notif.add(Component.text("§6/city invite §7ou depuis le §6Menu de sa Ville"));

            inventory.put(15, new ItemBuilder(this, Material.BEEHIVE, itemMeta -> {
                itemMeta.itemName(name_notif);
                itemMeta.lore(lore_notif);
            }).setOnClick(inventoryClickEvent -> {
                MessagesManager.sendMessageType(player, "Tu n'as aucune invitations en attente", Prefix.CITY, MessageType.ERROR, false);
            }));
        } else {
            name_notif = Component.text("§7Vous avez une §6invitation");

            Player inviter = CityCommands.invitations.get(player);
            String inviterCity = CityManager.getPlayerCity(inviter.getUniqueId());

            lore_notif.add(Component.text("§7" + inviter.getName() + " vous a invité\n dans " + CityManager.getCityName(inviterCity)));
            lore_notif.add(Component.text("§e§lCLIQUEZ ICI POUR VOTRE CONFIRMATION"));

            inventory.put(15, new ItemBuilder(this, Material.BEEHIVE, itemMeta -> {
                itemMeta.itemName(name_notif);
                itemMeta.lore(lore_notif);
            }).setOnClick(inventoryClickEvent -> {
                ConfirmMenu menu = new ConfirmMenu(player, new CityMenu(player), this::accept, this::refuse, "§7Vous accepterez la demande d'invitation de " + inviter.getName(), "§7Vous refuserez la demande d'invitation de " + inviter.getName());
                menu.open();
            }));
        }

        inventory.put(11, new ItemBuilder(this, Material.SCAFFOLDING, itemMeta -> {
            itemMeta.itemName(Component.text("§7Créer §dvotre Ville"));
            itemMeta.lore(lore_create);
        }));

        return inventory;
    }

    private void accept() {
        Bukkit.dispatchCommand(getOwner(), "city accept");
    }

    private void refuse() {
        Bukkit.dispatchCommand(getOwner(), "city deny");
    }
}
