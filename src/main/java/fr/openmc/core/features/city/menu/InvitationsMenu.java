package fr.openmc.core.features.city.menu;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.template.ConfirmMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.commands.CityInviteCommands;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
    public @NotNull Component getName() {
	    return TranslationManager.translation("feature.city.menus.invitations.name");
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

        List<Component> invitationLore = TranslationManager.translationLore("feature.city.menus.invitations.item.lore");

        for (Player inviter : invitations) {
            City inviterCity = CityManager.getPlayerCity(inviter.getUniqueId());

            if (inviterCity == null) {
                invitations.remove(inviter);
                if (invitations.isEmpty()) {
                    CityInviteCommands.invitations.remove(player);
                }
                return getItems();
            }

            Component invitationName = TranslationManager.translation(
                    "feature.city.menus.invitations.item.name",
                    Component.text(inviter.getName()).color(NamedTextColor.GRAY),
                    Component.text(inviterCity.getName()).color(NamedTextColor.GRAY)
            );

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
                        List.of(TranslationManager.translation("messages.global.accept")),
                        List.of(TranslationManager.translation("feature.city.menus.invitations.confirm.deny", Component.text(inviter.getName()).color(NamedTextColor.GRAY)))).open();
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
        Map<Integer, ItemBuilder> map = new HashMap<>();
        map.put(45, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("messages.menus.back"));
            itemMeta.lore(List.of(TranslationManager.translation("messages.menus.back_lore")));
        }, true));

        map.put(49, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_cancel").getBest(), itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("messages.menus.close"));
        }).setOnClick(inventoryClickEvent ->
                getOwner().closeInventory()
        ));

        map.put(48,
                new ItemBuilder(this,
                        Objects.requireNonNull(CustomItemRegistry.getByName("_iainternal:icon_back_orange")).getBest(),
                        itemMeta -> itemMeta.displayName(TranslationManager.translation("messages.menus.previous_page"))).setPreviousPageButton());
        map.put(50,
                new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("_iainternal:icon_next_orange")).getBest(),
                        itemMeta -> itemMeta.displayName(TranslationManager.translation("messages.menus.next_page"))).setNextPageButton());

        return map;
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }
}
