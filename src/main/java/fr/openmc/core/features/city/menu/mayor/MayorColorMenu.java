package fr.openmc.core.features.city.menu.mayor;

import dev.xernas.menulib.Menu;
import dev.xernas.menulib.utils.InventorySize;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.mayor.MayorElector;
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
    private final Perks perk2;
    private final Perks perk3;

    public MayorColorMenu(Player owner, Perks perk2, Perks perk3, String type) {
        super(owner);
        this.type = type;
        this.perk2 = perk2;
        this.perk3 = perk3;
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
                    loreAccept.add(Component.text(perk2.getName()));
                    loreAccept.addAll(perk2.getLore());
                    loreAccept.add(Component.text(""));
                    loreAccept.add(Component.text(perk3.getName()));
                    loreAccept.addAll(perk3.getLore());
                    loreAccept.add(Component.text(""));
                    loreAccept.add(Component.text("§c§lAUCUN RETOUR EN ARRIERE POSSIBLE!"));


                    ConfirmMenu menu = new ConfirmMenu(player,
                            () -> {
                                MayorElector elector = new MayorElector(city, player.getName(), player.getUniqueId().toString(), color, perk2.getId(), perk3.getId(), 0);
                                MayorManager.getInstance().createElector(city, elector);
                                MessagesManager.sendMessage(player, Component.text("§7Vous vous êtes présenter avec §asuccès§7!"), Prefix.CITY, MessageType.ERROR, false);
                                for (UUID uuid : city.getMembers()) {
                                    Player playerMember = Bukkit.getPlayer(uuid);
                                    assert playerMember != null;
                                    if (playerMember == player) continue;
                                    if (playerMember.isOnline()) {
                                        MessagesManager.sendMessage(playerMember, Component.text(player.getName()).color(color).append(Component.text(" §7s'est présenté en tant que §6Maire§7!")), Prefix.CITY, MessageType.ERROR, false);
                                    }
                                }
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
                    MayorElector mayorElector = MayorManager.getInstance().getElector(player);
                    NamedTextColor thisColor = mayorElector.getElectorColor();
                    ConfirmMenu menu = new ConfirmMenu(player,
                            () -> {
                                mayorElector.setElectorColor(color);
                                MessagesManager.sendMessage(player, Component.text("§7Vous avez changer votre ").append(Component.text("couleur ").color(thisColor)).append(Component.text("§7en ")).append(Component.text("celle ci").color(color)), Prefix.CITY, MessageType.ERROR, false);
                                player.closeInventory();
                            },
                            () -> {
                                player.closeInventory();
                            },
                            List.of(
                                    Component.text("§7Changer sa ").append(Component.text("couleur ").color(thisColor)).append(Component.text("§7en ")).append(Component.text("celle ci").color(color))
                            ),
                            List.of(
                                    Component.text("§7Ne pas changer sa ").append(Component.text("couleur ").color(thisColor)).append(Component.text("§7en ")).append(Component.text("celle ci").color(color))
                            )
                    );
                    menu.open();
                }
            }));
        });


        return inventory;
    }
}