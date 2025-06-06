package fr.openmc.core.features.city.sub.war.menu.main;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.city.CPermission;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.utils.CacheOfflinePlayer;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
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

        List<UUID> sortedMembers = city.getMembers().stream()
                .sorted(Comparator.comparing((UUID uuid) -> !Bukkit.getPlayer(uuid).isOnline())
                        .thenComparing(uuid -> {
                            if (city.hasPermission(uuid, CPermission.OWNER)) return 0;
                            else if (MayorManager.getInstance().cityMayor.get(city).getUUID().equals(uuid))
                                return 1;
                            else return 2;
                        }))
                .toList();

        for (UUID uuid : sortedMembers) {
            OfflinePlayer playerOffline = CacheOfflinePlayer.getOfflinePlayer(uuid);

            boolean hasPermissionOwner = city.hasPermission(uuid, CPermission.OWNER);
            String title;
            if (hasPermissionOwner) {
                title = "Propriétaire ";
            } else if (MayorManager.getInstance().cityMayor.get(city).getUUID() == uuid) {
                title = "Maire ";
            } else {
                title = "Membre ";
            }

            String finalTitle = title;
            items.add(new ItemBuilder(this, ItemUtils.getPlayerSkull(uuid), itemMeta -> itemMeta.displayName(Component.text(finalTitle + playerOffline.getName()).decoration(TextDecoration.ITALIC, false))));
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
        map.put(45, new ItemBuilder(this, Material.ARROW, itemMeta -> {
            itemMeta.itemName(Component.text("§aRetour"));
            itemMeta.lore(List.of(
                    Component.text("§7Vous allez retourner au Menu des Détails de la Ville en guerre"),
                    Component.text("§e§lCLIQUEZ ICI POUR CONFIRMER")
            ));
        }).setOnClick(inventoryClickEvent -> new WarCityDetailsMenu(getOwner(), city).open()));
        map.put(49, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("menu:close_button")).getBest(), itemMeta -> itemMeta.displayName(Component.text("§7Fermer"))).setCloseButton());
        map.put(48, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("menu:previous_page")).getBest(), itemMeta -> itemMeta.displayName(Component.text("§cPage précédente"))).setPreviousPageButton());
        map.put(50, new ItemBuilder(this, Objects.requireNonNull(CustomItemRegistry.getByName("menu:next_page")).getBest(), itemMeta -> itemMeta.displayName(Component.text("§aPage suivante"))).setNextPageButton());
        return map;
    }

    @Override
    public @NotNull String getName() {
        return "Menu de Guerre  - Membres";
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
