package fr.openmc.core.features.city.sub.war.menu.main;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityPermission;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.SkullUtils;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WarPlayerListMenu extends PaginatedMenu {

    private final City city;

    public WarPlayerListMenu(Player owner, City city) {
        super(owner);
        this.city = city;
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

        List<UUID> sortedMembers = city.getMembers().stream()
                .sorted(Comparator.comparing((UUID uuid) -> !Bukkit.getPlayer(uuid).isOnline())
                        .thenComparing(uuid -> {
                            if (city.hasPermission(uuid, CityPermission.OWNER)) return 0;
                            else if (MayorManager.cityMayor.get(city.getUniqueId()).getMayorUUID().equals(uuid))
                                return 1;
                            else return 2;
                        }))
                .toList();

        for (UUID memberUUID : sortedMembers) {
            OfflinePlayer playerOffline = CacheOfflinePlayer.getOfflinePlayer(memberUUID);

            boolean hasPermissionOwner = city.hasPermission(memberUUID, CityPermission.OWNER);
            String title;
            if (hasPermissionOwner) {
                title = "Propriétaire ";
            } else if (MayorManager.cityMayor.get(city.getUniqueId()).getMayorUUID().equals(memberUUID)) {
                title = "Maire ";
            } else {
                title = "Membre ";
            }

            String finalTitle = title;
            items.add(new ItemBuilder(this, SkullUtils.getPlayerSkull(memberUUID), itemMeta -> itemMeta.displayName(Component.text(finalTitle + playerOffline.getName()).decoration(TextDecoration.ITALIC, false))));
        }

        return items;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }

    @Override
    public Map<Integer, ItemBuilder> getButtons() {
        Map<Integer, ItemBuilder> map = new HashMap<>();
        map.put(45, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.itemName(Component.text("§aRetour"));
            itemMeta.lore(List.of(
		            Component.text("§7Vous allez retourner au menu des détails de la ville en guerre"),
                    Component.text("§e§lCLIQUEZ ICI POUR CONFIRMER")
            ));
        }, true));
        map.put(49, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_cancel").getBest(), itemMeta -> itemMeta.displayName(Component.text("§7Fermer"))).setCloseButton());
        map.put(48, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_back_orange").getBest(), itemMeta -> itemMeta.displayName(Component.text("§cPage précédente"))).setPreviousPageButton());
        map.put(50, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_next_orange").getBest(), itemMeta -> itemMeta.displayName(Component.text("§aPage suivante"))).setNextPageButton());
        return map;
    }

    @Override
    public @NotNull String getName() {
        return "Menu de Guerre - Membres";
    }

    @Override
    public String getTexture() {
        return "§r§f:offset_-48::city_template6x9:";
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
