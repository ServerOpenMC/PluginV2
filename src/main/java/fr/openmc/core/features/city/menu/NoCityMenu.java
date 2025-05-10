package fr.openmc.core.features.city.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.signgui.SignGUI;
import fr.openmc.api.signgui.exception.SignGUIVersionException;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.commands.CityCommands;
import fr.openmc.core.features.city.conditions.CityCreateConditions;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.InputUtils;
import fr.openmc.core.utils.ItemUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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

        List<Component> loreCreate = List.of(
                Component.text("§7Vous pouvez aussi créer §dvotre Ville"),
                Component.text("§7Faites §d/city create <name> §7ou bien cliquez ici !"),
                Component.text(""),
                Component.text("§cCoûte :"),
                Component.text("§8- §6"+ CityCreateConditions.MONEY_CREATE + EconomyManager.getEconomyIcon()).decoration(TextDecoration.ITALIC, false),
                Component.text("§8- §d"+ CityCreateConditions.AYWENITE_CREATE + " d'Aywenite"),
                Component.text(""),
                Component.text("§e§lCLIQUEZ ICI POUR CREER VOTRE VILLE")
        );


        Component nameNotif;
        List<Component> loreNotif = new ArrayList<>();
        if (!CityCommands.invitations.containsKey(player)) {
            nameNotif = Component.text("§7Vous n'avez aucune §6invitation");
            loreNotif.add(Component.text("§7Le Maire d'une ville doit vous §6inviter"));
            loreNotif.add(Component.text("§6via /city invite"));

            inventory.put(15, new ItemBuilder(this, Material.CHISELED_BOOKSHELF, itemMeta -> {
                itemMeta.itemName(nameNotif);
                itemMeta.lore(loreNotif);
            }).setOnClick(inventoryClickEvent -> MessagesManager.sendMessage(player, Component.text("Tu n'as aucune invitation en attente"), Prefix.CITY, MessageType.ERROR, false)));
        } else {
            List<Player> invitations = CityCommands.invitations.get(player);
            nameNotif = Component.text("§7Vous avez §6" + invitations.size() + " invitation" + (invitations.size() > 1 ? "s" : ""));

            loreNotif.add(Component.text("§e§lCLIQUEZ ICI POUR VOIR VOS INVITATIONS"));

            inventory.put(15, new ItemBuilder(this, Material.BOOKSHELF, itemMeta -> {
                itemMeta.itemName(nameNotif);
                itemMeta.lore(loreNotif);
            }).setOnClick(inventoryClickEvent -> {
                new InvitationsMenu(player).open();
            }));
        }

        inventory.put(11, new ItemBuilder(this, Material.SCAFFOLDING, itemMeta -> {
            itemMeta.itemName(Component.text("§7Créer §dvotre ville"));
            itemMeta.lore(loreCreate);
        }).setOnClick(inventoryClickEvent -> {
            if (!CityCreateConditions.canCityCreate(player)) {
                return;
            }

            String[] lines = new String[4];
            lines[0] = "";
            lines[1] = " ᐱᐱᐱᐱᐱᐱᐱ ";
            lines[2] = "Entrez votre nom";
            lines[3] = "de ville ci dessus";

            SignGUI gui = null;
            try {
                gui = SignGUI.builder()
                        .setLines(null, lines[1] , lines[2], lines[3])
                        .setType(ItemUtils.getSignType(player))
                        .setHandler((p, result) -> {
                            String input = result.getLine(0);

                            for (City city : CityManager.getCities()){
                                String cityName = city.getCityName();
                                if (cityName!=null && cityName.equalsIgnoreCase(input)){
                                    MessagesManager.sendMessage(player, Component.text("§cUne ville possédant ce nom existe déjà"), Prefix.CITY, MessageType.INFO, false);
                                    return Collections.emptyList();
                                }
                            }

                            if (InputUtils.isInputCityName(input)) {
                                Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                                    CityTypeMenu menu = new CityTypeMenu(player, input);
                                    menu.open();
                                });

                            } else {
                                MessagesManager.sendMessage(player, Component.text("Veuillez mettre une entrée correcte"), Prefix.CITY, MessageType.ERROR, true);
                            }

                            return Collections.emptyList();
                        })
                        .build();
            } catch (SignGUIVersionException e) {
                throw new RuntimeException(e);
            }

            gui.open(player);
        }));

        return inventory;
    }
}
