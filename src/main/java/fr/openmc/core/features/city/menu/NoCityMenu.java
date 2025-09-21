package fr.openmc.core.features.city.menu;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.api.input.DialogInput;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.MenuUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.actions.CityCreateAction;
import fr.openmc.core.features.city.commands.CityCommands;
import fr.openmc.core.features.city.conditions.CityCreateConditions;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static fr.openmc.core.utils.InputUtils.MAX_LENGTH_CITY;

public class NoCityMenu extends Menu {

    public NoCityMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        return "Menu des Villes - Aucune";
    }

    @Override
    public String getTexture() {
        return null;
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
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> inventory = new HashMap<>();
        Player player = getOwner();


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

        Supplier<ItemBuilder> createItemSupplier = () -> {
                List<Component> loreCreate;
                if (!DynamicCooldownManager.isReady(player.getUniqueId(), "city:big")) {
                    loreCreate = List.of(
                            Component.text("§7Vous pouvez aussi créer §dvotre Ville"),
                            Component.text("§7Faites §d/city create <name> §7ou bien cliquez ici !"),
                            Component.empty(),
                            Component.text("§7Vous devez attendre §c" + DateUtils.convertMillisToTime(DynamicCooldownManager.getRemaining(player.getUniqueId(), "city:big")) + " §7avant de pouvoir créer une ville")
                    );
                } else {
                    loreCreate = List.of(
                            Component.text("§7Vous pouvez aussi créer §dvotre Ville"),
                            Component.text("§7Faites §d/city create <name> §7ou bien cliquez ici !"),
                            Component.empty(),
                            Component.text("§cCoûte :"),
                            Component.text("§8- §6" + CityCreateConditions.MONEY_CREATE + EconomyManager.getEconomyIcon()).decoration(TextDecoration.ITALIC, false),
                            Component.text("§8- §d" + CityCreateConditions.AYWENITE_CREATE + " d'Aywenite"),
                            Component.empty(),
                            Component.text("§e§lCLIQUEZ ICI POUR CREER VOTRE VILLE")
                    );
                }

                return new ItemBuilder(this, Material.SCAFFOLDING, itemMeta -> {
                    itemMeta.itemName(Component.text("§7Créer §dvotre ville"));
                    itemMeta.lore(loreCreate);
                }).setOnClick(inventoryClickEvent -> {
                    if (!DynamicCooldownManager.isReady(player.getUniqueId(), "city:big")) return;

                    DialogInput.send(player, Component.text("Entrez le nom de la ville"), MAX_LENGTH_CITY, input -> {
                                if (input == null) return;
                                CityCreateAction.beginCreateCity(player, input);
                            }
                    );
                });
            };

            if (!DynamicCooldownManager.isReady(player.getUniqueId(), "city:big")) {
                MenuUtils.runDynamicItem(player, this, 11, createItemSupplier)
                        .runTaskTimer(OMCPlugin.getInstance(), 0L, 20L);
            } else {
                inventory.put(11, createItemSupplier.get());
            }


        return inventory;
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
