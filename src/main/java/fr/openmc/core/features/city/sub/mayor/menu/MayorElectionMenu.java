package fr.openmc.core.features.city.sub.mayor.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.menu.CityMenu;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.managers.PerkManager;
import fr.openmc.core.features.city.sub.mayor.menu.create.MayorCreateMenu;
import fr.openmc.core.features.city.sub.mayor.menu.create.MayorModifyMenu;
import fr.openmc.core.features.city.sub.mayor.menu.create.MenuType;
import fr.openmc.core.features.city.sub.mayor.perks.Perks;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.api.ItemsAdderApi;
import fr.openmc.core.utils.api.PapiApi;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

import static fr.openmc.core.features.city.sub.mayor.managers.MayorManager.PHASE_2_DAY;

public class MayorElectionMenu extends Menu {

    public MayorElectionMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        if (PapiApi.hasPAPI() && ItemsAdderApi.hasItemAdder()) {
            return PlaceholderAPI.setPlaceholders(getOwner(), "§r§f%img_offset_-38%%img_mayor%");
        } else {
            return "Menu des Elections";
        }
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
    public @NotNull Map<Integer, ItemStack> getContent() {
        Map<Integer, ItemStack> inventory = new HashMap<>();
        Player player = getOwner();

        City city = CityManager.getPlayerCity(player.getUniqueId());

        boolean hasPermissionOwner = city.hasPermission(player.getUniqueId(), CPermission.OWNER);

        Supplier<ItemStack> electionItemSupplier = () -> {
            List<Component> loreElection;
            if (MayorManager.hasVoted(player)) {
                loreElection = List.of(
                        Component.text("§7Les Elections sont §6ouvertes§7!"),
                        Component.text("§7Vous pouvez changer votre vote !"),
                        Component.empty(),
                        Component.text("§7Vote Actuel : ").append(
                                        Component.text(MayorManager.getPlayerVote(player).getName()))
                                .decoration(TextDecoration.ITALIC, false)
                                .color(MayorManager.getPlayerVote(player).getCandidateColor()),
                        Component.text("§cFermeture dans " + DateUtils.getTimeUntilNextDay(PHASE_2_DAY)),
                        Component.empty(),
                        Component.text("§e§lCLIQUEZ ICI POUR ACCEDER AU MENU")
                );
            } else {
                loreElection = List.of(
                        Component.text("§7Les Elections sont §6ouvertes§7!"),
                        Component.text("§7Choissiez le Maire qui vous plait !"),
                        Component.empty(),
                        Component.text("§cFermeture dans " + DateUtils.getTimeUntilNextDay(PHASE_2_DAY)),
                        Component.empty(),
                        Component.text("§e§lCLIQUEZ ICI POUR CHOISIR")
                );
            }

            return new ItemBuilder(this, Material.JUKEBOX, itemMeta -> {
                itemMeta.itemName(Component.text("§6Les Elections"));
                itemMeta.lore(loreElection);
            }).setOnClick(inventoryClickEvent -> {
                if (MayorManager.cityElections.get(city.getUUID()) == null) {
                    MessagesManager.sendMessage(player, Component.text("Il y a aucun volontaire pour être maire"), Prefix.MAYOR, MessageType.ERROR, true);
                    return;
                }
                new MayorVoteMenu(player).open();
            });
        };

        MenuUtils.runDynamicItem(player, this, 29, electionItemSupplier)
                .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L);

        List<Component> loreCandidature;
        if (MayorManager.hasCandidated(player)) {
            loreCandidature = List.of(
                    Component.text("§7Vous vous êtes déjà §3présenter §7!"),
                    Component.text("§7Modifier votre couleur et regardez §3les Réformes §7que vous avez choisis"),
                    Component.empty(),
                    Component.text("§e§lCLIQUEZ ICI POUR ACCEDER AU MENU")
            );
        } else {
            loreCandidature = List.of(
                    Component.text("§7Vous pouvez vous §3inscire §7afin d'être maire !"),
                    Component.text("§7Séléctionner §3vos Réformes §7et votre couleur !"),
                    Component.empty(),
                    Component.text("§e§lCLIQUEZ ICI POUR VOUS INSCRIRE")
            );
        }

        if (hasPermissionOwner) {
            List<Component> lorePerkOwner;
            if (MayorManager.hasChoicePerkOwner(player)) {
                Perks perk1 = PerkManager.getPerkById(city.getMayor().getIdPerk1());
                lorePerkOwner = new ArrayList<>(List.of(
                        Component.text("§7Vous avez déjà choisis §3votre Réforme §7!"),
                        Component.empty(),
                        Component.text(perk1.getName())
                ));
                lorePerkOwner.addAll(perk1.getLore());
            } else {
                lorePerkOwner = List.of(
                        Component.text("§7Vous êtes le propriétaire de la §dVille§7!"),
                        Component.text("§7Vous pouvez choisir une §3Réforme événementiel §7!"),
                        Component.empty(),
                        Component.text("§e§lCLIQUEZ ICI POUR CHOISIR LA REFORME")
                );
            }

            inventory.put(22, new ItemBuilder(this, ItemUtils.getPlayerSkull(player.getUniqueId()), itemMeta -> {
                itemMeta.displayName(Component.text("§7Choix d'une §3Réforme"));
                itemMeta.lore(lorePerkOwner);
            }).setOnClick(inventoryClickEvent -> {
                if (!MayorManager.hasChoicePerkOwner(player)) {
                    Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            new MayorCreateMenu(player, null, null, null, MenuType.OWNER_1).open();
                        }
                    });
                }
            }));
        }

        inventory.put(33, new ItemBuilder(this, Material.PAPER, itemMeta -> {
            itemMeta.itemName(Component.text("§7Votre §3Candidature"));
            itemMeta.lore(loreCandidature);
        }).setOnClick(inventoryClickEvent -> {
            if (MayorManager.hasCandidated(player)) {
                new MayorModifyMenu(player).open();
            } else {
                new MayorCreateMenu(player, null, null, null, MenuType.CANDIDATE).open();
            }
        }));

        inventory.put(46, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.itemName(Component.text("§aRetour"));
            itemMeta.lore(List.of(
                    Component.text("§7Vous allez retourner au Menu de votre ville"),
                    Component.text("§e§lCLIQUEZ ICI POUR CONFIRMER")
            ));
        }).setOnClick(inventoryClickEvent -> {
            CityMenu menu = new CityMenu(player);
            menu.open();
        }));


        List<Component> loreInfo = Arrays.asList(
                Component.text("§7Apprenez en plus sur les Maires !"),
                Component.text("§7Le déroulement..., Les éléctions, ..."),
                Component.text("§e§lCLIQUEZ ICI POUR EN VOIR PLUS!")
        );

        inventory.put(52, new ItemBuilder(this, Material.BOOK, itemMeta -> {
            itemMeta.displayName(Component.text("§r§aPlus d'info !"));
            itemMeta.lore(loreInfo);
        }).setNextMenu(new MoreInfoMenu(getOwner())));

        return inventory;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
