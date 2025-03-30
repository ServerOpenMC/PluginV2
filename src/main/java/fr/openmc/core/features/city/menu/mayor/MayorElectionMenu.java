package fr.openmc.core.features.city.menu.mayor;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.features.city.menu.CityMenu;
import fr.openmc.core.features.city.menu.bank.CityBankMenu;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.PlayerUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.util.*;

public class MayorElectionMenu extends Menu {

    public MayorElectionMenu(Player owner) {
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
        City city = CityManager.getPlayerCity(player.getUniqueId());
        MayorManager mayorManager = MayorManager.getInstance();
        boolean hasPermissionOwner = city.hasPermission(player.getUniqueId(), CPermission.OWNER);

        List<Component> loreElection;
        if (mayorManager.hasVoted(player)) {
            loreElection = List.of(
                    Component.text("§7Les Elections sont §6ouvertes§7!"),
                    Component.text("§7Vous pouvez changer votre vote !"),
                    Component.text(""),
                    Component.text("§7Vote Actuel : ").append(Component.text(mayorManager.getPlayerVote(player).getName())).decoration(TextDecoration.ITALIC, false).color(mayorManager.getPlayerVote(player).getCandidateColor()),
                    Component.text("§cFermeture dans " + DateUtils.getTimeUntilNextDay(DayOfWeek.THURSDAY)),
                    Component.text(""),
                    Component.text("§e§lCLIQUEZ ICI POUR ACCEDER AU MENU")
            );
        } else {
            loreElection = List.of(
                    Component.text("§7Les Elections sont §6ouvertes§7!"),
                    Component.text("§7Choissiez le Maire qui vous plait !"),
                    Component.text(""),
                    Component.text("§cFermeture dans " + DateUtils.getTimeUntilNextDay(DayOfWeek.THURSDAY)),
                    Component.text(""),
                    Component.text("§e§lCLIQUEZ ICI POUR CHOISIR")
            );
        }

        inventory.put(11, new ItemBuilder(this, Material.JUKEBOX, itemMeta -> {
            itemMeta.itemName(Component.text("§6Les Elections"));
            itemMeta.lore(loreElection);
        }).setOnClick(inventoryClickEvent -> {
            if (mayorManager.cityElections.get(city) == null) {
                MessagesManager.sendMessage(player, Component.text("Il y a aucun volontaire pour être maire"), Prefix.CITY, MessageType.ERROR, true);
            }
            new MayorVoteMenu(player).open();
        }));

        List<Component> loreCandidature;
        if (mayorManager.hasCandidated(player)) {
            loreCandidature = List.of(
                    Component.text("§7Vous vous êtes déjà §3présenter §7!"),
                    Component.text("§7Modifier votre couleur et regardez §3les Réformes §7que vous avez choisis"),
                    Component.text(""),
                    Component.text("§e§lCLIQUEZ ICI POUR ACCEDER AU MENU")
            );
        } else {
            loreCandidature = List.of(
                    Component.text("§7Vous pouvez vous §3inscire §7afin d'être maire !"),
                    Component.text("§7Séléctionner §3vos Réformes §7et votre couleur !"),
                    Component.text(""),
                    Component.text("§e§lCLIQUEZ ICI POUR VOUS INSCRIRE")
            );
        }

        if (hasPermissionOwner) {
            inventory.put(13, new ItemBuilder(this, PlayerUtils.getPlayerSkull(player), itemMeta -> {
                itemMeta.itemName(Component.text("§7Votre §3Candidature"));
                itemMeta.lore(loreCandidature);
            }).setOnClick(inventoryClickEvent -> {
                if (mayorManager.hasCandidated(player)) {
                    new MayorModifyMenu(player).open();
                } else {
                    new MayorCreateMenu(player, null, null).open();
                }
            }));
        }

        inventory.put(15, new ItemBuilder(this, Material.PAPER, itemMeta -> {
            itemMeta.itemName(Component.text("§7Votre §3Candidature"));
            itemMeta.lore(loreCandidature);
        }).setOnClick(inventoryClickEvent -> {
            if (mayorManager.hasCandidated(player)) {
                new MayorModifyMenu(player).open();
            } else {
               new MayorCreateMenu(player, null, null).open();
            }
        }));

        inventory.put(18, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.itemName(Component.text("§aRetour"));
            itemMeta.lore(List.of(
                    Component.text("§7Vous allez retourner au Menu de la Banque de votre ville"),
                    Component.text("§e§lCLIQUEZ ICI POUR CONFIRMER")
            ));
        }).setOnClick(inventoryClickEvent -> {
            CityMenu menu = new CityMenu(player);
            menu.open();
        }));


        return inventory;
    }
}
