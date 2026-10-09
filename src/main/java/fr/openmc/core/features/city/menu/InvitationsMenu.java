package fr.openmc.core.features.city.menu;

import fr.openmc.api.omcplayer.OMCOfflinePlayer;
import fr.openmc.api.omcplayer.OMCPlayer;
import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.template.ConfirmMenu;
import fr.openmc.api.menulib.template.ItemMenuTemplate;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.models.CityInvite;
import fr.openmc.core.features.city.models.city.City;
import fr.openmc.core.features.city.commands.CityInviteCommands;
import fr.openmc.core.utils.cache.CachePlayerName;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvitationsMenu extends PaginatedMenu {

    private static final CityManager cityManager = OMCRegistry.FEATURES.CITY.get();
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
        OMCPlayer player = getOwner();

        List<Component> invitationLore = TranslationManager.translationLore("feature.city.menus.invitations.item.lore");

        for (CityInvite invite : cityManager.getInvitations(player.getUniqueId())) {
            City inviterCity = invite.city();

            if (inviterCity == null) {
                cityManager.removeInvitation(player.getUniqueId(), invite.inviterUUID());
                return getItems();
            }

            Component invitationName = TranslationManager.translation(
                    "feature.city.menus.invitations.item.name",
                    CachePlayerName.name(invite.inviterUUID()).color(NamedTextColor.GRAY),
                    Component.text(inviterCity.getName()).color(NamedTextColor.GRAY)
            );

            items.add(new ItemMenuBuilder(this, Material.PAPER, itemMeta -> {
                itemMeta.itemName(invitationName);
                itemMeta.lore(invitationLore);
            }).setOnClick(_ -> {
                OMCOfflinePlayer omcInviter = OMCPlayer.of(invite.inviterUUID());
                new ConfirmMenu(player,
                        () -> {
                            CityInviteCommands.acceptInvitation(player, omcInviter);
                            player.closeInventory();
                        },
                        () -> {
                            CityInviteCommands.denyInvitation(player, omcInviter);
                            player.closeInventory();
                        },
                        List.of(TranslationManager.translation("messages.global.accept")),
                        List.of(TranslationManager.translation("feature.city.menus.invitations.confirm.deny",
                                omcInviter.getNameWithHead()))).open();
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
    public Map<Integer, ItemMenuBuilder> getButtons() {
        Map<Integer, ItemMenuBuilder> map = new HashMap<>();
        map.put(45, new ItemMenuBuilder(this, Material.ARROW, true));

        map.put(49, ItemMenuTemplate.BTN_CANCEL.apply(this));
        map.put(48, ItemMenuTemplate.BTN_PREVIOUS_PAGE_ORANGE.apply(this));
        map.put(50, ItemMenuTemplate.BTN_NEXT_PAGE_ORANGE.apply(this));

        return map;
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }
}
