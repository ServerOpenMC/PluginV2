package fr.openmc.core.features.city.menu.list;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.utils.CacheOfflinePlayer;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
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

public class CityPlayerListMenu extends PaginatedMenu {

    private final City city;

    public CityPlayerListMenu(Player owner, City city) {
        super(owner);
        this.city = city;
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

        try {
            for (UUID uuid : city.getMembers()) {
                OfflinePlayer playerOffline = CacheOfflinePlayer.getOfflinePlayer(uuid);

                boolean hasPermissionOwner = city.hasPermission(uuid, CPermission.OWNER);
                String title = "";
                if (hasPermissionOwner) {
                    title = "Propriétaire ";
                } else if (MayorManager.cityMayor.get(city.getUUID()).getUUID() == uuid) {
                    title = "Maire ";
                } else {
                    title = "Membre ";
                }

                String finalTitle = title;
                items.add(new ItemBuilder(this, ItemUtils.getPlayerSkull(uuid), itemMeta -> {
                    itemMeta.displayName(Component.text(finalTitle + playerOffline.getName()).decoration(TextDecoration.ITALIC, false));
                }));
            }
            return items;
        } catch (Exception e) {
            MessagesManager.sendMessage(player, Component.text("§cUne Erreur est survenue, veuillez contacter le Staff"), Prefix.OPENMC, MessageType.ERROR, false);
            player.closeInventory();
            e.printStackTrace();
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
        map.put(49, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("menu:close_button")).getBest(), itemMeta -> itemMeta.displayName(Component.text("§7Fermer"))).setCloseButton());
        map.put(48, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("menu:previous_page")).getBest(), itemMeta -> itemMeta.displayName(Component.text("§cPage précédente"))).setPreviousPageButton());
        map.put(50, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("menu:next_page")).getBest(), itemMeta -> itemMeta.displayName(Component.text("§aPage suivante"))).setNextPageButton());
        return map;
    }

    @Override
    public @NotNull String getName() {
        return "Détails des membres de " + city.getName();
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
