package fr.openmc.core.features.city.menu.mayor;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mayor.perks.Perks;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.features.city.mayor.managers.PerkManager;
import fr.openmc.core.features.city.menu.CityMenu;
import fr.openmc.core.features.city.menu.mayor.create.MayorCreateMenu;
import fr.openmc.core.features.city.menu.mayor.create.MayorModifyMenu;
import fr.openmc.core.features.city.menu.mayor.create.MenuType;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.PlayerUtils;
import fr.openmc.core.utils.menu.MenuUtils;
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
import java.util.function.Supplier;

import static fr.openmc.core.features.city.mayor.managers.MayorManager.PHASE_2_DAY;

public class MayorElectionMenu extends Menu {

    public MayorElectionMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        return "Menu des Elections";
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

        try {
            City city = CityManager.getPlayerCity(player.getUniqueId());
            MayorManager mayorManager = MayorManager.getInstance();
            boolean hasPermissionOwner = city.hasPermission(player.getUniqueId(), CPermission.OWNER);

            Supplier<ItemStack> electionItemSupplier = () -> {
                List<Component> loreElection;
                if (mayorManager.hasVoted(player)) {
                    loreElection = List.of(
                            Component.text("§7Les Elections sont §6ouvertes§7!"),
                            Component.text("§7Vous pouvez changer votre vote !"),
                            Component.text(""),
                            Component.text("§7Vote Actuel : ").append(Component.text(mayorManager.getPlayerVote(player).getName())).decoration(TextDecoration.ITALIC, false).color(mayorManager.getPlayerVote(player).getCandidateColor()),
                            Component.text("§cFermeture dans " + DateUtils.getTimeUntilNextDay(PHASE_2_DAY)),
                            Component.text(""),
                            Component.text("§e§lCLIQUEZ ICI POUR ACCEDER AU MENU")
                    );
                } else {
                    loreElection = List.of(
                            Component.text("§7Les Elections sont §6ouvertes§7!"),
                            Component.text("§7Choissiez le Maire qui vous plait !"),
                            Component.text(""),
                            Component.text("§cFermeture dans " + DateUtils.getTimeUntilNextDay(PHASE_2_DAY)),
                            Component.text(""),
                            Component.text("§e§lCLIQUEZ ICI POUR CHOISIR")
                    );
                }

                return new ItemBuilder(this, Material.JUKEBOX, itemMeta -> {
                    itemMeta.itemName(Component.text("§6Les Elections"));
                    itemMeta.lore(loreElection);
                }).setOnClick(inventoryClickEvent -> {
                    if (mayorManager.cityElections.get(city) == null) {
                        MessagesManager.sendMessage(player, Component.text("Il y a aucun volontaire pour être maire"), Prefix.MAYOR, MessageType.ERROR, true);
                        return;
                    }
                    new MayorVoteMenu(player).open();
                });
            };

            MenuUtils.runDynamicItem(player, this, 11, electionItemSupplier)
                    .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L);

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
                List<Component> lorePerkOwner;
                if (mayorManager.hasChoicePerkOwner(player)) {
                    Perks perk1 = PerkManager.getPerkById(city.getMayor().getIdPerk1());
                    lorePerkOwner = new ArrayList<>(List.of(
                            Component.text("§7Vous avez déjà choisis §3votre Réforme §7!"),
                            Component.text(""),
                            Component.text(perk1.getName())
                    ));
                    lorePerkOwner.addAll(perk1.getLore());
                } else {
                    lorePerkOwner = List.of(
                            Component.text("§7Vous êtes le propriétaire de la §dVille§7!"),
                            Component.text("§7Vous pouvez choisir une §3Réforme événementiel §7!"),
                            Component.text(""),
                            Component.text("§e§lCLIQUEZ ICI POUR CHOISIR LA REFORME")
                    );
                }

                inventory.put(13, new ItemBuilder(this, PlayerUtils.getPlayerSkull(player), itemMeta -> {
                    itemMeta.displayName(Component.text("§7Choix d'une §3Réforme"));
                    itemMeta.lore(lorePerkOwner);
                }).setOnClick(inventoryClickEvent -> {
                    if (!mayorManager.hasChoicePerkOwner(player)) {
                        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                new MayorCreateMenu(player, null, null, null, MenuType.OWNER_1).open();
                            }
                        });
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
                   new MayorCreateMenu(player, null, null, null, MenuType.CANDIDATE).open();
                }
            }));

            inventory.put(18, new ItemBuilder(this, Material.ARROW, itemMeta -> {
                itemMeta.itemName(Component.text("§aRetour"));
                itemMeta.lore(List.of(
                        Component.text("§7Vous allez retourner au Menu de votre ville"),
                        Component.text("§e§lCLIQUEZ ICI POUR CONFIRMER")
                ));
            }).setOnClick(inventoryClickEvent -> {
                CityMenu menu = new CityMenu(player);
                menu.open();
            }));


            return inventory;

        } catch (Exception e) {
            MessagesManager.sendMessage(player, Component.text("§cUne Erreur est survenue, veuillez contacter le Staff"), Prefix.OPENMC, MessageType.ERROR, false);
            player.closeInventory();
            e.printStackTrace();
        }
        return inventory;
    }
}
