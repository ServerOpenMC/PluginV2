package fr.openmc.core.features.city.menu;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.template.ConfirmMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.commands.CityInviteCommands;
import fr.openmc.core.items.CustomItemRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
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
    public String getTexture() {
        return "§r§f:offset_-48::city_template6x9:";
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        // empty
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.AIR;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.getStandardSlots(getInventorySize());
    }

    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        Player player = getOwner();
        List<Player> invitations = CityInviteCommands.invitations.get(player);

        List<Component> invitationLore = List.of(
                Component.text("§e§lCLIQUEZ ICI POUR REJOINDRE LA VILLE"));

        for (Player inviter : invitations) {
            City inviterCity = CityManager.getPlayerCity(inviter.getUniqueId());

            if (inviterCity == null) {
                invitations.remove(inviter);
                if (invitations.isEmpty()) {
                    CityInviteCommands.invitations.remove(player);
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
                            CityInviteCommands.acceptInvitation(player, inviter);
                            player.closeInventory();
                        },
                        () -> {
                            CityInviteCommands.denyInvitation(player, inviter);
                            player.closeInventory();
                        },
                        List.of(Component.text("§7Accepter")),
                        List.of(Component.text("§7Refuser" + inviter.getName()))).open();
            }));
        }

        return items;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public int getSizeOfItems() {
        return getItems().size();
    }

    @Override
    public Map<Integer, ItemBuilder> getButtons() {
        Player player = getOwner();
        Map<Integer, ItemBuilder> map = new HashMap<>();
        map.put(49,
                new ItemBuilder(this,
                        Objects.requireNonNull(CustomItemRegistry.getByName("_iainternal:icon_cancel")).getBest(),
                        itemMeta -> itemMeta.displayName(Component.text("§7Retour au menu précédent")), true));
        map.put(48,
                new ItemBuilder(this,
                        Objects.requireNonNull(CustomItemRegistry.getByName("_iainternal:icon_back_orange")).getBest(),
                        itemMeta -> itemMeta.displayName(Component.text("§cPage précédente"))).setPreviousPageButton());
        map.put(50,
                new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("_iainternal:icon_next_orange")).getBest(),
                        itemMeta -> itemMeta.displayName(Component.text("§aPage suivante"))).setNextPageButton());

        return map;
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }
}
