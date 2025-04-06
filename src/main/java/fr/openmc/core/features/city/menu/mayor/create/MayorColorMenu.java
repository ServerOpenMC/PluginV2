package fr.openmc.core.features.city.menu.mayor.create;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mayor.ElectionType;
import fr.openmc.core.features.city.mayor.MayorCandidate;
import fr.openmc.core.features.city.mayor.Perks;
import fr.openmc.core.features.city.mayor.managers.MayorManager;
import fr.openmc.core.utils.ColorUtils;
import fr.openmc.core.utils.menu.ConfirmMenu;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MayorColorMenu extends Menu {
    private final String type;
    private final Perks perk1;
    private final Perks perk2;
    private final Perks perk3;
    private final MenuType menuType;

    public MayorColorMenu(Player owner, Perks perk1, Perks perk2, Perks perk3, String type, MenuType menuType) {
        super(owner);
        this.type = type;
        this.perk1 = perk1;
        this.perk2 = perk2;
        this.perk3 = perk3;
        this.menuType = menuType;
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

        try {
            City city = CityManager.getPlayerCity(player.getUniqueId());
            MayorManager mayorManager = MayorManager.getInstance();
            Map<NamedTextColor, Integer> colorSlot = new HashMap<>();
            {
                colorSlot.put(NamedTextColor.RED, 3);
                colorSlot.put(NamedTextColor.GOLD, 4);
                colorSlot.put(NamedTextColor.YELLOW, 5);
                colorSlot.put(NamedTextColor.GREEN, 10);
                colorSlot.put(NamedTextColor.DARK_GREEN, 11);
                colorSlot.put(NamedTextColor.BLUE, 12);
                colorSlot.put(NamedTextColor.AQUA, 13);
                colorSlot.put(NamedTextColor.DARK_BLUE, 14);
                colorSlot.put(NamedTextColor.DARK_PURPLE, 15);
                colorSlot.put(NamedTextColor.LIGHT_PURPLE, 16);
                colorSlot.put(NamedTextColor.WHITE, 21);
                colorSlot.put(NamedTextColor.GRAY, 22);
                colorSlot.put(NamedTextColor.DARK_GRAY, 23);
            }
            colorSlot.forEach((color, slot) -> {
                List<Component> loreColor = List.of(
                        Component.text("§7Votre nom sera affiché en " + ColorUtils.getNameFromColor(color)),
                        Component.text(""),
                        Component.text("§e§lCLIQUEZ ICI POUR CONFIRMER")
                );
                inventory.put(slot, new ItemBuilder(this, ColorUtils.getMaterialFromColor(color), itemMeta -> {
                    itemMeta.displayName(Component.text("§7Mettez du " + ColorUtils.getNameFromColor(color)));
                    itemMeta.lore(loreColor);
                }).setOnClick(inventoryClickEvent -> {
                    if (type == "create") {
                        List<Component> loreAccept = new ArrayList<>(List.of(
                                Component.text("§7Vous allez vous présenter en tant que §6Maire de " + city.getName()),
                                Component.text(""),
                                Component.text("Maire " + player.getName()).color(color).decoration(TextDecoration.ITALIC, false)
                        ));
                        if (perk1 != null) {
                            loreAccept.add(Component.text(perk1.getName()));
                            loreAccept.addAll(perk1.getLore());
                            loreAccept.add(Component.text(""));
                        }
                        loreAccept.add(Component.text(perk2.getName()));
                        loreAccept.addAll(perk2.getLore());
                        loreAccept.add(Component.text(""));
                        loreAccept.add(Component.text(perk3.getName()));
                        loreAccept.addAll(perk3.getLore());
                        loreAccept.add(Component.text(""));
                        loreAccept.add(Component.text("§c§lAUCUN RETOUR EN ARRIERE POSSIBLE!"));


                        ConfirmMenu menu = new ConfirmMenu(player,
                                () -> {
                                    if (menuType == MenuType.CANDIDATE) {
                                        MayorCandidate candidate = new MayorCandidate(city, player.getName(), player.getUniqueId(), color, perk2.getId(), perk3.getId(), 0);
                                        MayorManager.getInstance().createCandidate(city, candidate);
                                        for (UUID uuid : city.getMembers()) {
                                            Player playerMember = Bukkit.getPlayer(uuid);
                                            assert playerMember != null;
                                            if (playerMember == player) continue;
                                            if (playerMember.isOnline()) {
                                                MessagesManager.sendMessage(playerMember, Component.text(player.getName()).color(color).append(Component.text(" §7s'est présenté en tant que §6Maire§7!")), Prefix.MAYOR, MessageType.ERROR, false);
                                            }
                                        }
                                    } else { // donc si c MenuType.OWNER
                                        mayorManager.createMayor(player.getName(), player.getUniqueId(), city, perk1, perk2, perk3, color, mayorManager.getElectionType(city));
                                    }
                                    MessagesManager.sendMessage(player, Component.text("§7Vous vous êtes présenter avec §asuccès§7!"), Prefix.MAYOR, MessageType.ERROR, false);
                                    player.closeInventory();
                                },
                                () -> {
                                    player.closeInventory();
                                },
                                loreAccept,
                                List.of(
                                        Component.text("§7Ne pas se présenter en tant que §6Maire de " + city.getName())
                                )
                        );
                        menu.open();
                    } else if (type == "change") {
                        System.out.println(mayorManager.getElectionType(city));
                        if (mayorManager.getElectionType(city) == ElectionType.OWNER_CHOOSE) {
                            if (city.getMayor() == null) {
                                MessagesManager.sendMessage(player, Component.text("Votre ville n'a pas de maire !"), Prefix.MAYOR, MessageType.ERROR, false);
                                return;
                            }
                            NamedTextColor thisColor = city.getMayor().getMayorColor();
                            ConfirmMenu menu = new ConfirmMenu(player,
                                    () -> {
                                        city.getMayor().setMayorColor(color);
                                        MessagesManager.sendMessage(player, Component.text("§7Vous avez changer votre ").append(Component.text("couleur ").decoration(TextDecoration.ITALIC, false).color(thisColor)).append(Component.text("§7en ")).append(Component.text("celle ci").decoration(TextDecoration.ITALIC, false).color(color)), Prefix.MAYOR, MessageType.SUCCESS, false);
                                        player.closeInventory();
                                    },
                                    () -> {
                                        player.closeInventory();
                                    },
                                    List.of(
                                            Component.text("§7Changer sa ").append(Component.text("couleur ").decoration(TextDecoration.ITALIC, false).color(thisColor)).append(Component.text("§7en ")).append(Component.text("celle ci").decoration(TextDecoration.ITALIC, false).color(color))
                                    ),
                                    List.of(
                                            Component.text("§7Ne pas changer sa ").append(Component.text("couleur ").decoration(TextDecoration.ITALIC, false).color(thisColor)).append(Component.text("§7en ")).append(Component.text("celle ci").decoration(TextDecoration.ITALIC, false).color(color))
                                    )
                            );
                            menu.open();
                        } else {
                            MayorCandidate mayorCandidate = MayorManager.getInstance().getCandidate(player);
                            NamedTextColor thisColor = mayorCandidate.getCandidateColor();
                            ConfirmMenu menu = new ConfirmMenu(player,
                                    () -> {
                                        mayorCandidate.setCandidateColor(color);
                                        MessagesManager.sendMessage(player, Component.text("§7Vous avez changer votre ").append(Component.text("couleur ").decoration(TextDecoration.ITALIC, false).color(thisColor)).append(Component.text("§7en ")).append(Component.text("celle ci").decoration(TextDecoration.ITALIC, false).color(color)), Prefix.CITY, MessageType.SUCCESS, false);
                                        player.closeInventory();
                                    },
                                    () -> {
                                        player.closeInventory();
                                    },
                                    List.of(
                                            Component.text("§7Changer sa ").append(Component.text("couleur ").decoration(TextDecoration.ITALIC, false).color(thisColor)).append(Component.text("§7en ")).append(Component.text("celle ci").decoration(TextDecoration.ITALIC, false).color(color))
                                    ),
                                    List.of(
                                            Component.text("§7Ne pas changer sa ").append(Component.text("couleur ").decoration(TextDecoration.ITALIC, false).color(thisColor)).append(Component.text("§7en ")).append(Component.text("celle ci").decoration(TextDecoration.ITALIC, false).color(color))
                                    )
                            );
                            menu.open();
                        }
                    }
                }));
            });


            return inventory;

        } catch (Exception e) {
            MessagesManager.sendMessage(player, Component.text("§cUne Erreur est survenue, veuillez contacter le Staff"), Prefix.OPENMC, MessageType.ERROR, false);
            player.closeInventory();
            e.printStackTrace();
        }
        return inventory;
    }
}