package fr.openmc.core.features.city.menu;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
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

public class NoCityMenu extends Menu {

    public NoCityMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        return "Menu des villes";
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
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> inventory = new HashMap<>();
        Player player = getOwner();

        List<Component> loreCreate = new ArrayList<>();
        loreCreate.add(Component.text("§7Vous pouvez aussi créer §dvotre Ville"));
        loreCreate.add(Component.text("§7Faites §d/city create <name>"));

        Component nameNotif;
        List<Component> loreNotif = new ArrayList<>();
        if (!CityCommands.invitations.containsKey(player)) {
            nameNotif = Component.text("§7Vous n'avez aucune §6invitation");
            loreNotif.add(Component.text("§7Le Maire d'une ville doit vous §6inviter"));
            loreNotif.add(Component.text("§6via /city invite"));

            inventory.put(15, new ItemBuilder(this, Material.CHISELED_BOOKSHELF, itemMeta -> {
                itemMeta.itemName(nameNotif);
                itemMeta.lore(loreNotif);
            }).setOnClick(inventoryClickEvent -> MessagesManager.sendMessageType(player, Component.text("Tu n'as aucune invitation en attente"), Prefix.CITY, MessageType.ERROR, false)));
        } else {
            nameNotif = Component.text("§7Vous avez une §6invitation");

            Player inviter = CityCommands.invitations.get(player);
            City inviterCity = CityManager.getPlayerCity(inviter.getUniqueId());

            assert inviterCity != null;

            loreNotif.add(Component.text("§7" + inviter.getName() + " vous a invité(e) dans " + inviterCity.getName()));
            loreNotif.add(Component.text("§e§lCLIQUEZ ICI POUR CONFIRMER"));

            inventory.put(15, new ItemBuilder(this, Material.CHISELED_BOOKSHELF, itemMeta -> {
                itemMeta.itemName(nameNotif);
                itemMeta.lore(loreNotif);
            }).setOnClick(inventoryClickEvent -> {
                ConfirmMenu menu = new ConfirmMenu(player, new NoCityMenu(player), this::accept, this::refuse, "§7Accepter", "§7Refuser" + inviter.getName());
                menu.open();
            }));
        }

        inventory.put(11, new ItemBuilder(this, Material.SCAFFOLDING, itemMeta -> {
            itemMeta.itemName(Component.text("§7Créer §dvotre ville"));
            itemMeta.lore(loreCreate);
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
