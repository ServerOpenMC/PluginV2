package fr.openmc.core.features.city.menu;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.features.city.actions.CityTransferAction;
import fr.openmc.core.utils.CacheOfflinePlayer;
import fr.openmc.core.items.CustomItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CityTransferMenu extends PaginatedMenu {

    public CityTransferMenu(Player owner) {
        super(owner);
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.LIGHT_GRAY_STAINED_GLASS_PANE;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.STANDARD;
    }

    @Override
    public @NotNull List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        Player player = getOwner();

        City city = CityManager.getPlayerCity(player.getUniqueId());
        assert city != null;

        boolean hasPermissionOwner = city.hasPermission(player.getUniqueId(), CPermission.OWNER);

            for (UUID uuid : city.getMembers()) {
                if (uuid.equals(city.getPlayerWithPermission(CPermission.OWNER))) {
                    continue;
                }

            OfflinePlayer playerOffline = CacheOfflinePlayer.getOfflinePlayer(uuid);

            items.add(new ItemBuilder(this, ItemUtils.getPlayerSkull(uuid), itemMeta -> {
                itemMeta.displayName(Component.text("Membre " + playerOffline.getName()).decoration(TextDecoration.ITALIC, false));
                itemMeta.lore(List.of(
                        Component.text("§7Voulez-vous donner à §d" + playerOffline.getName() + " §7votre ville ?"),
                        Component.text("§e§lCLIQUEZ ICI POUR CONFIRMER")
                ));
            }).setOnClick(inventoryClickEvent -> {
                if (!hasPermissionOwner) {
                    MessagesManager.sendMessage(player, MessagesManager.Message.PLAYERNOOWNER.getMessage(), Prefix.CITY, MessageType.ERROR, false);
                    return;
                }

                CityTransferAction.transfer(player, city, playerOffline);
            }));
        }

        return items;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    @Override
    public Map<Integer, ItemStack> getButtons() {
        Map<Integer, ItemStack> map = new HashMap<>();
        map.put(49, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_cancel").getBest(), itemMeta -> {
            itemMeta.displayName(Component.text("§7Fermer"));
        }).setCloseButton());
        map.put(48, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_back_orange").getBest(), itemMeta -> {
            itemMeta.displayName(Component.text("§cPage précédente"));
        }).setPreviousPageButton());
        map.put(50, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_next_orange").getBest(), itemMeta -> {
            itemMeta.displayName(Component.text("§aPage suivante"));
        }).setNextPageButton());
        return map;
    }

    @Override
    public @NotNull String getName() {
        return "Menu des Villes - Transferer";
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        //empty
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }
}
