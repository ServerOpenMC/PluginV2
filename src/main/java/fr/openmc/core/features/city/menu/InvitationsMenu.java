package fr.openmc.core.features.city.menu;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.commands.CityCommands;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import fr.openmc.core.utils.menu.ConfirmMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class InvitationsMenu extends PaginatedMenu {

    public InvitationsMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull String getName() {
        return "Menu des villes - Invitations";
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        // empty
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.GRAY_STAINED_GLASS_PANE;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.STANDARD;
    }

    @Override
    public @NotNull List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        Player player = getOwner();
        List<Player> invitations = CityCommands.invitations.get(player);

        List<Component> invitationLore = List.of(
                Component.text("§e§lCLIQUEZ ICI POUR REJOINDRE LA VILLE"));

        for (Player inviter : invitations) {
            City inviterCity = CityManager.getPlayerCity(inviter.getUniqueId());

            if (inviterCity == null) {
                invitations.remove(inviter);
                if (invitations.size() == 0) {
                    CityCommands.invitations.remove(player);
                }
                return getItems();
            }

            Component invitationName = Component.text("§7" + inviter.getName() + " vous a invité(e) dans " + inviterCity.getName());
            
            items.add(new ItemBuilder(this, Material.PAPER, itemMeta -> {
                itemMeta.itemName(invitationName);
                itemMeta.lore(invitationLore);
            }).setOnClick(InventoryClickEvent -> {
                new ConfirmMenu(player,
                        () -> {
                            CityCommands.acceptInvitation(player, inviter);
                            player.closeInventory();
                        },
                        () -> {
                            CityCommands.denyInvitation(player, inviter);
                            player.closeInventory();
                        },
                        List.of(Component.text("§7Accepter")),
                        List.of(Component.text("§7Refuser" + inviter.getName()))).open();
            }));
        }

        return items;
    }

    @Override
    public Map<Integer, ItemStack> getButtons() {
        Player player = getOwner();
        Map<Integer, ItemStack> map = new HashMap<>();
        map.put(49,
                new ItemBuilder(this,
                        Objects.requireNonNull(CustomItemRegistry.getByName("menu:close_button")).getBest(),
                        itemMeta -> itemMeta.displayName(Component.text("§7Retour au menu des villes")))
                        .setOnClick(InventoryClickEvent -> new NoCityMenu(player).open()));
        map.put(48,
                new ItemBuilder(this,
                        Objects.requireNonNull(CustomItemRegistry.getByName("menu:previous_page")).getBest(),
                        itemMeta -> itemMeta.displayName(Component.text("§cPage précédente"))).setPreviousPageButton());
        map.put(50,
                new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("menu:next_page")).getBest(),
                        itemMeta -> itemMeta.displayName(Component.text("§aPage suivante"))).setNextPageButton());

        return map;
    }
}
